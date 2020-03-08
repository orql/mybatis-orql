package com.github.orql.mybatis;

import com.github.orql.core.orql.OrqlParser;
import com.github.orql.core.schema.SchemaManager;
import com.github.orql.core.sql.OrqlToSql;
import org.apache.ibatis.session.SqlSessionFactory;

public class Configuration {

    private String schemaPath;

    private String dialect;

    private SchemaManager schemaManager;

    private OrqlToSql orqlToSql;

    private OrqlParser orqlParser;

    private String namespace;

    private SqlSessionFactory sqlSessionFactory;

    public Configuration() {
        schemaManager = new SchemaManager();
        orqlToSql = new OrqlToSql();
        orqlParser = new OrqlParser(schemaManager);
        namespace = "com.github.orql.mybatis.mapper.CommonMapper";
    }

    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public OrqlParser getOrqlParser() {
        return orqlParser;
    }

    public OrqlToSql getOrqlToSql() {
        return orqlToSql;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public String getSchemaPath() {
        return schemaPath;
    }
}
