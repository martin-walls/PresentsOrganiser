package com.martinwalls.presentsorganiser.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "presents")
public class Present {

    @PrimaryKey
    private int id;

    private String name;

    private String notes;

    private boolean bought;

    private boolean sent;

    public Present(int id, String name, String notes, boolean bought, boolean sent) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.bought = bought;
        this.sent = sent;
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

    public boolean isBought() {
        return bought;
    }

    public boolean isSent() {
        return sent;
    }
}
