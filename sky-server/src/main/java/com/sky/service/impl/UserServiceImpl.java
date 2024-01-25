package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.UserNotLoginException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String GRANT_TYPE = "authorization_code";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //请求微信aip服务器获取openid
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",weChatProperties.getAppid());
        paramMap.put("secret",weChatProperties.getSecret());
        paramMap.put("js_code",userLoginDTO.getCode());
        paramMap.put("grant_type",GRANT_TYPE);
        String response = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", paramMap);
        //将响应回来的字符串转化为json对象
        JSONObject jsonObject = JSON.parseObject(response);
        //将json对象中key为openid的值拿到
        String openid = (String) jsonObject.get("openid");
        if(openid == null || openid.isEmpty()){
            throw  new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }

        //判断是否为新用户若是新用户则存入响应的数据库
        User user = userMapper.getByOpenid(openid);
        if(user == null )
        {
             user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户
        return user;
    }
}
