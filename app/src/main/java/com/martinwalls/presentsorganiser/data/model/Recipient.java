package com.martinwalls.presentsorganiser.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipients",
        foreignKeys = @ForeignKey(entity = Group.class,
                parentColumns = "id",
                childColumns = "group_id",
                onDelete = ForeignKey.RESTRICT))
public class Recipient {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    private String surname;

    @ColumnInfo(name = "group_id")
    private int groupId;

    public Recipient(int id, String firstName, String surname, int groupId) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.groupId = groupId;
    }

    @Ignore
    public Recipient(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    @Ignore
    public Recipient(String firstName, String surname, int groupId) {
        this.firstName = firstName;
        this.surname = surname;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public int getGroupId() {
        return groupId;
    }
}
