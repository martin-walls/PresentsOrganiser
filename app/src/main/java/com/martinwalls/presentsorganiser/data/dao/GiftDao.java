package com.martinwalls.presentsorganiser.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.martinwalls.presentsorganiser.data.model.Gift;

import java.util.List;

@Dao
public interface GiftDao {

    @Insert
    void insert(Gift gift);

    @Delete
    void delete(Gift gift);

    @Query("SELECT * FROM gifts")
    LiveData<List<Gift>> getAllGifts();
}
