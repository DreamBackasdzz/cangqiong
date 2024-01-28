package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //需要返回一个TurnoverReportVO对象
        //封装datelist数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
             begin = begin.plusDays(1);
             dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        //封装turnoverlist数据
        for (LocalDate date : dateList) {
            LocalDateTime localBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime localEnd = LocalDateTime.of(date, LocalTime.MAX);
            //查询order表中的每一天的营业额
            //select sum(amount) from orders where order_time >= localBegin and order_time <= localEnd and status = 5
            Map map = new HashMap();
            map.put("begin",localBegin);
            map.put("end",localEnd);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        //返回turnoverVO对象
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //封装datelist集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //封装newUserList
        //select count(id) from user where create_time >= ? and create_time <= ?
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localEnd = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localBegin = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap<>();
            //查询全部用户
            map.put("end",localEnd);
            Integer totalUser = userMapper.getByMap(map);
            totalUserList.add(totalUser);
            map.put("begin",localBegin);
            //查询新增用户
            Integer newUser = userMapper.getByMap(map);
            newUserList.add(newUser);
        }
        //封装totalUserList
        //select count(id) from user where create_time <= ?
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }
}
