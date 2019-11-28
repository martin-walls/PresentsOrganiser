package com.martinwalls.presentsorganiser.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martinwalls.presentsorganiser.Person;
import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.data.DBHandler;
import com.martinwalls.presentsorganiser.ui.misc.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private RecipientsViewModel viewModel;

    private List<Person> recipients = new ArrayList<>();
    private RecipientsAdapter recipientsAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        CustomRecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recipientsAdapter = new RecipientsAdapter(recipients);
        recyclerView.setAdapter(recipientsAdapter);
        TextView empty = view.findViewById(R.id.empty);
        recyclerView.setEmptyView(empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecipientsViewModel.class);

        recipients.clear();
        recipients.addAll(viewModel.getAllRecipients());
        recipientsAdapter.notifyDataSetChanged();
    }

}
