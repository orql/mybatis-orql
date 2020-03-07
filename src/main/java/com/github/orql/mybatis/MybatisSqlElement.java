package com.github.orql.mybatis;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import com.github.orql.mybatis.MybatisResultElement.*;

/**
 * mybatis xml element
 */
public class MybatisSqlElement {

    @XmlRootElement(name = "mapper")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class XmlMapper {

        @XmlAttribute
        private String namespace;

        @XmlElement(name = "select")
        private List<Select> selects = new ArrayList<>();

        @XmlElement(name = "insert")
        private List<Insert> inserts = new ArrayList<>();

        @XmlElement(name = "delete")
        private List<BaseNode> deletes = new ArrayList<>();

        @XmlElement(name = "update")
        private List<BaseNode> updates = new ArrayList<>();

        @XmlElement(name = "resultMap")
        private List<ResultMap> resultMaps = new ArrayList<>();

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public List<Select> getSelects() {
            return selects;
        }

        public void setSelects(List<Select> selects) {
            this.selects = selects;
        }

        public List<Insert> getInserts() {
            return inserts;
        }

        public void setInserts(List<Insert> inserts) {
            this.inserts = inserts;
        }

        public List<BaseNode> getDeletes() {
            return deletes;
        }

        public void setDeletes(List<BaseNode> deletes) {
            this.deletes = deletes;
        }

        public List<BaseNode> getUpdates() {
            return updates;
        }

        public void setUpdates(List<BaseNode> updates) {
            this.updates = updates;
        }

        public List<ResultMap> getResultMaps() {
            return resultMaps;
        }

        public void setResultMaps(List<ResultMap> resultMaps) {
            this.resultMaps = resultMaps;
        }

        public void addSelect(Select select) {
            selects.add(select);
        }

        public void addUpdate(BaseNode update) {
            updates.add(update);
        }

        public void addInsert(Insert insert) {
            inserts.add(insert);
        }

        public void addDelete(BaseNode delete) {
            deletes.add(delete);
        }

        public void addResultMap(ResultMap resultMap) {
            resultMaps.add(resultMap);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static abstract class BaseNode {

        @XmlAttribute
        protected String id;

        @XmlValue
        protected String sql;

        public BaseNode(String id, String sql) {
            this.id = id;
            this.sql = sql;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Insert extends BaseNode {

        public Insert(String id, String sql, String keyProperty) {
            super(id, sql);
            this.keyProperty = keyProperty;
        }

        @XmlAttribute
        private String keyProperty;

        @XmlAttribute
        private String parameterType;

        public String getKeyProperty() {
            return keyProperty;
        }

        public void setKeyProperty(String keyProperty) {
            this.keyProperty = keyProperty;
        }

        public String getParameterType() {
            return parameterType;
        }

        public void setParameterType(String parameterType) {
            this.parameterType = parameterType;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Select extends BaseNode {

        @XmlAttribute
        private String resultMap;

        @XmlAttribute
        private String resultType;

        public Select(String id, String sql) {
            super(id, "<![CDATA[" + sql + "]]>");
        }

        public String getResultMap() {
            return resultMap;
        }

        public void setResultMap(String resultMap) {
            this.resultMap = resultMap;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }
    }

    public static class Update extends BaseNode {

        public Update(String id, String sql) {
            super(id, sql);
        }
    }

    public static class Delete extends BaseNode {

        public Delete(String id, String sql) {
            super(id, sql);
        }
    }



}
