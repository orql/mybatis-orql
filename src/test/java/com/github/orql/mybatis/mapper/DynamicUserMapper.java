package com.github.orql.mybatis.mapper;

import com.github.orql.mybatis.schema.User;

public interface DynamicUserMapper {

    User findById(Long id);

}
