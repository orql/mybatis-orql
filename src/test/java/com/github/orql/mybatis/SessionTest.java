package com.github.orql.mybatis;

import com.github.orql.mybatis.mapper.UserMapper;
import com.github.orql.mybatis.schema.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SessionTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(SessionTest.class);

    @Test
    public void testQueryById() {
        SqlSession session = sqlSessionFactory.openSession(true);
        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = userMapper.queryById(1L);
        logger.info("user: {}", user);
        assertEquals("n1", user.getName());
    }

    @Test
    public void testAdd() {
        SqlSession session = sqlSessionFactory.openSession(true);
        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = new User();
        user.setCreateTime(new Date());
        user.setName("n11");
        userMapper.add(user);
        User result = userMapper.queryById(user.getId());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    public void testQueryAll() {
        SqlSession session = sqlSessionFactory.openSession(true);
        UserMapper userMapper = session.getMapper(UserMapper.class);
        List<User> users = userMapper.queryAll();
        logger.info("users: {}", users);
    }

    @Test
    public void testOrderDesc() {
        SqlSession session = sqlSessionFactory.openSession(true);
        UserMapper userMapper = session.getMapper(UserMapper.class);
        List<User> users = userMapper.queryAllOrderByIdDesc();
        logger.info("users: {}", users);
    }

}
