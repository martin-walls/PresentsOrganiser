package com.martinwalls.presentsorganiser.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {

    @Insert
    void insert(Group group);

    @Delete
    void delete(Group group);

    @Query("SELECT * FROM groups WHERE id = :id")
    LiveData<Group> getGroup(int id);

    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getAllGroups();
}
