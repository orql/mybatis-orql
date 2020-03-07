package com.github.orql.mybatis;

import com.github.orql.core.mapper.OrqlResultGen;
import com.github.orql.core.mapper.ResultRoot;
import com.github.orql.core.orql.OrqlNode;
import com.github.orql.core.orql.OrqlParser;
import com.github.orql.core.schema.ColumnInfo;
import com.github.orql.core.sql.OrqlToSql;
import com.github.orql.mybatis.annotation.Orql;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.orql.mybatis.MybatisSqlElement.*;
import com.github.orql.mybatis.MybatisResultElement.*;

public class OrqlMybatisFactory {

    private SqlSessionFactory sqlSessionFactory;

    private Configuration configuration;

    private OrqlToSql orqlToSql;

    private OrqlParser orqlParser;

    private OrqlResultGen orqlResultGen;

    public OrqlMybatisFactory() {
        orqlToSql.getSqlGenerator().setSqlParamTemplate(sqlParam ->
                "#{" + sqlParam.getName() + "}");
    }

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
        XmlMapper mapper = new XmlMapper();
        for (Method method : mapperClazz.getMethods()) {
            String id = method.getName();
            Orql orql = method.getAnnotation(Orql.class);
            if (orql == null) {
                continue;
            }
            if (!orql.add().isEmpty()) {
                OrqlNode orqlNode = orqlParser.parse(orql.add());
                String sql = orqlToSql.toAdd(orqlNode.getRoot());
                ColumnInfo columnInfo = orqlNode.getRoot().getRef().getIdColumn();
                String keyProperty = columnInfo != null && columnInfo.isGeneratedKey()
                        ? columnInfo.getField()
                        : null;
                Insert insert = new Insert(id, sql, keyProperty);
                mapper.addInsert(insert);
            } else if (!orql.update().isEmpty()) {
                OrqlNode orqlNode = orqlParser.parse(orql.update());
                String sql = orqlToSql.toUpdate(orqlNode.getRoot());
                Update update = new Update(id, sql);
                mapper.addUpdate(update);
            } else if (!orql.delete().isEmpty()) {
                OrqlNode orqlNode = orqlParser.parse(orql.delete());
                String sql = orqlToSql.toDelete(orqlNode.getRoot());
                Delete delete = new Delete(id, sql);
                mapper.addDelete(delete);
            } else if (!orql.query().isEmpty()) {
                OrqlNode orqlNode = orqlParser.parse(orql.query());
                //FIXME 后续加上orders
                String sql = orqlToSql.toQuery("query", orqlNode.getRoot(), hasPage(method), null);
                String resultMapId = id + "ResultMap";
                ResultRoot resultRoot = orqlResultGen.toResult(orqlNode.getRoot());
                mapper.addResultMap(resultRootToResultMap(resultRoot));
                Select select = new Select(method.getName(), sql);
                select.setResultMap(resultMapId);
                mapper.addSelect(select);
            } else if (!orql.count().isEmpty()) {
                OrqlNode orqlNode = orqlParser.parse(orql.count());
                String sql = orqlToSql.toQuery("query", orqlNode.getRoot(), false, null);
                Select countSelect = new Select(id, sql);
                countSelect.setResultType("java.lang.Integer");
                mapper.addSelect(countSelect);
            }
        }
    }

    private ResultMap resultRootToResultMap(ResultRoot resultRoot) {
        return null;
    }

    private boolean hasPage(Method method) {
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    if (((Param) annotation).value().equals("limit")) {
                        return true;
                    }
                    if (((Param) annotation).value().equals("offset")) {
                        return true;
                    }
                }
            }
        }
        return false;
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
