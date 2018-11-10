package com.martinwalls.presentsorganiser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.MyViewHolder>
        implements Filterable {
    private final int SECTION_VIEW = 0;
    private final int CONTENT_VIEW = 0;

    private List<Person> personList;
    private List<Person> personListFiltered;
    private NamesAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, letter;

        public MyViewHolder(View view) {
            super(view);
            letter = view.findViewById(R.id.letter);
            name = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNameSelected(personListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public NamesAdapter(List<Person> namesList, NamesAdapterListener listener) {
        this.personList = namesList;
        this.personListFiltered = namesList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (personListFiltered.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (getItemViewType(position) == SECTION_VIEW) {
            Person person = personListFiltered.get(position);
            holder.letter.setText(person.getSectionName());
            holder.name.setText(person.getName());
        } else {
            Person person = personListFiltered.get(position);
            holder.name.setText(person.getName());
        }
    }

    @Override
    public int getItemCount() {
        return personListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    personListFiltered = personList;
                } else {
                    List<Person> filteredList = new ArrayList<>();
                    for (Person person : personList) {
                        // check if name contains search string
                        if (person.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(person);
                        }
                    }
                    personListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = personListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                // update recycler view
                personListFiltered = (ArrayList<Person>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NamesAdapterListener {
        void onNameSelected(Person person);
    }
}
