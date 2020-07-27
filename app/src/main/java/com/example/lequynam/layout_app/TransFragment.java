package com.example.lequynam.layout_app;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import Common.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransFragment extends Fragment {

    Button buttonSenMoney;
    EditText editTextCoin, editTextUsd, editTextWallet;
    ImageView imageViewQr;
    float coin, usd;
    int notifNum;
    TextView textViewCoin, textViewUsd;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.222:8000/");
        } catch (URISyntaxException e) {}
    }


    public TransFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_trans, container, false);
        buttonSenMoney = (Button) view.findViewById(R.id.buttonSendMoney);
        editTextCoin = (EditText) view.findViewById(R.id.editTextBuyCoin);
        imageViewQr = (ImageView) view.findViewById(R.id.imageViewQr);
        editTextWallet = (EditText) view.findViewById(R.id.editTextWallet);

        textViewCoin = (TextView) view.findViewById(R.id.textViewCoin);
        textViewUsd = (TextView) view.findViewById(R.id.textViewUsd);

        String cashNum = getArguments().getString("cashNum");
        String coinNum = getArguments().getString("coinNum").substring(0, 6);
        textViewCoin.setText(coinNum);
        textViewUsd.setText(cashNum);

        imageViewQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).initiateScan();
            }
        });

        buttonSenMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextCoin.getText())){
                    editTextCoin.setError("Please enter your coin number");
                } else {
                    coin = Float.parseFloat(editTextCoin.getText().toString());
                    try {
                        final JSONObject obj = new JSONObject("{type: wallet}");
                        obj.put("txto", editTextWallet.getText());
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

        return view;
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

//                            float newcash = Math.round(Float.parseFloat(Common.cash) - 69.96f* coin);
//
//                            Common.cash =  Float.toString(newcash);
//                            textViewUsd.setText(Common.cash);

                            float newcoin = Float.parseFloat(textViewCoin.getText().toString()) - coin;
                            textViewCoin.setText(Float.toString(newcoin).substring(0, 6));

                            Common.arrayNotif.add("Successful transfer!!");
                        } else {
                            Toast.makeText(getActivity(), "Get Error! Please check your network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


//                    float newcash = Float.parseFloat(Common.cash) - 69.96f* coin;
//                    Common.cash = Float.toString(newcash);
                    Toast.makeText(getActivity(),"OK", Toast.LENGTH_SHORT).show();


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
