package com.martinwalls.presentsorganiser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsDialog extends DialogFragment {
    DetailsDialogListener listener;
    private GivenPresent present = new GivenPresent();
    private RelativeLayout nonEditView, editView;
    private boolean isFromFamilyView = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            int presentId = getArguments().getInt(RecipientViewActivity.ARG_PRESENT_ID);
            DBHandler dbHandler = new DBHandler(getContext());
            present = dbHandler.loadPresent(presentId);
            if (getArguments().containsKey(FamilyViewActivity.ARG_FROM_FAMILY_VIEW)) {
                isFromFamilyView = getArguments().getBoolean(FamilyViewActivity.ARG_FROM_FAMILY_VIEW);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate and set layout for dialog
        View presentDetailsView = inflater.inflate(R.layout.dialog_present_details, null);
        nonEditView = presentDetailsView.findViewById(R.id.normalview);
        editView = presentDetailsView.findViewById(R.id.editview);

        ImageButton editBtn = presentDetailsView.findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditView();
            }
        });
        ImageButton deleteBtn = presentDetailsView.findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close the dialog and call listener
                DetailsDialog.this.getDialog().cancel();
                listener.onPresentDeleteAction(DetailsDialog.this, present);
            }
        });

        // show recipient name if in family view
        TextView tvRecipientName = presentDetailsView.findViewById(R.id.recipient);
        if (isFromFamilyView) {
            tvRecipientName.setText(present.getRecipient().getName());
        } else {
            tvRecipientName.setVisibility(View.GONE);
        }

        builder.setView(presentDetailsView);
//        builder.setTitle(present.getPresent());

        TextView txt_present = presentDetailsView.findViewById(R.id.present);
        txt_present.setText(present.getPresent());
        TextView notes = presentDetailsView.findViewById(R.id.notes);
        if (!present.getNotes().isEmpty()) {
            notes.setText(present.getNotes());
        } else {
            notes.setVisibility(View.GONE);
        }

        TextView bought = presentDetailsView.findViewById(R.id.bought);
        bought.setText(present.isBought() ? "Bought" : "Not bought");
        TextView sent = presentDetailsView.findViewById(R.id.sent);
        sent.setText(present.isSent() ? "Sent" : "Not sent");

        // set buttons
        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDetailsDialogPositiveClick(DetailsDialog.this, present);
                    }
                });

        return builder.create();
    }

    public interface DetailsDialogListener {
        void onDetailsDialogPositiveClick(DialogFragment dialog, GivenPresent originalPresent);
        void onPresentDeleteAction(DialogFragment dialog, GivenPresent present);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify host activity implements callback interface
        try {
            // instantiate listener so we can send events
            listener = (DetailsDialogListener) context;
        } catch (ClassCastException e) {
            // activity doesn't implement interface
            throw new ClassCastException(getActivity().toString()
                    + " must implement DetailsDialogListener");
        }
    }

    private void showEditView() {
        nonEditView.setVisibility(View.GONE);
        editView.setVisibility(View.VISIBLE);
        TextView tvTitle = editView.findViewById(R.id.editview_title);
        tvTitle.setText(present.getPresent());
        TextView tvRecipient = editView.findViewById(R.id.editview_recipient);
        tvRecipient.setText(present.getRecipient().getName());
        EditText etPresent = editView.findViewById(R.id.editview_present);
        etPresent.setText(present.getPresent());
        EditText etNotes = editView.findViewById(R.id.editview_notes);
        etNotes.setText(present.getNotes());
        CheckBox cbBought = editView.findViewById(R.id.editview_bought);
        cbBought.setChecked(present.isBought());
        CheckBox cbSent = editView.findViewById(R.id.editview_sent);
        cbSent.setChecked(present.isSent());
    }
}
