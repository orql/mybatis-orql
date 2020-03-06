package com.github.orql.mybatis;

import com.github.orql.mybatis.mapper.DynamicUserMapper;
import com.github.orql.mybatis.schema.User;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class DynamicMapperTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(DynamicMapperTest.class);

    @Test
    public void testAddMapper() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("dynamic-user-mapper.xml");
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "file://user-dynamic-mapper.xml", configuration.getSqlFragments());
        mapperParser.parse();
        SqlSession session = sqlSessionFactory.openSession();
        DynamicUserMapper userMapper = session.getMapper(DynamicUserMapper.class);
        User user = userMapper.findById(1L);
        logger.info("user: {}", user);
        assertEquals(1, (long) user.getId());
    }

}
