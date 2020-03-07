package com.github.orql.mybatis.mapper;

import com.github.orql.mybatis.annotation.Orql;
import com.github.orql.mybatis.schema.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    @Orql(add = "user: {*, !id}")
    void add(User user);

    @Orql(delete = "user(id = $id)")
    void deleteById(Long id);
}
