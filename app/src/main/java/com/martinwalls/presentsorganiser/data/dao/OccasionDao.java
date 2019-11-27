package com.martinwalls.presentsorganiser.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.martinwalls.presentsorganiser.data.model.Occasion;

@Dao
public interface OccasionDao {

    @Insert
    void insert(Occasion occasion);

    @Delete
    void delete(Occasion occasion);
}
