package com.example.lequynam.layout_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    TextView textViewWallet;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_acount, container, false);

        textViewWallet = (TextView) view.findViewById(R.id.textViewWallet);
        String wallet = getArguments().getString("wallet");

        textViewWallet.setText(wallet);
        return view;
    }

}
