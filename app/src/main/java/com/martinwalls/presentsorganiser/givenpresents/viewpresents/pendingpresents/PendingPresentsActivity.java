package com.martinwalls.presentsorganiser.givenpresents.viewpresents.pendingpresents;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.martinwalls.presentsorganiser.GivenPresent;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.data.DBHandler;
import com.martinwalls.presentsorganiser.givenpresents.viewpresents.common.DetailsDialog;
import com.martinwalls.presentsorganiser.ui.misc.CustomRecyclerView;
import com.martinwalls.presentsorganiser.ui.misc.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PendingPresentsActivity extends AppCompatActivity
        implements PendingPresentsAdapter.PendingPresentsAdapterListener,
        DetailsDialog.DetailsDialogListener {

    public static String PRESENTS_TO_SHOW = "com.martinwalls.PRESENTS_TO_SHOW";
    public static int UNBOUGHT = 0;
    public static int UNSENT = 1;


    private List<GivenPresent> presentList = new ArrayList<>();
    private CustomRecyclerView recyclerView;
    private PendingPresentsAdapter pendingPresentsAdapter;
    private int presentsToShow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_presents);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        presentsToShow = intent.getIntExtra(PRESENTS_TO_SHOW, UNBOUGHT);
        getSupportActionBar().setTitle(presentsToShow == UNBOUGHT ? "Unbought presents" : "Unsent presents");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(findViewById(R.id.empty));
        pendingPresentsAdapter = new PendingPresentsAdapter(presentList, this);

        // load presents
        loadPresents();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pendingPresentsAdapter);
    }

    private void loadPresents() {
        DBHandler dbHandler = new DBHandler(this);
        presentList.clear();
        if (presentsToShow == UNBOUGHT) {
            presentList.addAll(dbHandler.loadPendingPresents(UNBOUGHT));
        } else if (presentsToShow == UNSENT) {
            presentList.addAll(dbHandler.loadPendingPresents(UNSENT));
        }
        pendingPresentsAdapter.notifyDataSetChanged();
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
}
