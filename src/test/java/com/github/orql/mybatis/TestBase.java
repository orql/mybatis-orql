package com.github.orql.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

public class TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected SqlSessionFactory sqlSessionFactory;

    protected Configuration configuration;

    public static String readString(String resourcePath) {
        URL url = TestBase.class.getResource(resourcePath);
        try {
            Path resPath = Paths.get(url.toURI());
            return new String(Files.readAllBytes(resPath), "UTF8");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    @Before
    public void init() throws IOException, SQLException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        configuration = sqlSessionFactory.getConfiguration();

        SqlSession session = sqlSessionFactory.openSession(true);
        Connection connection = session.getConnection();
        connection.prepareStatement(readString("/sql/init-table.sql")).execute();
        connection.prepareStatement(readString("/sql/data.sql")).execute();
        session.close();
        OrqlMybatisFactory orqlMybatisFactory = OrqlMybatisFactory.create(sqlSessionFactory);

//        org.h2.tools.Server.startWebServer(connection);
    }

}
