package com.martinwalls.presentsorganiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PresentsActivity extends AppCompatActivity
        implements PresentsAdapter.PresentsAdapterListener {

    public static String PRESENTS_TO_SHOW = "presents_to_show";
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

        // load presents
        DBHandler dbHandler = new DBHandler(this);
        presentList = dbHandler.loadUnboughtPresents();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(findViewById(R.id.empty));
        presentsAdapter = new PresentsAdapter(presentList, this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(presentsAdapter);
    }



    @Override
    public void onPresentClicked(View view, GivenPresent present) {
        DialogFragment dialog = new DetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DetailsDialog.ARG_PRESENT_ID, present.getPresentId());

        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "details");
    }
}
