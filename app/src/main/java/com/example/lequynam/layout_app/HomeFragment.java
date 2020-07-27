package com.example.lequynam.layout_app;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Common.Common;

import static com.facebook.AccessTokenManager.SHARED_PREFERENCES_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ImageView imageViewShop, imageViewTrans, imageViewGetMoney, imageViewGetCoin, imageViewLogout;
    ProfilePictureView profilePictureView;
    TextView textViewUserName, textViewCoinHome, textViewCashHome;

    private TransFragment transFragment;
    private CoinFragment coinFragment;
    String coinNum,  cashNum;

    public HomeFragment() {
        // Required empty public constructor
    }

    String fb_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        imageViewShop = (ImageView) view.findViewById(R.id.imageViewShopping);
        imageViewTrans = (ImageView) view.findViewById(R.id.imageViewTrans);
        imageViewGetCoin = (ImageView) view.findViewById(R.id.imageViewGetCoin);
        imageViewGetMoney = (ImageView) view.findViewById(R.id.imageViewGetMoney);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
        textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewCashHome = (TextView) view.findViewById(R.id.textViewCashHome);
        textViewCoinHome = (TextView) view.findViewById(R.id.textViewCoinHome);
        imageViewLogout = (ImageView) view.findViewById(R.id.imageViewLogout);

        textViewCashHome.setText(Common.cash);
        textViewUserName.setText(Common.fb_name);


        fb_id = getArguments().getString("fb_id");


        getBalance("http://192.168.0.222:8000/balance");

        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        imageViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).initiateScan();
            }
        });

        imageViewGetMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).initiateScan();

            }
        });

        imageViewGetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinFragment = new CoinFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("coinNum", textViewCoinHome.getText().toString());
                bundle.putString("cashNum", textViewCashHome.getText().toString());
                coinFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_frame, coinFragment, "coin");
                fragmentTransaction.commit();


            }
        });

        imageViewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transFragment = new TransFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("coinNum", textViewCoinHome.getText().toString());
                bundle.putString("cashNum", textViewCashHome.getText().toString());
                transFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_frame, transFragment, "trans");
                fragmentTransaction.commit();
            }
        });

        return view;

    }


    //Get Balance
    public void getBalance(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    JSONObject object = new JSONObject(response);
                    textViewCoinHome.setText(object.getString("balance"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(HomeActivity.this, "Xay ra loi", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("id", fb_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public class cashObj implements Serializable {
        String cashNum;
        String coinNum;

        // GETTERS AND SETTERS
    }
    public void setText(String text){
        textViewUserName.setText(text);
    }



}
