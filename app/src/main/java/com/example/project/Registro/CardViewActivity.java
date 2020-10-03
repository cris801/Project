package com.example.project.Registro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import com.example.project.MainActivity;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;

public class CardViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Version> versionList;

    RecyclerView recyclerView_i;
    List<Version> versionList_i;

    RecyclerView recyclerView_ii;
    List<Version> versionList_ii;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView_i = (RecyclerView) findViewById(R.id.recyclerViewI);
        recyclerView_ii = (RecyclerView) findViewById(R.id.recyclerViewII);

        imageView = (ImageView) findViewById(R.id.imageView6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent2);
            }
        });

        initData();
        setRecyclerView();

        initData_i();
        setRecyclerView_i();

        initData_ii();
        setRecyclerView_ii();
    }


    private void setRecyclerView() {
        VersionAdapter versionAdapter = new VersionAdapter(versionList);
        recyclerView.setAdapter(versionAdapter);
        // aqui mero se le menea para que se haga adaptable
        recyclerView.setHasFixedSize(false);
    }

    private void initData() {
        versionList = new ArrayList<>();
        versionList.add(new Version("DATOS PERSONALES"));
    }


    private void setRecyclerView_i() {
        VersionAdapterI versionAdapterI = new VersionAdapterI(versionList_i);
        recyclerView_i.setAdapter(versionAdapterI);
        // aqui mero se le menea para que se haga adaptable
        recyclerView_i.setHasFixedSize(false);
    }

    private void initData_i() {
        versionList_i = new ArrayList<>();
        versionList_i.add(new Version("DATOS CLINICOS"));
    }


    private void setRecyclerView_ii() {
        VersionAdapterII versionAdapterII = new VersionAdapterII(versionList_ii);
        recyclerView_ii.setAdapter(versionAdapterII);
        // aqui mero se le menea para que se haga adaptable
        recyclerView_ii.setHasFixedSize(false);
    }

    private void initData_ii() {
        versionList_ii = new ArrayList<>();
        versionList_ii.add(new Version("FOTO"));
    }
}