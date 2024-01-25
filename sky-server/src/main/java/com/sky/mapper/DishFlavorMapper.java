package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import javafx.scene.chart.ValueAxis;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    @AutoFill(value = OperationType.INSERT)
    void insert(List<DishFlavor> flavors);

    void deleteByDishIds(List<Long> dishIds);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);
}
