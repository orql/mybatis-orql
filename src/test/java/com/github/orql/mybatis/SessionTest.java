package com.github.orql.mybatis;

import com.github.orql.mybatis.mapper.UserMapper;
import com.github.orql.mybatis.schema.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class SessionTest extends TestBase {

    @Test
    public void testAdd() {
        SqlSession session = sqlSessionFactory.openSession(true);
        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = new User();
        user.setName("n11");
        userMapper.add(user);
    }

}
