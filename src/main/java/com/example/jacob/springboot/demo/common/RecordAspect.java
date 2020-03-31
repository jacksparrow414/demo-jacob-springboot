package com.example.jacob.springboot.demo.common;

import cn.hutool.core.util.ObjectUtil;
import com.example.jacob.springboot.demo.annotation.RecordLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author duhongbo
 * @date 2020/3/30 15:47
 */
@Aspect
@Component
public class RecordAspect {

    @Pointcut(value = "@annotation(com.example.jacob.springboot.demo.annotation.RecordLog)")
    public void pointCut(){

    }

    @Before("pointCut()")
    public void before(){
        System.out.println("方法执行之前");
    }

    @Around("pointCut()")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RecordLog annotation = method.getAnnotation(RecordLog.class);
        if (ObjectUtil.isNotNull(annotation)){
            System.out.println("进入切面");
        }
        return joinPoint.proceed();
    }

    @After("pointCut()")
    public void after(){
        System.out.println("方法执行完毕");
    }
}
