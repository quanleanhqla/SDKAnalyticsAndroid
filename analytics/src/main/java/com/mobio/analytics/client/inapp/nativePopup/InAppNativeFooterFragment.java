package com.mobio.analytics.client.inapp.nativePopup;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.R;

public class InAppNativeFooterFragment extends InAppNativeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_app_native_footer, container, false);

        ImageView imvHeaderIcon = view.findViewById(R.id.footer_icon);
        setUpImage(push.getAlert().getString("background_image"), imvHeaderIcon);

        TextView tvTitle = view.findViewById(R.id.footer_title);
        setUpText(tvTitle, push.getAlert().getTitle());

        TextView tvContent = view.findViewById(R.id.footer_message);
        setUpText(tvContent, push.getAlert().getBody());

        Button btnHeaderButton1 = view.findViewById(R.id.footer_button_1);
        setUpButton(btnHeaderButton1, "Đồng ý");

        Button btnHeaderButton2 = view.findViewById(R.id.footer_button_2);
        setUpButton(btnHeaderButton2, "Hủy");

        return view;
    }
}