package com.plantrice.forum.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//annotation 注解的意思
//登录的状态才可以访问
//@Target 元注解，声明自定义的注解，可以作用在那个类型（类，方法，属性）
//@Retention 声明保留时间，有效时间，在编译时有效，运行时有效
//@Document 是否生成文档，默认不生成
//@Inherited 继承，子类继承父类，是否继承父类的

//用来描述方法
@Target(ElementType.METHOD)
//程序运行时有效
@Retention(RetentionPolicy.RUNTIME)
//打上LoginRequired这个标记，说明只有登录才能被访问
public @interface LoginRequired {



}
