package com.example.lequynam.layout_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import Common.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifFragment extends Fragment {

    ListView lvNotif;
    ArrayList<String> arrayCourse;


    public NotifFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notif, container, false);
        lvNotif = (ListView) view.findViewById(R.id.listViewNotif);
        arrayCourse = new ArrayList<>();
        arrayCourse.add("Android");
        arrayCourse.add("Ios");
        arrayCourse.add("PHP");

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_expandable_list_item_1,Common.arrayNotif);
        lvNotif.setAdapter(adapter);
        return view;
    }

}
