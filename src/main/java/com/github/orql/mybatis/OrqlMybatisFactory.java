package com.github.orql.mybatis;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class OrqlMybatisFactory {

    private SqlSessionFactory sqlSessionFactory;

    private Configuration configuration;

    public static OrqlMybatisFactory create(SqlSessionFactory sqlSessionFactory) {
        OrqlMybatisFactory orqlMybatisFactory = new OrqlMybatisFactory();
        orqlMybatisFactory.setSqlSessionFactory(sqlSessionFactory);
        return orqlMybatisFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.configuration = sqlSessionFactory.getConfiguration();
    }

    public void registerMapper(Class<?> mapperClazz) {

    }

    /**
     * 添加mapper xml
     * @param xml mapper xml string
     * @param resource file://aa.xml
     */
    private void addMapperXml(String xml, String resource) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
        mapperParser.parse();
    }
}
