package com.example.pictopz.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pictopz.R;
import com.example.pictopz.adapters.ProfileContestAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ProfileUpcomingContest extends Fragment {
    RecyclerView recyclerView;
    ProfileContestAdapter contestAdapter;
    BottomNavigationView navBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile_upcoming_contest, container, false);
        recyclerView = view.findViewById(R.id.profile_contest_Recycleview);
        contestAdapter = new ProfileContestAdapter();
        recyclerView.setAdapter(contestAdapter);

        return  view;
    }



}