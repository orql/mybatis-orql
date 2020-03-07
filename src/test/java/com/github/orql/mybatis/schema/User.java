package com.github.orql.mybatis.schema;

import com.github.orql.core.annotation.Column;
import com.github.orql.core.annotation.Schema;

import java.util.Date;

@Schema
public class User {

    @Column(primaryKey = true, generatedKey = true)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
