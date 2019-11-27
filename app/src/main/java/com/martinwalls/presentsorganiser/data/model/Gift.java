package com.martinwalls.presentsorganiser.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "gifts",
        foreignKeys = {
                @ForeignKey(entity = Recipient.class,
                        parentColumns = "id",
                        childColumns = "recipient_id",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Present.class,
                        parentColumns = "id",
                        childColumns = "present_id",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Occasion.class,
                        parentColumns = "id",
                        childColumns = "occasion_id",
                        onDelete = ForeignKey.RESTRICT)})
public class Gift {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "recipient_id")
    private int recipientId;

    @ColumnInfo(name = "present_id")
    private int presentId;

    private LocalDate date;

    @ColumnInfo(name = "occasion_id")
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
