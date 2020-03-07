package com.github.orql.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Orql {

    /**
     * @Orql(add = "user : {name, phone}")
     * void addUser(User user);
     */
    String add() default "";

    /**
     * @Orql(update = "user(id = $id) : {name, phone}")
     * void updateUser(User user);
     */
    String update() default "";

    /**
     * @Orql(delete = "user(id = $id)")
     */
    String delete() default "";

    /**
     * @Orql(query = "user(id = $id) : {*}")
     * void queryById(Long id);
     */
    String query() default "";

    /**
     * @Orql(count = "user")
     * Long countAllUser();
     */
    String count() default "";

}
