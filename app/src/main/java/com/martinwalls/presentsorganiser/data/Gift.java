package com.martinwalls.presentsorganiser.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "gifts")
public class Gift {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "recipient_id")
    private int recipientId;

    @ColumnInfo(name = "present_id")
    private int presentId;

    private LocalDate date;

    @ColumnInfo(name = "present_id")
    private int occasionId;

    public Gift(int id, int recipientId, int presentId, LocalDate date, int occasionId) {
        this.id = id;
        this.recipientId = recipientId;
        this.presentId = presentId;
        this.date = date;
        this.occasionId = occasionId;
    }

    public int getId() {
        return id;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public int getPresentId() {
        return presentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getOccasionId() {
        return occasionId;
    }
}
