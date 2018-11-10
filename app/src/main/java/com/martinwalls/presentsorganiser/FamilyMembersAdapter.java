package com.martinwalls.presentsorganiser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FamilyMembersAdapter extends RecyclerView.Adapter<FamilyMembersAdapter.MyViewHolder> {
    private List<Person> members;
    private FamilyMembersAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            //TODO delete / edit buttons

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMemberClicked(name.getText().toString());
                }
            });
        }
    }

    public FamilyMembersAdapter(Family family, FamilyMembersAdapterListener listener) {
        this.members = family.getFamilyMembers();
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_view_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Person person = members.get(position);
        holder.name.setText(person.getName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public interface  FamilyMembersAdapterListener {
        void onMemberClicked(String name);
    }
}
