package com.example.hp.musicapp;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FirstFragment extends Fragment {

public FirstFragment() {
// Required empty public constructor
}

@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
Bundle savedInstanceState) {
// Inflate the layout for this fragment
return inflater.inflate(R.layout.pdfview, container, false);
}

}