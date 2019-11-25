package com.martinwalls.presentsorganiser.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipientDao {

    @Insert
    void insert(Recipient recipient);

    @Delete
    void delete(Recipient recipient);

    @Query("SELECT * FROM recipients") //todo JOIN with groups table
    LiveData<List<Recipient>> getAllRecipients();
}
