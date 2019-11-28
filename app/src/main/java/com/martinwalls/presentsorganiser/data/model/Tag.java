package com.martinwalls.presentsorganiser.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tags")
public class Tag {

    @PrimaryKey
    private int id;

    private String name;

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
