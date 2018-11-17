package com.martinwalls.presentsorganiser.givenpresents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.martinwalls.presentsorganiser.Family;
import com.martinwalls.presentsorganiser.R;

import java.util.ArrayList;
import java.util.List;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.MyViewHolder>
        implements Filterable {
    private final int SECTION_VIEW = 0;
    private final int CONTENT_VIEW = 0;

    private List<Family> familyList;
    private List<Family> familyListFiltered;
    private FamilyAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, letter, info;

        public MyViewHolder(View view) {
            super(view);
            letter = view.findViewById(R.id.letter);
            name = view.findViewById(R.id.name);
            info = view.findViewById(R.id.info);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFamilySelected(familyListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public FamilyAdapter(List<Family> namesList, FamilyAdapterListener listener) {
        this.familyList = namesList;
        this.familyListFiltered = namesList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_name, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (familyListFiltered.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (getItemViewType(position) == SECTION_VIEW) {
            Family family = familyListFiltered.get(position);
            holder.letter.setText(family.getFamilyName().substring(0, 1));
            holder.name.setText(family.getFamilyName());
            String numMembers = family.getFamilyMembers().size() +
                    (family.getFamilyMembers().size() == 1 ? " Person" : " People");
            holder.info.setText(numMembers);
        } else {
            Family family = familyListFiltered.get(position);
            holder.name.setText(family.getFamilyName());
            String numMembers = family.getFamilyMembers().size() +
                    (family.getFamilyMembers().size() == 1 ? " Person" : " People");
            holder.info.setText(numMembers);
        }
    }

    @Override
    public int getItemCount() {
        return familyListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    familyListFiltered = familyList;
                } else {
                    List<Family> filteredList = new ArrayList<>();
                    for (Family family : familyList) {
                        // check if name contains search string
                        if (family.getFamilyName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(family);
                        }
                    }
                    familyListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = familyListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                // update recycler view
                familyListFiltered = (ArrayList<Family>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface FamilyAdapterListener {
        void onFamilySelected(Family family);
    }
}

