package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroIIActivity extends AppCompatActivity {

    //String URL="http://192.168.1.80/projectws/clinical_information.php";
    String HttpURI = "http://192.168.1.80/projectws/login.php";

    ProgressDialog progressDialog;
    //RequestQueue requestQueue;

    private static final Pattern TEXT_PATTERN =
            Pattern.compile("^[a-zA-Z ]+$");

    private TextInputLayout textInputPadecimiento;
    private TextInputLayout textInputMedicamento;
    private TextInputLayout textInputIndicaciones;
    private TextInputLayout textInputClinica;
    private TextInputLayout textInputNota;

    RequestQueue requestQueuee;
    JsonArrayRequest jsonArrayRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_i_i);

        textInputPadecimiento = findViewById(R.id.text_input_padecimiento);
        textInputMedicamento = findViewById(R.id.text_input_medicamento);
        textInputIndicaciones = findViewById(R.id.text_input_indicaciones);
        textInputClinica = findViewById(R.id.text_input_clinica);
        textInputNota = findViewById(R.id.text_input_nota);


        // inicializamos a requestQueue
        //requestQueue = Volley.newRequestQueue(RegistroIIActivity.this);
        progressDialog = new ProgressDialog(RegistroIIActivity.this);


        // para el boton de back
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void registrarDatos() {

        // mostramos el progresDialog
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        // creacion de la cadea a ejecutar en el web service mediante Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverResponse) {
                        // oculatamos el progressDialog
                        progressDialog.dismiss();

                        try{
                            JSONObject obj = new JSONObject(serverResponse);
                            Boolean error = obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");

                            if(error == true)
                                //Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();
                                mostrarAlertDialog(mensaje);
                            else{
                                Toast.makeText(getApplicationContext(),"REGISTRO CORRECTO", Toast.LENGTH_SHORT).show();
                                // si concede el permiso
                                siguiente();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // oculatamos el progressDialog
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                        mostrarAlertDialog(error.toString());
                    }
                }){
            protected Map<String, String> getParams(){
                Map<String,String> parametros = new HashMap<>();
                parametros.put("suffering",textInputPadecimiento.getEditText().getText().toString());
                parametros.put("medicine",textInputMedicamento.getEditText().getText().toString());
                parametros.put("indications",textInputIndicaciones.getEditText().getText().toString());
                parametros.put("name_clinic",textInputClinica.getEditText().getText().toString());
                parametros.put("notes",textInputNota.getEditText().getText().toString());
                parametros.put("opcion","registroII");
                return parametros;
            }
        };
        //requestQueue.add(stringRequest);
        // AQUI HACEMOS EL LLAMADO A NUESTRO VOLLEY
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void mostrarAlertDialog(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FALLO");
        builder.setMessage(string);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void siguiente() {
        Intent intent = new Intent(this,RegistroIIIActivity.class);
        startActivity(intent);
    }


    // ------------- VALIDACIONES ----------------------------------------------------------------------------------------------------------------------------------

    private boolean validatePadecimiento() {
        String usernameInput = textInputPadecimiento.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNota.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputNota.setError("Nombre de usuario demasiado largo");
            return false;
        }else {
            textInputNota.setError(null);
            return true;
        }
    }
    private boolean validateMedicamento() {
        String usernameInput = textInputMedicamento.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNota.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputNota.setError("Nombre de usuario demasiado largo");
            return false;
        }else {
            textInputNota.setError(null);
            return true;
        }
    }
    private boolean validateIndicaciones() {
        String usernameInput = textInputIndicaciones.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNota.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputNota.setError("Nombre de usuario demasiado largo");
            return false;
        }else {
            textInputNota.setError(null);
            return true;
        }
    }
    private boolean validateClinica() {
        String usernameInput = textInputClinica.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNota.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputNota.setError("Nombre de usuario demasiado largo");
            return false;
        }else {
            textInputNota.setError(null);
            return true;
        }
    }
    private boolean validateNotas() {
        String usernameInput = textInputNota.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNota.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 30) {
            textInputNota.setError("Nombre de usuario demasiado largo");
            return false;
        }else {
            textInputNota.setError(null);
            return true;
        }
    }
    public void confirmInput(View v) {
        if (!validatePadecimiento() | !validateMedicamento() | !validateIndicaciones() | !validateClinica() | !validateNotas()) {
            return;
        }

        registrarDatos();

    }
}