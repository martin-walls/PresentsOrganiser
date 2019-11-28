package com.martinwalls.presentsorganiser.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.data.model.Recipient;

import java.util.List;

public class RecipientsAdapter extends RecyclerView.Adapter<RecipientsAdapter.ViewHolder> {

    private List<Person> recipients;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView family;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            family = view.findViewById(R.id.family);
        }
    }

    public RecipientsAdapter(List<Person> recipients) {
        this.recipients = recipients;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipient, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person recipient = recipients.get(position);
        holder.name.setText(recipient.getName());
        if (!TextUtils.isEmpty(recipient.getFamily())) {
            holder.family.setText(recipient.getFamily());
        }
    }

    @Override
    public int getItemCount() {
        return recipients.size();
    }
}
