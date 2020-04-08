package com.github.orql.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrqlMybatisFactory {

    private static final Logger logger = LoggerFactory.getLogger(OrqlMybatisFactory.class);

    private Configuration configuration;

    public OrqlMybatisFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public void init() {
        configuration.getOrqlToSql().getSqlGenerator().setSqlParamTemplate(sqlParam ->
                "#{" + sqlParam.getName() + "}");
        configuration.getSchemaManager().scanPackage(configuration.getSchemaPath());
        configuration.getMapperRegister().createAllResultMap();
        configuration.getMapperRegister().registerMappers();
    }

    public void registerMapper(Class<?> mapper) {
        configuration.getMapperRegister().registerMapper(mapper);
    }
}
