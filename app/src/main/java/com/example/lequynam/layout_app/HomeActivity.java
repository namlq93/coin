package com.example.lequynam.layout_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import Common.Common;

import static com.facebook.AccessTokenManager.SHARED_PREFERENCES_NAME;

public class HomeActivity extends AppCompatActivity {


    AHBottomNavigation bottomNavigation;
    private ConstraintLayout mMainFrame;
    private HomeFragment homeFragment;
    private NotifFragment notifFragment;
    private AccountFragment accountFragment;
    private TransFragment transFragment;
    String cash, wallet;
    int notifNum;


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.222:8000/");
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        anhxa();
        createNavigationItems();
        Intent intent = getIntent();
        String[] arrayDataFb = intent.getStringArrayExtra("dataFb");

        Bundle bundle = new Bundle();
        bundle.putString("fb_id", arrayDataFb[0]);
        homeFragment.setArguments(bundle);


//        addFragment(accountFragment, "account");
//        addFragment(notifFragment, "notif");
        setFragment(homeFragment, "home");


        //homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
        //homeFragment.setText("OK");
        //Toast.makeText(this, cash, Toast.LENGTH_SHORT).show();

    }

    private void anhxa() {

        mMainFrame = (ConstraintLayout) findViewById(R.id.main_frame);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        notifFragment = new NotifFragment();
        accountFragment = new AccountFragment();
    }

    //Set navigation bottom
    private void createNavigationItems() {
        //bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.baseline_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Notification", R.drawable.baseline_notifications_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Account", R.drawable.baseline_person_black_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#2376B3"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        // Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));


        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                switch (position){
                    case 0:
                        setFragment(homeFragment, "home");
                        //homeFragment.textViewCashHome.setText(Common.cash);
                        return true;

                    case 1:
                        setFragment(notifFragment, "notif");
                        Common.notif = "0";
                        bottomNavigation.setNotification("", 1);

                        return true;

                    case 2:
                        //get profile facebook after login
                        getWallet("http://192.168.0.222:8000/getwallet");
                        return true;

                    default:
                        return false;
                }
            }
        });

    }
    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment, tag);
        fragmentTransaction.commit();

    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_frame, fragment, tag);
        fragmentTransaction.commit();

    }


    //Get wallet address
    private void getWallet(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String wallet = object.getString("wallet");
                    Log.d("wallet",wallet);
                    Bundle bundle = new Bundle();
                    bundle.putString("wallet", wallet);
                    accountFragment.setArguments(bundle);
                    setFragment(accountFragment, "account");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Xay ra loi", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //get profile facebook after login
                Intent intent = getIntent();
                String[] arrayDataFb = intent.getStringArrayExtra("dataFb");
                Map<String,String> params = new HashMap<>();
                params.put("id", arrayDataFb[0]);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    //Qr scan:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    if(jsonObject.getString("type").equalsIgnoreCase("card")) {
                        cash = jsonObject.getString("value");
                        float cash_tmp = Float.parseFloat(Common.cash) + Float.parseFloat(cash);
                        Common.cash = String.valueOf(cash_tmp);
                        homeFragment.textViewCashHome.setText(Common.cash);
                        Common.arrayNotif.add("Recharge successful!!");
                        notifNum = Integer.parseInt(Common.notif) + 1;
                        Common.notif = String.valueOf(notifNum);
                        AHBottomNavigation bottomNavigation = (AHBottomNavigation) this.findViewById(R.id.bottom_navigation);
                        bottomNavigation.setNotification(String.valueOf(notifNum), 1);

                    }
                    else if(jsonObject.getString("type").equalsIgnoreCase("wallet")){
                        wallet = jsonObject.getString("address");
                        //Toast.makeText(this, wallet, Toast.LENGTH_SHORT).show();
                        transFragment = (TransFragment) getSupportFragmentManager().findFragmentByTag("trans");
                        transFragment.editTextWallet.setText(wallet);
                    }
                    else if(jsonObject.getString("type").equalsIgnoreCase("product")){
                        String product = jsonObject.getString("name");
                        String price_usd = jsonObject.getString("price");
                        float price = Float.parseFloat(price_usd) / 69.96f;
                        String wallet = jsonObject.getString("txto");
                        try {
                            final JSONObject obj = new JSONObject("{type: wallet}");
                            obj.put("txto", wallet);
                            obj.put("ncoin", price);
                            obj.put("id", Common.fb_id);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                            alertDialog.setTitle("Thong bao!");
                            alertDialog.setMessage("Do you want buy: " + product + " - price: " + price_usd + " USD (" + price + " Coin) ");
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


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    protected void onResume() {


        //get profile facebook after login
        Intent intent = getIntent();
        String[] arrayDataFb = intent.getStringArrayExtra("dataFb");

        homeFragment.textViewUserName.setText(arrayDataFb[1]);
        homeFragment.profilePictureView.setProfileId(arrayDataFb[0]);
        super.onResume();


    }



    //get data from server socket
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String status = data.getString("status");
                        if(status.equalsIgnoreCase("true")){
                            AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
                            bottomNavigation.setNotification(String.valueOf(notifNum), 1);

//                            float newcash = Math.round(Float.parseFloat(Common.cash) - 69.96f* coin);
//
//                            Common.cash =  Float.toString(newcash);
//                            textViewUsd.setText(Common.cash);
//
//                            float newcoin = Float.parseFloat(textViewCoin.getText().toString()) - coin;
//                            textViewCoin.setText(Float.toString(newcoin).substring(0, 6));

                            Common.arrayNotif.add("Shopping Successful!!");
                        } else {
                            Toast.makeText(HomeActivity.this, "Get Error! Please check your network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            });
        }
    };


    //get data from server socket
    private Emitter.Listener onNewMessage2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String status = data.getString("status");
                        if(status.equalsIgnoreCase("true")){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                            alertDialog.setTitle("Thong bao!");
                            alertDialog.setMessage("Transaction is processing. Please wait a moment..");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            alertDialog.show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Get Error! Please check your network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };
}
