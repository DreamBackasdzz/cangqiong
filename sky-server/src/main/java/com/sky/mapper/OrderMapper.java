package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> list(Integer status, LocalDateTime time);

    Double getByMap(Map map);

    Integer getCountByMap(Map map);

    List<GoodsSalesDTO> getGooodsSalesTop10(LocalDateTime begin, LocalDateTime end);

    Page<Orders> getByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(id) from orders where status = #{confirmed}")
    Integer countStatistics(Integer confirmed);
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
}
