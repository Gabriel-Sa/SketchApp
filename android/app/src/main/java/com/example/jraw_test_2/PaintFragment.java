package com.example.jraw_test_2;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PaintFragment extends Fragment {

    PaintView paintView;

//    public PaintFragment() {
//
//    }
//
//    public static PaintFragment getInstance() {
//        PaintFragment fragment = new PaintFragment();
//        fragment.setRetainInstance(true);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paint, container, false);

        paintView = (PaintView) view.findViewById(R.id.PaintView);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}