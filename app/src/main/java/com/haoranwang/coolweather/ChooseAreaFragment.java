package com.haoranwang.coolweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoranwang.coolweather.db.Province;
import com.haoranwang.coolweather.utl.HttpUtil;
import com.haoranwang.coolweather.utl.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    private static final String tag = "chooseAreaFragment";

    private static final String commonUrl = "http://guolin.tech/api";

    private TextView textView;

    private Button backButton;

    private Button deleteButton;

    private Button queryButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        textView = view.findViewById(R.id.title_text);

        backButton = view.findViewById(R.id.back_button);
        deleteButton = view.findViewById(R.id.delete_button);
        queryButton = view.findViewById(R.id.query_button);

        listView = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout
                .simple_expandable_list_item_1, dataList);
        listView.setAdapter(adapter);

        //queryProvince();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll(Province.class);
                dataList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryProvince();
            }
        });

        return view;
    }

/*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll(Province.class);
                dataList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryProvince();
            }
        });
    }
*/

    private void queryProvince(){
        //provinceList.clear();
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            queryProvinceFromDB();
        }
        else {
            queryProvinceFromServer(commonUrl + "/china");
        }
    }

    private void queryProvinceFromDB(){
        Log.d(tag, "queryFromDB Start");
        dataList.clear();
        for (Province province: provinceList){
            dataList.add(province.getProvinceName());
        }
        adapter.notifyDataSetChanged();
    }

    private void queryProvinceFromServer(String address){

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(getContext(), "Loaded Failure", Toast.LENGTH_SHORT).show();
                Log.d(tag,"Loaded Failure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(tag, "queryFromServer Start");
                //Log.d(tag,"Response: " + response.toString());
                //Log.d(tag,"Response: " + response.body().string());
                //could only use .string() once
                Utility.handleProvinceResponse(response.body().string());
            }
        });

    }
}

