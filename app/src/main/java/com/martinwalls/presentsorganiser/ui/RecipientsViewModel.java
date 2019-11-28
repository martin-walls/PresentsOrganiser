package com.martinwalls.presentsorganiser.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.data.DBHandler;
import com.martinwalls.presentsorganiser.data.model.Recipient;

import java.util.List;

public class RecipientsViewModel extends AndroidViewModel {

    private DBHandler dbHandler;

    public RecipientsViewModel(Application application) {
        super(application);
        dbHandler = new DBHandler(application);
    }

    public List<Person> getAllRecipients() {
        return dbHandler.loadRecipients();
    }
}
