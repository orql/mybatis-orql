package com.github.orql.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Add("user: {!id, *}
 * public void addUser(User user);
 *
 * @Add("user: {name, phone}")
 * public void addUser(@Param("name") String name, @Param("phone") String phone);
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Add {

    String value();

}
