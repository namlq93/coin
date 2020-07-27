package com.example.lequynam.layout_app;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import Common.Common;

import static Common.Common.fb_id;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoinFragment extends Fragment {

    Button buttonBuyCoin;
    EditText editTextBuyCoin;
    TextView textViewCoin, textViewUsd;
    int notifNum;
    //String txtCoin;
    float coin;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.222:8000/");
        } catch (URISyntaxException e) {}
    }

    public CoinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_coin, container, false);
        buttonBuyCoin = (Button) view.findViewById(R.id.buttonBuyCoin);
        editTextBuyCoin = (EditText) view.findViewById(R.id.editTextBuyCoin);
        textViewCoin = (TextView) view.findViewById(R.id.textViewCoin);
        textViewUsd = (TextView) view.findViewById(R.id.textViewUsd);

        //Toast.makeText(getActivity(), txtCoin, Toast.LENGTH_SHORT).show();
        String cashNum = getArguments().getString("cashNum");
        String coinNum = getArguments().getString("coinNum").substring(0, 6);
        textViewCoin.setText(coinNum);
        textViewUsd.setText(cashNum);

        buttonBuyCoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextBuyCoin.getText())){
                    editTextBuyCoin.setError("Please enter your coin number");
                } else {
                    //txtCoin = editTextBuyCoin.getText().toString();
                     coin = Float.parseFloat(editTextBuyCoin.getText().toString());
                    try {
                        final JSONObject obj = new JSONObject("{type: request}");
                        obj.put("ncoin", coin);
                        obj.put("id", Common.fb_id);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Thong bao!");
                        alertDialog.setMessage("Are you sure??");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mSocket.connect();
                                mSocket.emit("sendtrans", obj );
                                mSocket.on("sendtransAn", onNewMessage2);
                                mSocket.on("transcomplete", onNewMessage);
                                notifNum = Integer.parseInt(Common.notif) + 1;
                                Common.notif = String.valueOf(notifNum);
                            }
                        });

                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return  view;
    }




    //get data from server socket
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String status = data.getString("status");
                        if(status.equalsIgnoreCase("true")){
                            AHBottomNavigation bottomNavigation = (AHBottomNavigation) getActivity().findViewById(R.id.bottom_navigation);
                            bottomNavigation.setNotification(String.valueOf(notifNum), 1);

                            float newcash = Math.round(Float.parseFloat(Common.cash) - 69.96f* coin);
                            Common.cash = Float.toString(newcash);
                            textViewUsd.setText(Common.cash);
                            float newcoin = Float.parseFloat(textViewCoin.getText().toString()) + coin;
                            textViewCoin.setText(Float.toString(newcoin).substring(0, 6));
                            Common.arrayNotif.add("Buy coin success!!");
                        } else {
                            Toast.makeText(getActivity(), "Get Error! Please check your network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(getActivity(), Float.toString(newcash), Toast.LENGTH_SHORT).show();

//                    TextView textViewCashHome = (TextView) getActivity().findViewById(R.id.textViewCashHome);
//                    textViewCashHome.setText(Common.cash);

                    //Toast.makeText(getActivity(), txtCoin, Toast.LENGTH_SHORT).show();

                }
            });
        }
    };


    //get data from server socket
    private Emitter.Listener onNewMessage2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String status = data.getString("status");
                        if(status.equalsIgnoreCase("true")){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Thong bao!");
                            alertDialog.setMessage("Transaction is processing. Please wait a moment..");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            alertDialog.show();
                        } else {
                            Toast.makeText(getActivity(), "Get Error! Please check your network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //Toast.makeText(getActivity(), "pending", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}
