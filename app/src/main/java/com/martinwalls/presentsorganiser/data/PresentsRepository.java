package com.martinwalls.presentsorganiser.data;

import android.app.Application;

import com.martinwalls.presentsorganiser.data.dao.GiftDao;
import com.martinwalls.presentsorganiser.data.dao.GroupDao;
import com.martinwalls.presentsorganiser.data.dao.OccasionDao;
import com.martinwalls.presentsorganiser.data.dao.PresentDao;
import com.martinwalls.presentsorganiser.data.dao.RecipientDao;

public class PresentsRepository {

    private RecipientDao recipientDao;
    private GroupDao groupDao;
    private GiftDao giftDao;
    private PresentDao presentDao;
    private OccasionDao occasionDao;

    //todo add LiveData<> fields

    public PresentsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        recipientDao = db.recipientDao();
        groupDao = db.groupDao();
        giftDao = db.giftDao();
        presentDao = db.presentDao();
        occasionDao = db.occasionDao();
    }
}
