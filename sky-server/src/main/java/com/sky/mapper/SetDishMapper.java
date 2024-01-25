package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetDishMapper {

    List<Long> getByDishId(List<Long> dishIDs);
    @AutoFill(OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteBySetmealId(List<Long> ids);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealId(Long id);
}
