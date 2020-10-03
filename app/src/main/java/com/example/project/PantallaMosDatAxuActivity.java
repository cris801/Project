package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PantallaMosDatAxuActivity extends AppCompatActivity {

    TextView tvPadecimiento, tvMedicamento, tvIndicaciones, tvNombreClinica, tvNotas,tvNombreC;
    ProgressDialog progressDialog;
    RequestQueue request;
    ImageView ivFoto,imageView;

    int id;
    String miId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mos_dat_axu);

        SharedPreferences prefer1 = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        id = prefer1.getInt("id_help",0);
        miId = Integer.toString(id);

        tvNombreC = (TextView) findViewById(R.id.tvNombreC);
        tvPadecimiento = (TextView) findViewById(R.id.tvPadecimiento);
        tvMedicamento = (TextView) findViewById(R.id.tvMedicamento);
        tvIndicaciones = (TextView) findViewById(R.id.tvIndicaciones);
        tvNombreClinica = (TextView) findViewById(R.id.tvClinica);
        tvNotas = (TextView) findViewById(R.id.tvNotas);

        request = Volley.newRequestQueue(this);

        ivFoto = (ImageView) findViewById(R.id.ivPhotoHelp);

        searchData();
        cargarFoto(miId);

        imageView = (ImageView) findViewById(R.id.ivBack_a);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), HomeAuxiliarActivity.class);
                startActivity(intent2);
            }
        });

        // para el boton de back
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /*
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    private void searchData(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Procesando...");
        progressDialog.show();

        String URL="http://192.168.1.80/projectws/search_data.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // oculatamos el progressDialog
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvNombreC.setText((jsonObject.getString("name"))+" "+jsonObject.getString("lastname"));
                            tvPadecimiento.setText(jsonObject.getString("suffering"));
                            tvMedicamento.setText(jsonObject.getString("medicine"));
                            tvIndicaciones.setText(jsonObject.getString("indications"));
                            tvNombreClinica.setText(jsonObject.getString("name_clinic"));
                            tvNotas.setText(jsonObject.getString("note"));
                        } catch (JSONException e) {
                            //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            mostrarAlertDialog(e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "ERROR EN LA CONEXION "+error, Toast.LENGTH_LONG).show();

                // oculatamos el progressDialog
                progressDialog.dismiss();

                mostrarAlertDialog("ERROR EN LA CONEXION "+error.toString());
            }
        }){
            protected Map<String, String> getParams(){
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id",miId);
                return parametros;
            }
        };
        //requestQueue = Volley.newRequestQueue(getContext());
        //requestQueue.add(jsonArrayRequest);
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void mostrarAlertDialog(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FALLO - mostrar ayuda");
        builder.setMessage(string);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarFoto(String id_help) {

        String url = "http://192.168.1.80/projectws/imagenes/"+id_help+".jpg";

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ivFoto.setImageBitmap(response);
                    }
                },0,0, ImageView.ScaleType.CENTER,null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error al cargar la imagen",Toast.LENGTH_SHORT);
            }
        });

        request.add(imageRequest);
    }
}