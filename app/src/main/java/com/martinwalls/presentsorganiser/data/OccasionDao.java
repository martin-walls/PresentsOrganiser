package com.martinwalls.presentsorganiser.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

@Dao
public interface OccasionDao {

    @Insert
    void insert(Occasion occasion);

    @Delete
    void delete(Occasion occasion);
}
