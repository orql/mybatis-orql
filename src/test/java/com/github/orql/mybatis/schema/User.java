package com.github.orql.mybatis.schema;

import com.github.orql.core.annotation.BelongsTo;
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

    @Column(field = "create_time")
    private Date createTime;

    @BelongsTo(refKey = "role_id")
    private Role role;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createTime=" + createTime +
                ", role=" + role +
                '}';
    }
}
