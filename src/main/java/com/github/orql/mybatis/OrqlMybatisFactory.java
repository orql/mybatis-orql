package com.github.orql.mybatis;

import com.github.orql.core.orql.OrqlNode;
import com.github.orql.core.orql.OrqlParser;
import com.github.orql.core.schema.ColumnInfo;
import com.github.orql.core.schema.SchemaManager;
import com.github.orql.core.sql.OrqlToSql;
import com.github.orql.mybatis.annotation.Orql;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

import com.github.orql.mybatis.MybatisSqlElement.*;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class OrqlMybatisFactory {

    private static final Logger logger = LoggerFactory.getLogger(OrqlMybatisFactory.class);

    private SqlSessionFactory sqlSessionFactory;

    private Configuration configuration;

    private OrqlToSql orqlToSql = new OrqlToSql();

    private SchemaManager schemaManager;

    private OrqlParser orqlParser;

    private String dialect;

    private String schemasPath;

    private String mappersPath;

    private OrqlMybatisFactory() {

    }

    public static OrqlMybatisFactory create(SqlSessionFactory sqlSessionFactory) {
        OrqlMybatisFactory orqlMybatisFactory = new OrqlMybatisFactory();
        orqlMybatisFactory.setSqlSessionFactory(sqlSessionFactory);
        orqlMybatisFactory.build();
        return orqlMybatisFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.configuration = sqlSessionFactory.getConfiguration();
        Properties variables = this.configuration.getVariables();
        if (variables != null) {
            if (variables.containsKey("orql.dialect")) {
                dialect = variables.getProperty("orql.dialect");
            }
            if (variables.containsKey("orql.schemas")) {
                schemasPath = variables.getProperty("orql.schemas");
            }
            if (variables.containsKey("orql.mappers")) {
                mappersPath = variables.getProperty("orql.mappers");
            }
        }
    }

    private void build() {
        orqlToSql.getSqlGenerator().setSqlParamTemplate(sqlParam ->
                "#{" + sqlParam.getName() + "}");
        schemaManager = new SchemaManager();
        schemaManager.scanPackage(schemasPath);
        orqlParser = new OrqlParser(schemaManager);
        if (mappersPath != null) {
            Reflections mappersReflections = new Reflections(new ConfigurationBuilder().forPackages(mappersPath));
            for (Class clazz : mappersReflections.getTypesAnnotatedWith(Mapper.class)) {
                logger.info("scan mapper: {}", clazz.getName());
                registerMapper(clazz);
            }
        }
    }

    public void registerMapper(Class<?> mapperClazz) {
        XmlMapper mapper = new XmlMapper();
        mapper.setNamespace(mapperClazz.getName());
        boolean hasMethod = false;
        for (Method method : mapperClazz.getMethods()) {
            String id = method.getName();
            Orql orql = method.getAnnotation(Orql.class);
            if (orql == null) {
                continue;
            }
            hasMethod = true;
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
        if (!hasMethod) {
            return;
        }
        String xml = convertToXml(mapper);
        addMapperXml(xml, "file://orql-mapper/" + mapperClazz.getName() + ".xml");
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

    public String convertToXml(XmlMapper mapper) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(mapper.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // 移除standalone
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(mapper, writer);
            result = writer.toString();
            //字符串转义
            result = result.replace("&lt;", "<");
            result = result.replace("&gt;", ">");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n"
                + result;
    }
}
