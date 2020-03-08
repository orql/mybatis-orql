package com.github.orql.mybatis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class MybatisResultElement {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Result {

        @XmlAttribute
        private String column;

        @XmlAttribute
        private String property;

        public Result(String column, String property) {
            this.column = column;
            this.property = property;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BaseResultMap {

        @XmlAttribute
        private String columnPrefix;

        @XmlAttribute
        private String resultMap;

        @XmlElement(name = "id")
        private Result primaryKey;

        @XmlElement(name = "result")
        private List<Result> results = new ArrayList<>();

        /**
         * 一对一关联关系
         */
        @XmlElement(name = "association")
        private List<Association> associations = new ArrayList<>();

        /**
         * 一对多关系
         */
        @XmlElement(name = "collection")
        private List<Collection> collections = new ArrayList<>();

        public void addResut(Result result) {
            results.add(result);
        }

        public void addAssociation(Association association) {
            associations.add(association);
        }

        public void addCollection(Collection collection) {
            collections.add(collection);
        }

        public Result getPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(Result primaryKey) {
            this.primaryKey = primaryKey;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public List<Association> getAssociations() {
            return associations;
        }

        public void setAssociations(List<Association> associations) {
            this.associations = associations;
        }

        public List<Collection> getCollections() {
            return collections;
        }

        public void setCollections(List<Collection> collections) {
            this.collections = collections;
        }

        public String getColumnPrefix() {
            return columnPrefix;
        }

        public void setColumnPrefix(String columnPrefix) {
            this.columnPrefix = columnPrefix;
        }

        public String getResultMap() {
            return resultMap;
        }

        public void setResultMap(String resultMap) {
            this.resultMap = resultMap;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Association extends BaseResultMap {

        /**
         * 在拥有方的属性名
         */
        @XmlAttribute
        private String property;

        /**
         * 对应是类全名
         */
        @XmlAttribute
        private String javaType;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Collection extends BaseResultMap {

        /**
         * 在拥有方的属性名
         */
        @XmlAttribute
        private String property;

        /**
         * 单个对象类全名
         */
        @XmlAttribute
        private String ofType;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getOfType() {
            return ofType;
        }

        public void setOfType(String ofType) {
            this.ofType = ofType;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ResultMap extends BaseResultMap {

        /**
         * 对应的类全名
         */
        @XmlAttribute
        private String type;

        @XmlAttribute
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

}
