package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation("添加菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("获取的请求对象:{}",dishDTO);
        dishService.save(dishDTO);
        clearCache("dish_*");
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
        clearCache("dish_*");
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("更新菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        clearCache("dish_"+dishDTO.getCategoryId());
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品启售停售卖")
    public Result startOrStop(@PathVariable Integer status ,Long id){
        dishService.startOrStop(status,id);
        clearCache("dish_*");
        return Result.success();

    }
    @GetMapping("list")
    @ApiOperation("根据分类id获取菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> dishes = dishService.list(categoryId);
        return Result.success(dishes);
    }
    //清理缓存方法
    private void clearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
