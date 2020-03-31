package com.example.jacob.springboot.demo.annotation;

import java.lang.annotation.*;

/**
 * @author duhongbo
 * @date 2020/3/30 15:44
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RecordLog {
}
