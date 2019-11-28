package com.martinwalls.presentsorganiser.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.martinwalls.presentsorganiser.data.model.GiftTagJoin;
import com.martinwalls.presentsorganiser.data.model.Tag;

import java.util.List;

@Dao
public interface GiftTagJoinDao {

    @Insert
    void insert(GiftTagJoin giftTagJoin);

    @Query("SELECT * FROM tags " +
            "INNER JOIN gifts_tags " +
            "ON tags.id = gifts_tags.tag_id " +
            "WHERE gifts_tags.gift_id = :giftId")
    LiveData<List<Tag>> getAllTagsForGift(int giftId);
}
