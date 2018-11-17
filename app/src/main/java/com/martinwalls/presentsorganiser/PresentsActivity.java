package com.martinwalls.presentsorganiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PresentsActivity extends AppCompatActivity
        implements PresentsAdapter.PresentsAdapterListener,
        DetailsDialog.DetailsDialogListener {

    public static String PRESENTS_TO_SHOW = "com.martinwalls.PRESENTS_TO_SHOW";
    public static int UNBOUGHT = 1;
    public static int UNSENT = 2;


    private List<GivenPresent> presentList = new ArrayList<>();
    private CustomRecyclerView recyclerView;
    private PresentsAdapter presentsAdapter;
    private int presentsToShow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presents);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        presentsToShow = intent.getIntExtra(PRESENTS_TO_SHOW, UNBOUGHT);
        getSupportActionBar().setTitle(presentsToShow == UNBOUGHT ? "Unbought presents" : "Unsent presents");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(findViewById(R.id.empty));
        presentsAdapter = new PresentsAdapter(presentList, this);

        // load presents
        loadPresents();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(presentsAdapter);
    }

    private void loadPresents() {
        DBHandler dbHandler = new DBHandler(this);
        presentList.clear();
        presentList.addAll(dbHandler.loadUnboughtPresents());
        presentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPresentClicked(View view, GivenPresent present) {
        DialogFragment dialog = new DetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DetailsDialog.ARG_PRESENT_ID, present.getPresentId());
        bundle.putBoolean(DetailsDialog.ARG_SHOW_PERSON, true);

        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "details");
    }

    @Override
    public void onDetailsDialogPositiveClick(DialogFragment dialog, GivenPresent present) {
        RelativeLayout editView = dialog.getDialog().findViewById(R.id.editview);
        if (editView.getVisibility() == View.VISIBLE) {
            // get views
            EditText etPresent = dialog.getDialog().findViewById(R.id.editview_present);
            EditText etNotes = dialog.getDialog().findViewById(R.id.editview_notes);
            CheckBox cbBought = dialog.getDialog().findViewById(R.id.editview_bought);
            CheckBox cbSent = dialog.getDialog().findViewById(R.id.editview_sent);
            // get values
            String newPresent = etPresent.getText().toString();
            String newNotes = etNotes.getText().toString();
            boolean newBought = cbBought.isChecked();
            boolean newSent = cbSent.isChecked();

            // check if any values have changed else don't update
            if (!(newPresent.equals(present.getPresent()) && newNotes.equals(present.getNotes())
                    && newBought == present.isBought())) {

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
}
