package com.example.project.actualizarAux;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.project.HomeAuxiliarActivity;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Registro.Version;
import com.example.project.Registro.VersionAdapter;
import com.example.project.Registro.VersionAdapterI;
import com.example.project.Registro.VersionAdapterII;

import java.util.ArrayList;
import java.util.List;

public class CardViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<com.example.project.Registro.Version> versionList;

    RecyclerView recyclerView_i;
    List<com.example.project.Registro.Version> versionList_i;

    RecyclerView recyclerView_ii;
    List<com.example.project.Registro.Version> versionList_ii;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView_i = (RecyclerView) findViewById(R.id.recyclerViewI);
        recyclerView_ii = (RecyclerView) findViewById(R.id.recyclerViewII);

        imageView = (ImageView) findViewById(R.id.imageView6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), HomeAuxiliarActivity.class);
                startActivity(intent2);
            }
        });

        initData();
        setRecyclerView();



        initData_ii();
        setRecyclerView_ii();
    }


    private void setRecyclerView() {
        com.example.project.Registro.VersionAdapter versionAdapter = new VersionAdapter(versionList);
        recyclerView.setAdapter(versionAdapter);
        // aqui mero se le menea para que se haga adaptable
        recyclerView.setHasFixedSize(false);
    }

    private void initData() {
        versionList = new ArrayList<>();
        versionList.add(new com.example.project.Registro.Version("DATOS PERSONALES"));
    }



    private void setRecyclerView_ii() {
        com.example.project.Registro.VersionAdapterII versionAdapterII = new VersionAdapterII(versionList_ii);
        recyclerView_ii.setAdapter(versionAdapterII);
        // aqui mero se le menea para que se haga adaptable
        recyclerView_ii.setHasFixedSize(false);
    }

    private void initData_ii() {
        versionList_ii = new ArrayList<>();
        versionList_ii.add(new Version("FOTO"));
    }
}