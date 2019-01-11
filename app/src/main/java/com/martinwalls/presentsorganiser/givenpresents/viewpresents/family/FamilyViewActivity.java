package com.martinwalls.presentsorganiser.givenpresents.viewpresents.family;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.GivenPresent;
import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.database.DBHandler;
import com.martinwalls.presentsorganiser.givenpresents.MainActivity;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.common.AddPresentDialog;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.common.DetailsDialog;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.common.GivenPresentsAdapter;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.recipient.RecipientViewActivity;
import com.martinwalls.presentsorganiser.ui.CustomRecyclerView;
import com.martinwalls.presentsorganiser.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO add events to add presents for -- xmas / birthday -- dropdown? -- new db col?
//TODO group same presents for people

public class FamilyViewActivity extends AppCompatActivity
        // handles positive click on add present dialog
        implements AddPresentDialog.addPresentDialogListener,
        // handles clicks on presents in list
        GivenPresentsAdapter.GivenPresentsAdapterListener,
        // handles positive click on details dialog
        DetailsDialog.DetailsDialogListener,
        // handles clicks on members in dialog
        ViewMembersDialog.ViewMembersDialogListener {

    private List<GivenPresent> presentList = new ArrayList<>();
    private GivenPresentsAdapter givenPresentsAdapter;
    private Family family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load person
        Intent intent = getIntent();
        String familyName = intent.getStringExtra(MainActivity.FAMILY_NAME);
        DBHandler dbHandler = new DBHandler(this);
        family = dbHandler.loadFamily(familyName);
        getSupportActionBar().setTitle(family.getFamilyName());

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(findViewById(R.id.empty));
        givenPresentsAdapter = new GivenPresentsAdapter(presentList, this, true);
        // load presents
        loadPresents();


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(givenPresentsAdapter);

        // set up fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPresentDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipient_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_people) {
            viewMembersDialog();
            return true;
        } else if (id == R.id.action_delete) {
            deleteNameDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewMembersDialog() {
        DialogFragment dialog = new ViewMembersDialog();

        Bundle bundle = new Bundle();
        bundle.putString(ViewMembersDialog.ARG_FAMILY_NAME, family.getFamilyName());
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "viewMembers");

    }

    private void deleteNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this family? " +
                "This will delete all family members.");
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFamily();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteFamily() {
        DBHandler dbHandler = new DBHandler(this);
        boolean result = dbHandler.deleteFamily(family);
        if (result) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);
        } else {
            Toast.makeText(this, "Person could not be deleted", Toast.LENGTH_SHORT).show();
        }
    }


    // get presents from db and add sections by year
    private void loadPresents() {
        DBHandler dbHandler = new DBHandler(this);
        List<GivenPresent> presents = new ArrayList<>();
        for (Person person : family.getFamilyMembers()) {
            presents.addAll(dbHandler.loadGivenPresents(person)); //TODO group same present that's given to multiple people
            I GOT TO HERE
        }

        int lastSection = 0;
        int size = presents.size();

        // sort by year
        Collections.sort(presents);
        // clear list so presents can be added with sections
        presentList.clear();

        for (int i = 0; i < size; i++) {
            GivenPresent present = presents.get(i);
            int thisSection = present.getYear();

            if (lastSection != thisSection) {
                lastSection = thisSection;
                present.setSection(true);
            }

            presentList.add(present);
        }

        givenPresentsAdapter.notifyDataSetChanged();
    }

    private void addPresentDialog() {
        DialogFragment dialog = new AddPresentDialog();

        Bundle bundle = new Bundle();
        bundle.putString(AddPresentDialog.ARG_FAMILY_NAME, family.getFamilyName());
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "addPresent");
    }

    @Override
    public void onAddPresentDialogPositiveClick(DialogFragment dialog, List<Person> selectedMembers) {
        NumberPicker yearPicker = dialog.getDialog().findViewById(R.id.yearPicker);
        int year = yearPicker.getValue();
        EditText present = dialog.getDialog().findViewById(R.id.present);
        EditText notes = dialog.getDialog().findViewById(R.id.notes);
        CheckBox bought = dialog.getDialog().findViewById(R.id.bought);
        CheckBox sent = dialog.getDialog().findViewById(R.id.sent);

        final DBHandler dbHandler = new DBHandler(this);


        final GivenPresent newPresent = new GivenPresent(year, selectedMembers,
                present.getText().toString(), notes.getText().toString(),
                bought.isChecked(), sent.isChecked());


        if (dbHandler.isPresentAlreadyGiven(newPresent)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have already given this present before. Are you sure you want to continue?");
            builder.setPositiveButton(R.string.dialog_add_anyway, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addPresentToDb(newPresent);
                    loadPresents();
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (newPresent.getPresent().trim().isEmpty()) {
            Toast.makeText(this, "Invalid present name", Toast.LENGTH_SHORT).show();
        } else {
            addPresentToDb(newPresent);
        }

//        for (Person person : selectedMembers) {
//
////            Person recipient = dbHandler.loadPerson(
////                    dbHandler.loadPersonId(recipientName.getText().toString(), family.getFamilyName()));
//            final GivenPresent newPresent = new GivenPresent(year, person,
//                    present.getText().toString(), notes.getText().toString(),
//                    bought.isChecked(), sent.isChecked());
//
//
//            if (dbHandler.isPresentAlreadyGiven(newPresent)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("You have already given this present before. Are you sure you want to continue?");
//                builder.setPositiveButton(R.string.dialog_add_anyway, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addPresentToDb(newPresent);
//                        loadPresents();
//                    }
//                });
//                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else if (newPresent.getPresent().trim().isEmpty()) {
//                Toast.makeText(this, "Invalid present name", Toast.LENGTH_SHORT).show();
//            } else {
//                addPresentToDb(newPresent);
//            }
//        }

        if (selectedMembers.size() == 0) {
            Toast.makeText(this, "You must select at least one person", Toast.LENGTH_SHORT).show();
        }

        loadPresents();
    }

    private void addPresentToDb(final GivenPresent present) {
        final DBHandler dbHandler = new DBHandler(this);

        // add present and get id of added present
        int generatedId = dbHandler.addGivenPresentForYear(present);
        present.setPresentId(generatedId);
        // if not added, id is -1
        if (generatedId > 0) {
            Snackbar.make(findViewById(android.R.id.content), "Success", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbHandler.removeGivenPresentFromYear(present);
                            loadPresents();
                        }
                    }).show();
        } else {
            Toast.makeText(this, "Error adding present", Toast.LENGTH_SHORT).show();
        }
    }

    // show details dialog on clicked
    @Override
    public void onPresentClicked(View view, GivenPresent present) {

        DialogFragment dialog = new DetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DetailsDialog.ARG_PRESENT_ID, present.getPresentId());
        bundle.putBoolean(DetailsDialog.ARG_SHOW_PERSON, true);

        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "details");
    }

    // update present if it has been changed
    @Override
    public void onDetailsDialogPositiveClick(DialogFragment dialog, GivenPresent present) {
        RelativeLayout editView = dialog.getDialog().findViewById(R.id.edit_view);
        if (editView.getVisibility() == View.VISIBLE) {
            // get views
            EditText etPresent = dialog.getDialog().findViewById(R.id.edit_present);
            EditText etNotes = dialog.getDialog().findViewById(R.id.edit_notes);
            CheckBox cbBought = dialog.getDialog().findViewById(R.id.checkbox_bought);
            CheckBox cbSent = dialog.getDialog().findViewById(R.id.checkbox_sent);
            // get values
            String newPresent = etPresent.getText().toString();
            String newNotes = etNotes.getText().toString();
            boolean newBought = cbBought.isChecked();
            boolean newSent = cbSent.isChecked();

            // check if any values have changed else don't update
            if (!present.isEqual(newPresent, newNotes, newBought, newSent)) {

                present.setPresent(newPresent);
                present.setNotes(newNotes);
                present.setBought(newBought);
                present.setSent(newSent);

                DBHandler dbHandler = new DBHandler(this);
                dbHandler.updateGivenPresentFromYear(present);
                Toast.makeText(this, "Present updated", Toast.LENGTH_SHORT).show();
            }
            loadPresents();
        }
    }

    @Override
    public void onPresentDeleteAction(DialogFragment dialog, final GivenPresent present) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently delete this present?");
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePresent(present);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deletePresent(GivenPresent present) {
        DBHandler dbHandler = new DBHandler(this);
        boolean result = dbHandler.removeGivenPresentFromYear(present);
        Toast.makeText(this, result ? "Present deleted" : "Not deleted",
                Toast.LENGTH_SHORT).show();
        loadPresents();
    }

    @Override
    public void onMemberClicked(String name) {
        DBHandler dbHandler = new DBHandler(this);
        int recipientId = dbHandler.loadPersonId(name, family.getFamilyName());

        Intent intent = new Intent(this, RecipientViewActivity.class);
        intent.putExtra(MainActivity.RECIPIENT_ID, recipientId);
        startActivity(intent);
    }
}
