package com.github.orql.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;

public class OrqlMybatisFactoryBuilder {

    private Configuration configuration = new Configuration();

    public OrqlMybatisFactoryBuilder schemaPath(String schemaPath) {
        configuration.setSchemaPath(schemaPath);
        return this;
    }

    public OrqlMybatisFactoryBuilder sqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        configuration.setSqlSessionFactory(sqlSessionFactory);
        return this;
    }

    public OrqlMybatisFactory build() {
        OrqlMybatisFactory factory = new OrqlMybatisFactory(configuration);
        factory.init();
        return factory;
    }

}
