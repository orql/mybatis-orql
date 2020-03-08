package com.github.orql.mybatis.mapper;

import com.github.orql.mybatis.annotation.Orql;
import com.github.orql.mybatis.schema.Role;

public interface RoleMapper {

    @Orql(query = "role : {*}")
    Role queryById(Integer id);

}
