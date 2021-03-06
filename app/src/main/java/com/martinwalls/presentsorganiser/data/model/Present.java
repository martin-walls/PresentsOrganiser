package com.martinwalls.presentsorganiser.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "presents")
public class Present {

    @PrimaryKey
    private int id;

    private String name;

    private String notes;

    public Present(int id, String name, String notes) {
        this.id = id;
        this.name = name;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }
}
