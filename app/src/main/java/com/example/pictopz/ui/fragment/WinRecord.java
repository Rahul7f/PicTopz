package com.example.pictopz.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pictopz.R;
import com.example.pictopz.adapters.ProfileContestAdapter;
import com.example.pictopz.adapters.WinRecordAdapter;


public class WinRecord extends Fragment {

    RecyclerView recyclerView;
    WinRecordAdapter winRecordAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_win_record, container, false);

        recyclerView = view.findViewById(R.id.win_Record_recycleView);
        winRecordAdapter = new WinRecordAdapter();
        recyclerView.setAdapter(winRecordAdapter);


        return  view;
    }
}