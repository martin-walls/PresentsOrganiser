package com.martinwalls.presentsorganiser.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.martinwalls.presentsorganiser.data.model.Tag;

import java.util.List;

@Dao
public interface TagDao {

    @Insert
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);

    @Query("SELECT * FROM tags")
    LiveData<List<Tag>> getAllTags();
}
