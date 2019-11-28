package com.martinwalls.presentsorganiser.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "gifts_tags",
        foreignKeys = {
                @ForeignKey(entity = Gift.class,
                            parentColumns = "id",
                            childColumns = "gift_id",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tag.class,
                            parentColumns = "id",
                            childColumns = "tag_id",
                            onDelete = ForeignKey.CASCADE)})
public class GiftTagJoin {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "gift_id")
    private int giftId;

    @ColumnInfo(name = "tag_id")
    private int tagId;

    public GiftTagJoin(int id, int giftId, int tagId) {
        this.id = id;
        this.giftId = giftId;
        this.tagId = tagId;
    }

    public int getId() {
        return id;
    }

    public int getGiftId() {
        return giftId;
    }

    public int getTagId() {
        return tagId;
    }
}
