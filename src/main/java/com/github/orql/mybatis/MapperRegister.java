package com.github.orql.mybatis;

import com.github.orql.core.QueryOp;
import com.github.orql.core.QueryOrder;
import com.github.orql.core.orql.OrqlNode;
import com.github.orql.core.schema.AssociationInfo;
import com.github.orql.core.schema.ColumnInfo;
import com.github.orql.core.schema.SchemaInfo;
import com.github.orql.mybatis.annotation.Orql;
import com.github.orql.mybatis.util.ReflectUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MapperRegister {

    private static final Logger logger = LoggerFactory.getLogger(MapperRegister.class);

    private Configuration configuration;

    public MapperRegister(Configuration configuration) {
        this.configuration = configuration;
    }

    public void createAllResultMap() {
        MybatisSqlElement.XmlMapper mapper = new MybatisSqlElement.XmlMapper();
        mapper.setNamespace(configuration.getNamespace());
        for (SchemaInfo schemaInfo : configuration.getSchemaManager().getSchemas().values()) {
            MybatisResultElement.ResultMap resultMap = new MybatisResultElement.ResultMap();
            // id使用Simple类名
            resultMap.setId(getResultMapId(schemaInfo));
            logger.info("add resultMap: {}", resultMap.getId());
            resultMap.setType(schemaInfo.getClazz().getName());
            for (ColumnInfo columnInfo : schemaInfo.getColumns()) {
                String field = columnInfo.getField();
                if (columnInfo.isPrivateKey()) {
                    resultMap.setPrimaryKey(new MybatisResultElement.Result(field, columnInfo.getName()));
                } else if (columnInfo.isRefKey()) {
                    if (ReflectUtil.hasField(schemaInfo.getClazz(), columnInfo.getName())) {
                        // 排除没有的key，避免mybatis报错
                        resultMap.addResut(new MybatisResultElement.Result(field, columnInfo.getName()));
                    }
                } else {
                    resultMap.addResut(new MybatisResultElement.Result(field, columnInfo.getName()));
                }
            }
            for (AssociationInfo associationInfo : schemaInfo.getAssociations()) {
                if (associationInfo.getType() == AssociationInfo.Type.HasOne
                        || associationInfo.getType() == AssociationInfo.Type.BelongsTo) {
                    MybatisResultElement.Association association = new MybatisResultElement.Association();
                    association.setProperty(associationInfo.getName());
                    association.setJavaType(associationInfo.getRef().getClazz().getName());
                    association.setResultMap(getResultMapId(associationInfo.getRef()));
                    // columnPrefix table1_
                    String columnPrefix = associationInfo.getName() + "_";
                    association.setColumnPrefix(columnPrefix);
                    resultMap.addAssociation(association);
                } else {
                    MybatisResultElement.Collection collection = new MybatisResultElement.Collection();
                    collection.setProperty(associationInfo.getName());
                    collection.setOfType(associationInfo.getRef().getClazz().getName());
                    collection.setResultMap(getResultMapId(associationInfo.getRef()));
                    // columnPrefix table1_
                    String columnPrefix = associationInfo.getName() + "_";
                    collection.setColumnPrefix(columnPrefix);
                    resultMap.addCollection(collection);
                }
            }
            mapper.addResultMap(resultMap);
        }
        String xml = convertToXml(mapper);
//        logger.debug("all result map: \n{}", xml);
        addMapperXml(xml, "file://orql-mapper/all-result-map.xml");
    }

    private String getResultMapId(SchemaInfo schemaInfo) {
        return schemaInfo.getClazz().getSimpleName() + "ResultMap";
    }

    public void registerMapper(Class<?> mapperClazz) {
        MybatisSqlElement.XmlMapper mapper = new MybatisSqlElement.XmlMapper();
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
                OrqlNode orqlNode = configuration.getOrqlParser().parse(orql.add());
                String sql = configuration.getOrqlToSql().toAdd(orqlNode.getRoot());
                ColumnInfo columnInfo = orqlNode.getRoot().getRef().getIdColumn();
                String keyProperty = columnInfo != null && columnInfo.isGeneratedKey()
                        ? columnInfo.getField()
                        : null;
                MybatisSqlElement.Insert insert = new MybatisSqlElement.Insert(id, sql, keyProperty, keyProperty != null);
                mapper.addInsert(insert);
            } else if (!orql.update().isEmpty()) {
                OrqlNode orqlNode = configuration.getOrqlParser().parse(orql.update());
                String sql = configuration.getOrqlToSql().toUpdate(orqlNode.getRoot());
                MybatisSqlElement.Update update = new MybatisSqlElement.Update(id, sql);
                mapper.addUpdate(update);
            } else if (!orql.delete().isEmpty()) {
                OrqlNode orqlNode = configuration.getOrqlParser().parse(orql.delete());
                String sql = configuration.getOrqlToSql().toDelete(orqlNode.getRoot());
                MybatisSqlElement.Delete delete = new MybatisSqlElement.Delete(id, sql);
                mapper.addDelete(delete);
            } else if (!orql.query().isEmpty()) {
                OrqlNode orqlNode = configuration.getOrqlParser().parse(orql.query());
                List<QueryOrder> queryOrders = null;
                if (!orql.asc()[0].isEmpty() || !orql.desc()[0].isEmpty()) {
                    queryOrders = new ArrayList<>();
                    if (!orql.asc()[0].isEmpty()) {
                        QueryOrder queryOrder = new QueryOrder();
                        for (String column : orql.asc()) {
                            queryOrder.addColumn(column);
                        }
                        queryOrders.add(queryOrder);
                    }
                    if (!orql.desc()[0].isEmpty()) {
                        QueryOrder queryOrder = new QueryOrder();
                        queryOrder.setSort("desc");
                        for (String column : orql.desc()) {
                            queryOrder.addColumn(column);
                        }
                        queryOrders.add(queryOrder);
                    }
                }
                QueryOp op = method.getReturnType() == List.class ? QueryOp.QueryAll : QueryOp.QueryOne;
                String sql = configuration.getOrqlToSql().toQuery(op, orqlNode.getRoot(), hasPage(method), queryOrders);
                // 根据返回类型获取resultMap
                Class<?> returnClazz = ReflectUtil.getReturnClazz(method);
                String resultMapId = configuration.getNamespace() + "." + returnClazz.getSimpleName() + "ResultMap";
                MybatisSqlElement.Select select = new MybatisSqlElement.Select(method.getName(), sql);
                select.setResultMap(resultMapId);
                mapper.addSelect(select);
            } else if (!orql.count().isEmpty()) {
                OrqlNode orqlNode = configuration.getOrqlParser().parse(orql.count());
                String sql = configuration.getOrqlToSql().toQuery(QueryOp.Count, orqlNode.getRoot(), false, null);
                MybatisSqlElement.Select countSelect = new MybatisSqlElement.Select(id, sql);
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
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream,
                configuration.getSqlSessionFactory().getConfiguration(), resource,
                configuration.getSqlSessionFactory().getConfiguration().getSqlFragments());
        mapperParser.parse();
    }

    private String convertToXml(MybatisSqlElement.XmlMapper mapper) {
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
