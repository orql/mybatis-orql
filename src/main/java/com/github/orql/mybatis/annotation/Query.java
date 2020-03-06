package com.github.orql.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Query("user(id = $id): {name}")
 * public User findUserById(@Param("id") Long id);
 *
 * @Query("user: {name})
 * public User findAllUser(Page page);
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    String value();

}
