package com.martinwalls.presentsorganiser.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.martinwalls.presentsorganiser.data.model.Present;

import java.util.List;

@Dao
public interface PresentDao {

    @Insert
    void insert(Present present);

    @Delete
    void delete(Present present);

    @Query("SELECT * FROM presents")
    LiveData<List<Present>> getAllPresents();
}
