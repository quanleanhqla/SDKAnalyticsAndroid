package com.mobio.analytics.client.inapp.nativePopup;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.R;


public class InAppNativeHeaderFragment extends InAppNativeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_app_native_header, container, false);
        ImageView imvFooterIcon = view.findViewById(R.id.header_icon);
        setUpImage(push.getAlert().getString("background_image"), imvFooterIcon);

        TextView tvFooterTitle = view.findViewById(R.id.header_title);
        setUpText(tvFooterTitle, push.getAlert().getTitle());

        TextView tvFooterContent = view.findViewById(R.id.header_message);
        setUpText(tvFooterContent, push.getAlert().getBody());

        Button btnFooterButton1 = view.findViewById(R.id.header_button_1);
        setUpButton(btnFooterButton1, "Đồng ý");

        Button btnFooterButton2 = view.findViewById(R.id.header_button_2);
        setUpButton(btnFooterButton2, "Hủy");
        return view;
    }
}