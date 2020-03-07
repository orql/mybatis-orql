package com.github.orql.mybatis.mapper;

import com.github.orql.mybatis.annotation.Orql;
import com.github.orql.mybatis.schema.User;

public interface UserMapper {

    @Orql(add = "user: {*, !id}")
    void addUser(User user);

    @Orql(query = "user(id = $id) : {*}")
    User queryById(Long id);
}
