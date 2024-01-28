package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单{}", LocalDateTime.now());
        //select * from order where status = ? and order_time = ?
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList  = orderMapper.list(Orders.PENDING_PAYMENT,time);
        if(ordersList != null && !ordersList.isEmpty()){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时");
                orderMapper.update(orders);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ?")//每天1点触发一次
    public void processDeliveryOrder(){
        log.info("定时处理正在派送订单{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList  = orderMapper.list(Orders.DELIVERY_IN_PROGRESS,time);
        if(ordersList != null && !ordersList.isEmpty()){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
