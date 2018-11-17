package com.martinwalls.presentsorganiser.givenpresents.viewpresents.family;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.database.DBHandler;

public class ViewMembersDialog extends DialogFragment
        implements FamilyMembersAdapter.FamilyMembersAdapterListener {
    private ViewMembersDialogListener listener;
    private Family family;

    public static final String ARG_FAMILY_NAME = "FAMILY_NAME";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            DBHandler dbHandler = new DBHandler(getContext());
            family = dbHandler.loadFamily(getArguments().getString(ARG_FAMILY_NAME));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate and set layout for dialog
        // parent is null as its going in dialog layout
        View membersView = inflater.inflate(R.layout.dialog_view_members, null);
        builder.setView(membersView);
        builder.setTitle("Family members:");

        // set up recycler view
        RecyclerView recyclerView = membersView.findViewById(R.id.recycler_view);
        FamilyMembersAdapter adapter = new FamilyMembersAdapter(family, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // add buttons
        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onMemberClicked(String name) {
        listener.onMemberClicked(name);
    }

    public interface ViewMembersDialogListener {
        void onMemberClicked(String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify host activity implements callback interface
        try {
            // instantiate listener so we can send events
            listener = (ViewMembersDialogListener) context;
        } catch (ClassCastException e) {
            // activity doesn't implement interface
            throw new ClassCastException(getActivity().toString()
                    + " must implement ViewMembersDialogListener");
        }
    }
}
