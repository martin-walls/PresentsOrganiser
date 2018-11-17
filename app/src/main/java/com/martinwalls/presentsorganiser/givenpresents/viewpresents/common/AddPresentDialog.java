package com.martinwalls.presentsorganiser.givenpresents.viewpresents.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;

import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//TODO checkboxes for recipients rather than editText -- checkbox for "all"

public class AddPresentDialog extends DialogFragment {
    addPresentDialogListener listener;
    private Family family = new Family();
    private boolean showNameField = false;

    public static final String ARG_FAMILY_NAME = "FAMILY_NAME";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            DBHandler dbHandler = new DBHandler(getContext());
            family = dbHandler.loadFamily(
                    getArguments().getString(ARG_FAMILY_NAME));
            showNameField = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate and set layout for dialog
        // parent is null as its going in dialog layout
        View addPresentView = inflater.inflate(R.layout.dialog_add_present, null);
        builder.setView(addPresentView);
        builder.setTitle("Add present");

        final AutoCompleteTextView etName = addPresentView.findViewById(R.id.recipient);
        if (showNameField) {
            // set up name autocomplete
            List<Person> membersList = family.getFamilyMembers();
            List<String> memberNames = new ArrayList<>();
            for (Person person : membersList) {
                memberNames.add(person.getName());
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, memberNames);
            etName.setAdapter(adapter);
            etName.setThreshold(1);
            etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    etName.showDropDown();
                }
            });
        } else {
            etName.setVisibility(View.GONE);
        }

        // set up year selector wheel;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        final NumberPicker yearPicker = addPresentView.findViewById(R.id.yearPicker);
        yearPicker.setMaxValue(year);
        yearPicker.setMinValue(year-5);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // add buttons
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send positive click event
                        listener.onAddPresentDialogPositiveClick(AddPresentDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public interface addPresentDialogListener {
        void onAddPresentDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify host activity implements callback interface
        try {
            // instantiate listener so we can send events
            listener = (addPresentDialogListener) context;
        } catch (ClassCastException e) {
            // activity doesn't implement interface
            throw new ClassCastException(getActivity().toString()
                    + " must implement AddGivenPresentDialogListener");
        }
    }
}
