package com.martinwalls.presentsorganiser.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "occasions")
public class Occasion {

    @PrimaryKey
    private int id;

    private String name;

    public Occasion(int id, String name) {
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
