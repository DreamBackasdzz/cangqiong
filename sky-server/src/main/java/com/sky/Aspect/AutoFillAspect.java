package com.sky.Aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pointCut(){}

    @Before("pointCut()")
    public void autoFill(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解
        OperationType value = annotation.value();//获取注解中定义的操作类型
        Object[] args = joinPoint.getArgs();//获取方法的参数
        if(args == null || args.length == 0){
            //如果参数不存在
            return;
        }
        else {
            //参数存在 获取第一个参数(一般实体类参数放在第一个，这是一种约定)
            Object arg = args[0];
            if(value == OperationType.INSERT){
                try {
                    Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                    Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                    Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                    setCreateUser.invoke(arg,BaseContext.getCurrentId());
                    setCreateTime.invoke(arg,LocalDateTime.now());
                    setUpdateUser.invoke(arg,BaseContext.getCurrentId());
                    setUpdateTime.invoke(arg,LocalDateTime.now());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(value == OperationType.UPDATE){
                try {
                    Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                    Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    setUpdateUser.invoke(arg,BaseContext.getCurrentId());
                    setUpdateTime.invoke(arg,LocalDateTime.now());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }


    }
}
