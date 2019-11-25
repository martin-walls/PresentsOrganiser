package com.martinwalls.presentsorganiser.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.martinwalls.presentsorganiser.BuildConfig;
import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.GivenPresent;
import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.pendingpresents.PendingPresentsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 3;
    public static String DATABASE_NAME = "presentsDB.db";

    private static final String TAG = DBHandler.class.getName();
    private final Context context;

    // values for families table
    private static final String TABLE_FAMILIES = "Families";
    private static final String COLUMN_FAMILY = "Family";

    // values for names table
    private static final String TABLE_NAMES = "Names";
    private static final String COLUMN_PERSON_ID = "PersonId";
    private static final String COLUMN_NAME = "FirstName";

    // values for presents-names table
    private static final String TABLE_GIVEN_PRESENTS_NAMES = "GivenPresents_Names";

    // columns for given presents tables
    private static final String TABLE_GIVEN_PRESENTS = "GivenPresents";
    private static final String COLUMN_PRESENT_ID = "PresentId";
    private static final String COLUMN_YEAR = "Year";
    private static final String COLUMN_PRESENT = "Present";
    private static final String COLUMN_NOTES = "Notes";
    private static final String COLUMN_BOUGHT = "Bought";
    private static final String COLUMN_SENT = "Sent";

    //TODO: Add table for received presents

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table listing other tables for different years
//        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TABLES + " (" +
//                COLUMN_TABLE_NAME + " TEXT PRIMARY KEY, " + COLUMN_TABLE_TYPE + " TEXT)";
//        db.execSQL(CREATE_TABLE);

        // create presents table
        String CREATE_GIVEN_PRESENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GIVEN_PRESENTS + " (" +
                COLUMN_PRESENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_PRESENT + " TEXT NOT NULL, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_BOUGHT + " INTEGER, " +      // 1 or 0 for boolean
                COLUMN_SENT + " INTEGER )";
        db.execSQL(CREATE_GIVEN_PRESENTS_TABLE);

        // create table of families
        String CREATE_FAMILIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAMILIES + " (" +
                COLUMN_FAMILY + " TEXT PRIMARY KEY)";
        db.execSQL(CREATE_FAMILIES_TABLE);

        // create table of recipient names
        String CREATE_NAMES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAMES + " (" +
                COLUMN_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_FAMILY + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_FAMILY + ") REFERENCES " +
                TABLE_FAMILIES + " (" + COLUMN_FAMILY + ") )";
        db.execSQL(CREATE_NAMES_TABLE);

        String CREATE_GIVEN_PRESENTS_NAMES = "CREATE TABLE IF NOT EXISTS " + TABLE_GIVEN_PRESENTS_NAMES + " (" +
                COLUMN_PRESENT_ID + " INTEGER, " +
                COLUMN_PERSON_ID + " INTEGER, " +
                " PRIMARY KEY (" + COLUMN_PRESENT_ID + ", " + COLUMN_PERSON_ID + ")," +
                " FOREIGN KEY (" + COLUMN_PRESENT_ID + ") REFERENCES " +
                TABLE_GIVEN_PRESENTS + " (" + COLUMN_PRESENT_ID + ") ON DELETE CASCADE ON UPDATE NO ACTION," +
                " FOREIGN KEY (" + COLUMN_PERSON_ID + ") REFERENCES " +
                TABLE_NAMES + " (" + COLUMN_PERSON_ID + ") ON DELETE CASCADE ON UPDATE NO ACTION )";
        db.execSQL(CREATE_GIVEN_PRESENTS_NAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(db, context, migrationName);

            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }

    // read and execute SQL from a file
    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    // to execute SQL from a file
    private void executeSQLScript(SQLiteDatabase db, @NonNull BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }

    // export db to file
    public boolean exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + BuildConfig.APPLICATION_ID + "/databases/" + DATABASE_NAME;
        String backupDBPath = "Download/" + DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // import db from file
    public boolean importDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + BuildConfig.APPLICATION_ID + "/databases/" + DATABASE_NAME;
        String backupDBPath = "Download/" + DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(backupDB).getChannel();
            // clear current database so new db can be imported
            if (currentDB.delete()) {
                destination = new FileOutputStream(currentDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Family loadFamily(String familyName) {
        Family family = new Family();
        List<Person> members = loadPeopleInFamily(familyName);
        family.setFamilyName(familyName);
        family.setFamilyMembers(members);
        return family;
    }

    public List<Family> loadFamilies() {
        List<String> familyNames = loadFamilyNames();
        List<Family> result = new ArrayList<>();
        for (String familyName : familyNames) {
            Family family = loadFamily(familyName);
            result.add(family);
        }
        return result;
    }

    public List<String> loadFamilyNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> result = new ArrayList<>();
        String query = "SELECT " + COLUMN_FAMILY + " FROM " + TABLE_FAMILIES;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String familyName = cursor.getString(0);
            result.add(familyName);
        }
        cursor.close();
        db.close();
        return result;
    }

    private List<Person> loadPeopleInFamily(String familyName) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Person> result = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAMES +
                " WHERE " + COLUMN_FAMILY + " = '" + familyName.replace("'", "''") + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Person person = new Person();
            person.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID)));
            person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            person.setFamily(cursor.getString(cursor.getColumnIndex(COLUMN_FAMILY)));
            result.add(person);
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<Family> loadPeopleNoFamily() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Family> result = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAMES +
                " WHERE " + COLUMN_FAMILY + " IS NULL OR " + COLUMN_FAMILY + " = ''";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Person person = new Person();
            person.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID)));
            person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            person.setFamily(cursor.getString(cursor.getColumnIndex(COLUMN_FAMILY)));

            Family family = new Family();
            family.setFamilyName(person.getName());
            List<Person> members = new ArrayList<>();
            members.add(person);
            family.setFamilyMembers(members);
            family.setPersonWithoutFamily(true);
            result.add(family);
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean addFamily(String familyName) {
        List<String> families = loadFamilyNames();
        // if name already in db or empty (only whitespace) name is entered, invalid
        if (families.contains(familyName) || familyName.trim().isEmpty()) {
            return false;
        } else {
            familyName.replace("'", "''");
            // add new row to db with name
            ContentValues values = new ContentValues();
            values.put(COLUMN_FAMILY, familyName);
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_FAMILIES, null, values);
            db.close();
            return true;
        }
    }

    public boolean deleteFamily(Family family) {
        boolean result = false;
        // remove recipient from names table
        String queryNamesTable = "SELECT * FROM " + TABLE_FAMILIES +
                " WHERE " + COLUMN_FAMILY + " = '" +
                family.getFamilyName().replace("'", "''") + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryNamesTable, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            db.delete(TABLE_FAMILIES, COLUMN_FAMILY + "=?", new String[] {name});
            result = true;
        }
        cursor.close();

        // remove people in family
        for (Person person : family.getFamilyMembers()) {
            deletePerson(person);
        }

        db.close();
        return result;
    }

    // return list of presents from person from all years
    public List<GivenPresent> loadGivenPresents(Person person) {
        List<GivenPresent> presentList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_GIVEN_PRESENTS +
                " INNER JOIN " + TABLE_GIVEN_PRESENTS_NAMES + " ON " +
                TABLE_GIVEN_PRESENTS + "." + COLUMN_PRESENT_ID + " = " +
                TABLE_GIVEN_PRESENTS_NAMES + "." + COLUMN_PRESENT_ID +
                " WHERE " + COLUMN_PERSON_ID + " = " + person.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int presentId = cursor.getInt(cursor.getColumnIndex(COLUMN_PRESENT_ID));
            int year = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR));
            String presentName = cursor.getString(cursor.getColumnIndex(COLUMN_PRESENT));
            String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
            boolean isBought = cursor.getInt(cursor.getColumnIndex(COLUMN_BOUGHT)) != 0;
            boolean isSent = cursor.getInt(cursor.getColumnIndex(COLUMN_SENT)) != 0;

            List<Person> personList = new ArrayList<>();
            personList.add(person);
            GivenPresent present = new GivenPresent(year, presentId,
                    personList, presentName, notes, isBought, isSent);
            presentList.add(present);
        }

        return presentList;
    }

    // get a present from its id
    public GivenPresent loadPresent(int presentId) {
        GivenPresent present = new GivenPresent();
        String query = "SELECT * FROM " + TABLE_GIVEN_PRESENTS +
                " INNER JOIN " + TABLE_GIVEN_PRESENTS_NAMES + " ON " +
                TABLE_GIVEN_PRESENTS + "." + COLUMN_PRESENT_ID + " = " +
                TABLE_GIVEN_PRESENTS_NAMES + "." + COLUMN_PRESENT_ID +
                " WHERE " + TABLE_GIVEN_PRESENTS + "." + COLUMN_PRESENT_ID + " = " + presentId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int year = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR));
            int personId = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID));
            String presentName = cursor.getString(cursor.getColumnIndex(COLUMN_PRESENT));
            String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
            boolean isBought = cursor.getInt(cursor.getColumnIndex(COLUMN_BOUGHT)) != 0;
            boolean isSent = cursor.getInt(cursor.getColumnIndex(COLUMN_SENT)) != 0;

            Person person = loadPerson(personId);
            List<Person> personList = new ArrayList<>();
            personList.add(person);

            present = new GivenPresent(year, presentId,
                    personList, presentName, notes, isBought, isSent);

            while (cursor.moveToNext()) {
                int personId1 = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID));
                Person person1 = loadPerson(personId1);
                present.addRecipient(person1);
            }
        }
        cursor.close();
        db.close();
        return present;
    }

    public List<GivenPresent> loadPendingPresents(int presentsToLoad) {
        List<GivenPresent> presentList = new ArrayList<>();
        String columnToGet = presentsToLoad == PendingPresentsActivity.UNBOUGHT ? COLUMN_BOUGHT : COLUMN_SENT;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String query = "SELECT * FROM " + TABLE_GIVEN_PRESENTS +
                " INNER JOIN " + TABLE_GIVEN_PRESENTS_NAMES + " ON " +
                TABLE_GIVEN_PRESENTS + "." + COLUMN_PRESENT_ID + " = " +
                TABLE_GIVEN_PRESENTS_NAMES + "." + COLUMN_PRESENT_ID +
                " WHERE " + columnToGet + " = 0 OR " + columnToGet + " IS NULL" +
                " AND " + COLUMN_YEAR + " = " + year;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // last id makes sure the same present isn't added twice
        // (where there is more than one recipient)
        int lastPresentId = -1;
        while (cursor.moveToNext()) {
            int presentId = cursor.getInt(cursor.getColumnIndex(COLUMN_PRESENT_ID));
            if (lastPresentId != presentId) {
                int presentYear = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR));
                String presentName = cursor.getString(cursor.getColumnIndex(COLUMN_PRESENT));
                String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                boolean isBought = cursor.getInt(cursor.getColumnIndex(COLUMN_BOUGHT)) != 0;
                boolean isSent = cursor.getInt(cursor.getColumnIndex(COLUMN_SENT)) != 0;
                int personId = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID));
                Person person = loadPerson(personId);
                List<Person> personList = new ArrayList<>();
                personList.add(person);
                GivenPresent present = new GivenPresent(presentYear, presentId,
                        personList, presentName, notes, isBought, isSent);
                presentList.add(present);
            } else {
                int personId = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID));
                Person person = loadPerson(personId);
                presentList.get(presentList.size()-1).addRecipient(person);
            }

            lastPresentId = presentId;
        }
        cursor.close();
        db.close();
        return presentList;
    }

    public List<GivenPresent> loadGivenPresentsForFamily(Family family) {
        List<GivenPresent> presentList = new ArrayList<>();
        for (Person person : family.getFamilyMembers()) {
            presentList.addAll(loadGivenPresents(person));
        }
        return presentList;
    }

    public Person loadPerson(int personId) {
        Person person = new Person(personId);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAMES +
                " WHERE " + COLUMN_PERSON_ID + " = " + personId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            person.setFamily(cursor.getString(cursor.getColumnIndex(COLUMN_FAMILY)));
        }
        cursor.close();
        db.close();

        return person;
    }

    public int loadPersonId(String firstname, String familyName) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PERSON_ID + " FROM " + TABLE_NAMES +
                " WHERE " + COLUMN_NAME + " = '" + firstname + "'" +
                " AND " + COLUMN_FAMILY + " = '" + familyName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID));
        }
        cursor.close();
        db.close();

        return id;
    }

    // returns a sorted array of Person objects
    public List<Person> loadRecipients() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Person> result = new ArrayList<>();
        // get column of names
        String query = "SELECT * FROM " + TABLE_NAMES;
        Cursor cursor = db.rawQuery(query, null);
        // get names
        while (cursor.moveToNext()) {
            Person person = new Person();
            person.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_PERSON_ID)));
            person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            person.setFamily(cursor.getString(cursor.getColumnIndex(COLUMN_FAMILY)));
            result.add(person);
        }
        cursor.close();
        db.close();

        return result;
    }

    public List<String> loadRecipientNames() {
        List<String> result = new ArrayList<>();
        List<Person> personList = loadRecipients();
        for (Person person : personList) {
            result.add(person.getName());
        }
        return result;
    }

    private List<Integer> loadPersonIds() {
        List<Integer> result = new ArrayList<>();
        List<Person> personList = loadRecipients();

        for (Person person : personList) {
            result.add(person.getId());
        }
        return result;
    }

    // add new name to database
    public boolean addRecipient(Person person) {
        addFamily(person.getFamily());

        List<String> names = loadRecipientNames();
        // if name already in db or empty (only whitespace) name is entered, invalid
        if (names.contains(person.getName()) || person.getName().trim().isEmpty()) {
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, person.getName());
            values.put(COLUMN_FAMILY, person.getFamily());
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                // add new row to db with name
                db.insert(TABLE_NAMES, null, values);
                return true;
            } catch (SQLiteConstraintException e) {
                return false;
            } finally {
                db.close();
            }
        }
    }

    // add present for person for year (update row)
    public int addGivenPresentForYear(GivenPresent present) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, present.getYear());
        values.put(COLUMN_PRESENT, present.getPresent());
        values.put(COLUMN_NOTES, present.getNotes());
        values.put(COLUMN_BOUGHT, present.isBoughtInt());
        values.put(COLUMN_SENT, present.isSentInt());
        SQLiteDatabase db = this.getWritableDatabase();
        int presentId = (int) db.insert(TABLE_GIVEN_PRESENTS, null, values);
        for (Person person : present.getRecipientList()) {
            linkPresentToName(presentId, person.getId());
        }
        return presentId;
    }

    public void linkPresentToName(int presentId, int personId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRESENT_ID, presentId);
        values.put(COLUMN_PERSON_ID, personId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_GIVEN_PRESENTS_NAMES, null, values);
    }

    public void updateGivenPresentFromYear(GivenPresent present) {
        ContentValues values = new ContentValues();
        // user isn't currently able to change these
        // change recipient will have to be added with new function to update link table
//        values.put(COLUMN_PERSON_ID, present.getRecipient().getId());
//        values.put(COLUMN_YEAR, present.getYear());
        values.put(COLUMN_PRESENT, present.getPresent());
        values.put(COLUMN_NOTES, present.getNotes());
        values.put(COLUMN_BOUGHT, present.isBoughtInt());
        values.put(COLUMN_SENT, present.isSentInt());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_GIVEN_PRESENTS, values, COLUMN_PRESENT_ID + "=?",
                new String[] {String.valueOf(present.getPresentId())});
        db.close();
    }

    public boolean removeGivenPresentFromYear(GivenPresent present) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_GIVEN_PRESENTS +
                " WHERE " + COLUMN_PRESENT_ID + " = " + present.getPresentId();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            String id = String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_PRESENT_ID)));
            db.delete(TABLE_GIVEN_PRESENTS, COLUMN_PRESENT_ID + "=?", new String[] {id});
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }

    private void removeGivenPresentsForPerson(Person person) {
        List<GivenPresent> presentList = loadGivenPresents(person);
        for (GivenPresent present : presentList) {
            removeGivenPresentFromYear(present);
        }
    }

    private void deleteFamilyIfNoMoreMembers(String familyName) {
        List<Person> membersList = loadPeopleInFamily(familyName);
        if (membersList.size() == 0) {
            deleteFamily(new Family(familyName));
        }
    }

    // remove a name
    public boolean deletePerson(Person person) {
        boolean result = false;
        // remove recipient from names table
        String queryNamesTable = "SELECT * FROM " + TABLE_NAMES +
                " WHERE " + COLUMN_PERSON_ID + " = '" + person.getId() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryNamesTable, null);
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ID));
            db.delete(TABLE_NAMES, COLUMN_PERSON_ID + "=?", new String[] {id});
            result = true;
        }
        cursor.close();

        // remove any presents for person
        removeGivenPresentsForPerson(person);

        // remove family from db if no more members
        deleteFamilyIfNoMoreMembers(person.getFamily());

        //TODO remove person from received year tables

        db.close();
        return result;
    }

    // check if present has already been given to person
    public boolean isPresentAlreadyGiven(GivenPresent presentToCheck) {
        for (Person person : presentToCheck.getRecipientList()) {
            List<GivenPresent> givenPresents = loadGivenPresents(person);
            for (GivenPresent present : givenPresents) {
                if (present.getPresent().equals(presentToCheck.getPresent())) {
                    // present has already been given
                    return true;
                }
            }
        }
        return false;
    }
}
//TODO adapt rest of code to support multiple people per present etc
