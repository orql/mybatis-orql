package com.github.orql.mybatis.schema;

import com.github.orql.core.annotation.Column;
import com.github.orql.core.annotation.Schema;

@Schema
public class User {

    @Column(primaryKey = true, generatedKey = true)
    private Long id;

    @Column
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
