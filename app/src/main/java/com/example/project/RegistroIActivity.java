package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroIActivity extends AppCompatActivity {

    //String URL="http://192.168.1.80/projectws/new_user.php";
    String HttpURI = "http://192.168.1.80/projectws/login.php";
    String respuestaRB;
    RadioButton rbU, rbA;
    boolean rbUsuario,rbAuxiliar;
    Button btnSiguiente;

    ProgressDialog progressDialog;
    //RequestQueue requestQueue;

    ImageView imageView;

    // validaciones ---------------------------------------------------------------------------------------------------------------------------------
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 4 characters
                    "$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z ]+$");
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputNameI;
    private TextInputLayout textInputNameII;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_i);

        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputNameI = findViewById(R.id.text_input_namei);
        textInputNameII = findViewById(R.id.text_input_nameii);

        // inicializamos a requestQueue
        // requestQueue = Volley.newRequestQueue(RegistroIActivity.this);
        progressDialog = new ProgressDialog(RegistroIActivity.this);

        rbU = (RadioButton) findViewById(R.id.rbU);
        rbA = (RadioButton) findViewById(R.id.rbA);



        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmInput(view);
            }
        });


        // para el boton de back
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.ivBack_i);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void revisarRadioB() {

        rbUsuario = rbU.isChecked();
        rbAuxiliar = rbA.isChecked();

        if(rbUsuario==true){
            respuestaRB = "Usuario";
        }else if(rbAuxiliar == true){
            respuestaRB = "Auxiliar";
        }

        //Toast.makeText(getApplicationContext(),"respuesta:      "+respuestaRB+"usuario:    "+rbUsuario+"\nauxilia:    "+rbAuxiliar, Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
                            Boolean error = (Boolean) obj.getBoolean("error");
                            String mensaje = obj.getString("mensaje");
                            /*int id = obj.getInt("id");
                            String nombres = obj.getString("nombre");
                            String apellidos = obj.getString("apellidos"); */

                            if(error == true)
                                //Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();
                                mostrarAlertDialog(mensaje);
                            else{

                                /*SharedPreferences prefer1 = getSharedPreferences("MisPreferencias",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefer1.edit();
                                editor.putInt("id",id);
                                editor.putString("nombre",nombres);
                                editor.putBoolean("ingreso",true);
                                editor.commit();*/


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
                parametros.put("name",textInputNameI.getEditText().getText().toString().trim());
                parametros.put("last_name",textInputNameII.getEditText().getText().toString().trim());
                parametros.put("email",textInputEmail.getEditText().getText().toString().trim());
                parametros.put("type_user",respuestaRB);
                parametros.put("password",textInputPassword.getEditText().getText().toString().trim());
                parametros.put("opcion","registroI");
                return parametros;
            }
        };
        //requestQueue.add(stringRequest);
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

        if (respuestaRB.equalsIgnoreCase("usuario")){
            Intent intent = new Intent(this,RegistroIIActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this,RegistroIIIActivity.class);
            startActivity(intent);
        }

    }


    // ------------- VALIDACIONES -----------------------------------------------------------------------------

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            textInputEmail.setError("El campo no puede estar vacío.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Por favor, introduce una dirección de correo electrónico válida");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }
    private boolean validateNameI() {
        String usernameInput = textInputNameI.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNameI.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 40) {
            textInputNameI.setError("Nombre de usuario demasiado largo");
            return false;
        } else if (!NAME_PATTERN.matcher(usernameInput).matches()) {
            textInputNameI.setError("Por favor, introduce un Nombre valido");
            return false;
        }else {
            textInputNameI.setError(null);
            return true;
        }
    }
    private boolean validateNameII() {
        String usernameInput = textInputNameII.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputNameII.setError("El campo no puede estar vacío.");
            return false;
        } else if (usernameInput.length() > 40) {
            textInputNameII.setError("Nombre de usuario demasiado largo");
            return false;
        } else if (!NAME_PATTERN.matcher(usernameInput).matches()) {
            textInputNameII.setError("Por favor, introduce un Nombre valido");
            return false;
        }else {
            textInputNameII.setError(null);
            return true;
        }
    }
    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            textInputPassword.setError("El campo no puede estar vacío.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Contraseña demasiado debil");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
    private boolean validaRadioButton() {
        revisarRadioB();
        if(rbAuxiliar==false && rbUsuario==false ){
            Toast.makeText(getApplicationContext(),"Indica que tipo de usuario eres", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }
    public void confirmInput(View v) {
        if (!validateEmail() | !validateNameI() | !validateNameII() |  !validatePassword() | !validaRadioButton()) {
            return;
        }
        registrarDatos();
    }


}