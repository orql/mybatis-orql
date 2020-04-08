package com.github.orql.mybatis.mapper;

import com.github.orql.mybatis.annotation.Orql;
import com.github.orql.mybatis.schema.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    @Orql(add = "user: {*, !id}")
    void add(User user);

    @Orql(add = "user: {*, !id, role}")
    void addUserAndRole(User user);

    @Orql(delete = "user(id = $id)")
    void deleteById(Long id);

    @Orql(query = "user(id = $id): {*}")
    User queryById(Long id);

    @Orql(query = "user(id = $id): {id, name, role: {*}}")
    User queryByIdWithRole(Long id);

    @Orql(query = "user(id = $id) : {*, info: {*}}")
    User queryByIdWithInfo(Long id);

    @Orql(query = "user : {*}")
    List<User> queryAll();

    @Orql(query = "user : {*}", desc = "id")
    List<User> queryAllOrderByIdDesc();

    User selectByIdFromXml(Long id);

}
