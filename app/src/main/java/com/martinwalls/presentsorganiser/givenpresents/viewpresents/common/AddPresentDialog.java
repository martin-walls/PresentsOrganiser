package com.martinwalls.presentsorganiser.givenpresents.viewpresents.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPresentDialog extends DialogFragment {
    addPresentDialogListener listener;
    public static final String ARG_FAMILY_NAME = "FAMILY_NAME";

    private Family family = new Family();
    private boolean showNameField = false;
    private List<Person> selectedMembers = new ArrayList<>();

    private boolean[] checkedMembers;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            DBHandler dbHandler = new DBHandler(getContext());
            family = dbHandler.loadFamily(
                    getArguments().getString(ARG_FAMILY_NAME));
            showNameField = true;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate and set layout for dialog
        // parent is null as its going in dialog layout
        View addPresentView = inflater.inflate(R.layout.dialog_add_present, null);
        builder.setView(addPresentView);
        builder.setTitle("Add present");

        Button btnRecipients = addPresentView.findViewById(R.id.btn_select_recipients);
        if (showNameField) {
            // set up name multi choice dialog
            checkedMembers = new boolean[family.getFamilyMembers().size()];
            btnRecipients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectRecipientsDialog();
                }
            });
        } else {
            btnRecipients.setVisibility(View.GONE);
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
                        listener.onAddPresentDialogPositiveClick(AddPresentDialog.this, selectedMembers);
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

    private void selectRecipientsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final List<Person> members = family.getFamilyMembers();
        final List<String> names = new ArrayList<>();
        for (Person person : members) {
            names.add(person.getName());
        }

        final String[] namesArray = names.toArray(new String[0]);

        builder.setMultiChoiceItems(namesArray, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // update current focused item's checked status
                checkedMembers[which] = isChecked;
            }
        });

        builder.setCancelable(false);
        builder.setTitle("Select recipients");

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < names.size(); i++) {
                    boolean checked = checkedMembers[i];
                    Person person = members.get(i);
                    if (checked && !selectedMembers.contains(person)) {
                        selectedMembers.add(person);
                    } else if (!checked) {
                        if (selectedMembers.contains(person)) {
                            selectedMembers.remove(person);
                        }
                    }
                }
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

    public interface addPresentDialogListener {
        void onAddPresentDialogPositiveClick(DialogFragment dialog, List<Person> selectedMembers);
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
