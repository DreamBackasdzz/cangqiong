package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.Internal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        //封装每日有效订单数列表validOrderCountList
        for (LocalDate date : dateList) {
            LocalDateTime localBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime localEnd = LocalDateTime.of(date, LocalTime.MIN);
            //封装每日订单数列表orderCountList
            //select count(id) from orders where order_time >= begin and order_time <= end
            Map map = new HashMap();
            map.put("begin",localBegin);
            map.put("end",localEnd);
            Integer dailyOrderCount = orderMapper.getCountByMap(map);
            orderCountList.add(dailyOrderCount);
            map.put("status",Orders.COMPLETED);
            //select count(id) from orders where order_time >= begin and order_time <= end and status = 5
            Integer dailyValidOrderCount = orderMapper.getCountByMap(map);
            validOrderCountList.add(dailyValidOrderCount);
        }
        //订单总数totalOrderCount
//        Integer totalOrderCount=0;
//        for (Integer i : orderCountList) {
//            totalOrderCount = i +totalOrderCount;
//        }
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //有效订单数validOrderCount
//        Integer validOrderCount = 0;
//        for (Integer i : validOrderCountList) {
//            validOrderCount = i + validOrderCount;
//        }
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率orderCompletionRate
        Double orderCompletionRate = 0.0;
        try {
            orderCompletionRate = (validOrderCount/totalOrderCount) * 100.0;
        } catch (ArithmeticException e) {
            orderCompletionRate = 0.0;
        }
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime localBegin = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime localEnd = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> gooodsSalesTop10 = orderMapper.getGooodsSalesTop10(localBegin, localEnd);
        List<String> namelist = gooodsSalesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> mnumberlist = gooodsSalesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(namelist,","))
                .numberList(StringUtils.join(mnumberlist,","))
                .build();
    }
}
