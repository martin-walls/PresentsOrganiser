package com.martinwalls.presentsorganiser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GivenPresentsAdapter extends RecyclerView.Adapter<GivenPresentsAdapter.ItemViewHolder> {
    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    private List<GivenPresent> presentList;
    private GivenPresentsAdapterListener listener;
    private boolean isFamilyView;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView year, present, recipient;
        public ImageView bought;

        public ItemViewHolder(View view) {
            super(view);
            year = view.findViewById(R.id.year);
            present = view.findViewById(R.id.present);
            recipient = view.findViewById(R.id.recipient);
            bought = view.findViewById(R.id.img_bought);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPresentClicked(v, presentList.get(getAdapterPosition()));
                }
            });
//
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    listener.onPresentLongClicked(v, presentList.get(getAdapterPosition()));
//                    return true;
//                }
//            });
        }
    }

    public GivenPresentsAdapter(List<GivenPresent> presentList, GivenPresentsAdapterListener listener,
                                boolean isFamilyView) {
        this.presentList = presentList;
        this.listener = listener;
        this.isFamilyView = isFamilyView;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.present_list_row, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (presentList.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        if (getItemViewType(position) == SECTION_VIEW) {
            GivenPresent present = presentList.get(position);
            holder.year.setText(String.valueOf(present.getYear()));
            holder.present.setText(present.getPresent());
            holder.recipient.setText(present.getRecipient().getName());
            holder.recipient.setVisibility(isFamilyView ? View.VISIBLE : View.GONE);
            holder.bought.setVisibility(present.isBought() ? View.VISIBLE : View.GONE);
        } else {
            GivenPresent present = presentList.get(position);
            holder.present.setText(present.getPresent());
            holder.recipient.setText(present.getRecipient().getName());
            holder.recipient.setVisibility(isFamilyView ? View.VISIBLE : View.GONE);
            holder.bought.setVisibility(present.isBought() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return presentList.size();
    }

    public interface GivenPresentsAdapterListener {
        void onPresentClicked(View view, GivenPresent present);
//        void onPresentLongClicked(View view, GivenPresent present);
    }
}
