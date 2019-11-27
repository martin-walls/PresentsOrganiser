package com.martinwalls.presentsorganiser.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.martinwalls.presentsorganiser.data.dao.GiftDao;
import com.martinwalls.presentsorganiser.data.dao.GroupDao;
import com.martinwalls.presentsorganiser.data.dao.OccasionDao;
import com.martinwalls.presentsorganiser.data.dao.PresentDao;
import com.martinwalls.presentsorganiser.data.dao.RecipientDao;
import com.martinwalls.presentsorganiser.data.model.Gift;
import com.martinwalls.presentsorganiser.data.model.Group;
import com.martinwalls.presentsorganiser.data.model.Occasion;
import com.martinwalls.presentsorganiser.data.model.Present;
import com.martinwalls.presentsorganiser.data.model.Recipient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Recipient.class,
        Group.class,
        Gift.class,
        Present.class,
        Occasion.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "presents_db";

    public abstract RecipientDao recipientDao();
    public abstract GroupDao groupDao();
    public abstract GiftDao giftDao();
    public abstract PresentDao presentDao();
    public abstract OccasionDao occasionDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
