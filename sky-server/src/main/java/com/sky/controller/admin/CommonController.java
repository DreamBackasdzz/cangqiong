package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("上传文件图片")
    public Result<String> upload(MultipartFile file){
        String fileOriginalFilename = file.getOriginalFilename();
        String extend = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf("."));
        String objName = UUID.randomUUID().toString() + extend;
        try {
            String objPath = aliOssUtil.upload(file.getBytes(), objName);
            return Result.success(objPath);
        } catch (Exception e) {
            log.error("文件名异常");
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
