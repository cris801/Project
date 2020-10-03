package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.example.project.Registro.CardViewActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {
    TextView tvRegistrar;
    Button btnAcceder;
    Boolean estado;

    String e,p;

    Snackbar snackbar;

    ProgressDialog progressDialog;
    //RequestQueue requestQueue;

    //String ip = getString(R.string.ip);

    String HttpURI = "http://192.168.1.80/projectws/login.php";

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
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        revisarEstado();


        // sincronizamos componentes visuales
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

        // inicializamos a requestQueue
        //requestQueue = Volley.newRequestQueue(getApplicationContext());
        progressDialog = new ProgressDialog(MainActivity.this);

        btnAcceder = (Button) findViewById(R.id.btnAcceder);

        // enlace para el registro
        tvRegistrar = (TextView) findViewById(R.id.tvRegistry);
        tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registry();
            }
        });

        if(validaPermisos()){
            btnAcceder.setEnabled(true);

        }else{
            btnAcceder.setEnabled(false);
        }


    }

    private void acceder() {
        e = textInputEmail.getEditText().getText().toString().trim();
        p = textInputPassword.getEditText().getText().toString().trim();


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
                            int id = obj.getInt("id");
                            String nombres = obj.getString("nombre");
                            String tipo = obj.getString("tipo");
                            if(error)
                                mostrarAlertDialog(mensaje);
                            else{
                                String correo =textInputEmail.getEditText().getText().toString();
                                String password =textInputPassword.getEditText().getText().toString();

                                SharedPreferences prefer1 = getSharedPreferences("MisPreferencias",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefer1.edit();
                                editor.putInt("id",id);
                                editor.putString("nombre",nombres);
                                // Al iniciar sesión correctamente, mandarás a guardar una variable adicional al Shared Preferences "MisPreferencias" llamada "ingreso" de tipo booleano.
                                // El cual, guardará un valor true si la sesión está iniciada
                                editor.putBoolean("ingreso",true);
                                editor.putString("tipo",tipo);
                                editor.commit();

                                int miId = prefer1.getInt("id",0);

                                //Toast.makeText(getApplicationContext(),nombres.toUpperCase()+" INICIO SESIÓN", Toast.LENGTH_SHORT).show();

                                // si concede el permiso
                                iniciar(tipo);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();

                            Toast.makeText(getApplicationContext(),"USUARIO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
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
                parametros.put("email",e);
                parametros.put("password",p);
                parametros.put("opcion","login");
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



    private void iniciar(String string) {
        if (string.equalsIgnoreCase("auxiliar")){
            Intent intent = new Intent(this,HomeAuxiliarActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
        }
    }

    // metodo que conduce a las pantallas de registro
    private void registry() {
        //Intent intent = new Intent(this, CardViewActivity.class);
        Intent intent = new Intent(this, RegistroIActivity.class);
        startActivity(intent);
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------- VALIDACION DE CAMPOS ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            textInputEmail.setError("El campo no puede estar vacío.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Dirección de correo electrónico válida");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            textInputPassword.setError("El campo no puede estar vacío.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Revisa tu contraseña ");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
    public void confirmInput(View v) {
        if (!validateEmail() | !validatePassword()) {
            return;
        }
        acceder();
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------- VALIDAR PERMISOS ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                btnAcceder.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(MainActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------- CONFIGURACION SHARED PREFERENCES ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void revisarEstado(){
        // revisamos si el usuario esta o no activo
        SharedPreferences sp2 = getSharedPreferences("MisPreferencias",
                Context.MODE_PRIVATE);
        //recuperamos el valor de ingreso y lo asignamos a una variable
        estado = sp2.getBoolean("ingreso",false);
        String tipo = sp2.getString("tipo","no encontrado");
        if(estado) {
            String tipoo = sp2.getString("tipo","no encontrado");
            if(tipoo.equalsIgnoreCase("auxiliar")) {
                Intent intent = new Intent(this,HomeAuxiliarActivity.class);
                startActivity(intent);
            } else if (tipoo.equalsIgnoreCase("usuario")){
                Intent intent = new Intent(this,MenuActivity.class);
                startActivity(intent);
            }
        }
    }


}