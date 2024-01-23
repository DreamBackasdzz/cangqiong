package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @ApiOperation("添加菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("获取的请求对象:{}",dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> dishQuery(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.Query(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        return Result.success();
    }
}
