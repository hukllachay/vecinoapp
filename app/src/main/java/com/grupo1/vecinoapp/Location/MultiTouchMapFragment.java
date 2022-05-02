package com.grupo1.vecinoapp.Location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;


public class MultiTouchMapFragment extends SupportMapFragment {

    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        mOriginalContentView = super.onCreateView(layoutInflater, viewGroup, bundle);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Nullable
    @Override
    public View getView() {
        return mOriginalContentView;
    }
}
