package com.martinwalls.presentsorganiser;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO show list of all presents for year -- possibly new activity?
//TODO show list of all unbought presents (for current year)

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NamesAdapter.NamesAdapterListener,
        FamilyAdapter.FamilyAdapterListener {
    // shared preferences sort-by values
    public static final int SORTBY_FAMILY = 1;
    public static final int SORTBY_NAME = 2;
    private final int SORTBY_DEFAULT = SORTBY_NAME;

    public static final String RECIPIENT_ID = "com.martinwalls.presentsorganiser.RECIPIENT_ID";
    public static final String FAMILY_NAME = "com.martinwalls.presentsorganiser.FAMILY_NAME";

    private List<Person> personList = new ArrayList<>();
    private List<Family> familyList = new ArrayList<>();
    private CustomRecyclerView recyclerView;
    private NamesAdapter namesAdapter;
    private FamilyAdapter familyAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load the view to list all the names
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(findViewById(R.id.empty));

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortMode = sharedPref.getInt(getString(R.string.key_sort_names_by), SORTBY_DEFAULT);

        familyAdapter = new FamilyAdapter(familyList, this);
        namesAdapter = new NamesAdapter(personList, this);

        // load the names from database
        loadData();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (sortMode == SORTBY_FAMILY) {
            recyclerView.setAdapter(familyAdapter);
        } else {
            recyclerView.setAdapter(namesAdapter);
        }

        // set up FAB
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNameDialog(view);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //TODO re-enable when received presents implemented, disabled for now
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // associate searchable configuration with SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening for text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                namesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text changes
                namesAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_settings) {
            bottomSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadData() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortMode = sharedPref.getInt(getString(R.string.key_sort_names_by), SORTBY_DEFAULT);
        if (sortMode == SORTBY_FAMILY) {
            loadFamilies();
        } else {
            loadNames();
        }
    }

    private void loadNames() {
        // load list of names
        DBHandler dbHandler = new DBHandler(this);
        List<Person> peopleToShow = dbHandler.loadRecipients();

        Collections.sort(peopleToShow);

        personList.clear();

        int size = peopleToShow.size();
        String lastSection = "";

        for (int i = 0; i < size; i++) {
            Person person = peopleToShow.get(i);
            String thisSection = "";
            thisSection = person.getName().substring(0, 1);

            if (!lastSection.equals(thisSection)) {
                lastSection = thisSection;
                person.setSection(true);
                person.setSectionName(thisSection);
            }

            personList.add(person);
        }

        recyclerView.setAdapter(namesAdapter);

        namesAdapter.notifyDataSetChanged();
    }

    // load list of families
    private void loadFamilies() {
        DBHandler dbHandler = new DBHandler(this);
        List<Family> families = dbHandler.loadFamilies();

        List<Family> peopleNoFamily = dbHandler.loadPeopleNoFamily();
        families.addAll(peopleNoFamily);

        Collections.sort(families);

        familyList.clear();

        int size = families.size();
        String lastSection = "";

        for (int i = 0; i < size; i++) {
            Family family = families.get(i);
            String thisSection = family.getFamilyName().substring(0, 1);

            if (!lastSection.equals(thisSection)) {
                lastSection = thisSection;
                family.setSection(true);
            }

            familyList.add(family);
        }

        recyclerView.setAdapter(familyAdapter);

        familyAdapter.notifyDataSetChanged();
    }

    private void bottomSettingsDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_main, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_sort_by);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortMode = sharedPref.getInt(getString(R.string.key_sort_names_by), SORTBY_DEFAULT);
        switch (sortMode) {
            case SORTBY_FAMILY:
                radioGroup.check(R.id.radio_family);
                break;
            case SORTBY_NAME:
                radioGroup.check(R.id.radio_name);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                switch (checkedId) {
                    case R.id.radio_family:
                        editor.putInt(getString(R.string.key_sort_names_by), SORTBY_FAMILY);
                        editor.commit();
                        loadFamilies();
                        break;
                    case R.id.radio_name:
                        editor.putInt(getString(R.string.key_sort_names_by), SORTBY_NAME);
                        editor.commit();
                        loadNames();
                        break;
                }
                bottomSheetDialog.dismiss();
            }
        });

        TextView tvExportDb = dialogView.findViewById(R.id.export_db);
        tvExportDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
                bottomSheetDialog.dismiss();
            }
        });

        TextView tvImportDb = dialogView.findViewById(R.id.import_db);
        tvImportDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDBConfirmDialog();
                bottomSheetDialog.dismiss();
                loadData();
            }
        });


        bottomSheetDialog.setContentView(dialogView);

        bottomSheetDialog.show();
    }

    private void exportDB() {
        DBHandler dbHandler = new DBHandler(this);
        boolean result = dbHandler.exportDB();
        Toast.makeText(this, result ? "Success\nFile exported to 'Download' folder." : "Error exporting database",
                Toast.LENGTH_SHORT).show();
    }

    private void importDBConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View confirmView = inflater.inflate(R.layout.dialog_confirm_import_db, null);
        TextView message = confirmView.findViewById(R.id.message);
        String messageString = "Are you sure you want to import database? " +
                "You must have a previously exported file '" + DBHandler.DATABASE_NAME +
                "' in the 'Download' folder.";
        message.setText(messageString);
        builder.setView(confirmView);
        builder.setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importDB();
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void importDB() {
        DBHandler dbHandler = new DBHandler(this);
        boolean result = dbHandler.importDB();
        Toast.makeText(this, result ? "Success" : "Error importing database. File must be in Download folder.",
                Toast.LENGTH_SHORT).show();
    }

    private void addNameDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
//        builder.setTitle("Enter name:");

        View addNameView = inflater.inflate(R.layout.dialog_add_name, null);
        final EditText etName = addNameView.findViewById(R.id.et_name);
        final AutoCompleteTextView etFamily = addNameView.findViewById(R.id.et_family);

        DBHandler dbHandler = new DBHandler(this);
        List<String> familiesList = dbHandler.loadFamilyNames();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, familiesList);
        etFamily.setAdapter(adapter);
        etFamily.setThreshold(1);

        builder.setView(addNameView);

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Person personToAdd = new Person();
                personToAdd.setName(etName.getText().toString());
                personToAdd.setFamily(etFamily.getText().toString());
                addName(view, personToAdd);
                loadData();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // auto show keyboard when dialog opens
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etName.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        etName.requestFocus();

        builder.show();
    }

    private void addName(View view, Person person) {
        final DBHandler dbHandler = new DBHandler(this);
        boolean result = false;
        if (!person.getName().trim().isEmpty()) {
            result = dbHandler.addRecipient(person);
        } else {
            Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        // display appropriate message
        if (result) {
//            Snackbar.make(view, "Success", Snackbar.LENGTH_LONG)
//                .setAction("Undo", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dbHandler.deleteRecipient(name);
//                        loadNames();
//                    }
//                }).show();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } else {
//            Snackbar.make(view, "Invalid name", Snackbar.LENGTH_LONG).show();
            Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNameSelected(Person person) {
//        Toast.makeText(this, "Item clicked: " + name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecipientViewActivity.class);
        intent.putExtra(RECIPIENT_ID, person.getId());
        startActivity(intent);
    }

    @Override
    public void onFamilySelected(Family family) {
        if (family.isPersonWithoutFamily()) {
            Intent intent = new Intent(this, RecipientViewActivity.class);
            intent.putExtra(RECIPIENT_ID, family.getFamilyMembers().get(0).getId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, FamilyViewActivity.class);
            intent.putExtra(FAMILY_NAME, family.getFamilyName());
            startActivity(intent);
        }
    }
}
