package com.example.project.Registro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.Transliterator;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.R;
import com.example.project.RegistroIActivity;
import com.example.project.RegistroIIActivity;
import com.example.project.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.VersionVH> {

    List<Version> versionList;

    public VersionAdapter(List<Version> versionList) {
        this.versionList = versionList;
    }

    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,false);
        searchData(view);
        return new VersionAdapter.VersionVH(view);
    }

    //########################################################################### aqui se hace la carga de la informacion

    private void searchData(final View view){
        SharedPreferences prefer1 = view.getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Procesando...");
        progressDialog.show();

        final int id = prefer1.getInt("id",0);
        final String miId = Integer.toString(id);
        String URL="http://192.168.1.80/projectws/actualizarDP.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // oculatamos el progressDialog
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //tvNombreC.setText((jsonObject.getString("name"))+" "+jsonObject.getString("lastname"));
                            n.setText(jsonObject.getString("nombre"));
                            n.setFocusable(false); //true lo enfocas.
                            n.setEnabled(false);    //false lo deshabilitas.
                            apellido.setText(jsonObject.getString("apellidos"));
                            apellido.setEnabled(false);    //false lo deshabilitas.
                            email.setText(jsonObject.getString("correo"));
                            password.setText(jsonObject.getString("contrasena"));
                        } catch (JSONException e) {
                            //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            mostrarAlertDialog(e.toString(),view);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "ERROR EN LA CONEXION "+error, Toast.LENGTH_LONG).show();
                // oculatamos el progressDialog
                progressDialog.dismiss();
                mostrarAlertDialog("ERROR EN LA CONEXION "+error.toString(),view);
            }
        }){
            protected Map<String, String> getParams(){
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id",miId);
                //parametros.put("opcion","buscarDP");
                return parametros;
            }
        };
        //requestQueue = Volley.newRequestQueue(getContext());
        //requestQueue.add(jsonArrayRequest);
        VolleySingleton.getIntanciaVolley(view.getContext()).addToRequestQueue(stringRequest);
    }
    private void mostrarAlertDialog(String string,View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("FALLO");
        builder.setMessage(string);
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onBindViewHolder(@NonNull VersionVH holder, int position) {
        Version version = versionList.get(position);
        holder.codeNameTxt.setText(version.getCode_name());

        boolean isExpandable = versionList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return versionList.size();
    }

    //TextInputEditText nombre,apellido,email,password;
    TextInputEditText n,apellido,email,password;

    public class VersionVH extends RecyclerView.ViewHolder {

        TextView codeNameTxt;

        Button guardar;
        //TextInputLayout nombre;

        LinearLayout linearLayout;
        RelativeLayout expandableLayout;


        //String URL="http://192.168.1.80/projectws/new_user.php";
        String HttpURI = "http://192.168.1.80/projectws/actualizar.php";
        String respuestaRB;
        RadioButton rbU, rbA;
        boolean rbUsuario, rbAuxiliar;
        Button btnSiguiente;

        ProgressDialog progressDialog;
        //RequestQueue requestQueue;

        // validaciones ---------------------------------------------------------------------------------------------------------------------------------
        private final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        //"(?=.*[0-9])" +         //at least 1 digit
                        //"(?=.*[a-z])" +         //at least 1 lower case letter
                        //"(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{8,}" +               //at least 4 characters
                        "$");

        private final Pattern NAME_PATTERN =
                Pattern.compile("^[a-zA-Z ]+$");
        private TextInputLayout textInputEmail;
        private TextInputLayout textInputNameI;
        private TextInputLayout textInputNameII;
        private TextInputLayout textInputPassword;

        @SuppressLint("WrongViewCast")
        public VersionVH(@NonNull View itemView) {
            super(itemView);

            codeNameTxt = itemView.findViewById(R.id.code_name);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expendable_layout);

            //nombre = (TextInputLayout) itemView.findViewById(R.id.text_input_padecimiento);

            //guardar = (Button) itemView.findViewById(R.id.btnSiguiente);
            //nombre = (TextInputLayout) itemView.findViewById(R.id.text_input_namei);


            /*
            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "guardando "+ nombre.getEditText().getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    registrarDatos(view);
                }
            });

             */

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Version version = versionList.get(getAdapterPosition());
                    version.setExpandable(!version.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            textInputEmail = (TextInputLayout) itemView.findViewById(R.id.text_input_email);
            textInputPassword = (TextInputLayout) itemView.findViewById(R.id.text_input_password);
            textInputNameI = (TextInputLayout) itemView.findViewById(R.id.text_input_namei);
            textInputNameII = (TextInputLayout) itemView.findViewById(R.id.text_input_nameii);

            //nombre = (TextInputEditText) itemView.findViewById(R.id.etNombres_i);
            n = itemView.findViewById(R.id.etNombres_i);
            apellido = itemView.findViewById(R.id.etApellidos_i);
            email = itemView.findViewById(R.id.etCorreo_i);
            password = itemView.findViewById(R.id.etContrasena_i);


            // inicializamos a requestQueue
            // requestQueue = Volley.newRequestQueue(RegistroIActivity.this);


            rbU = (RadioButton) itemView.findViewById(R.id.rbU);
            rbA = (RadioButton) itemView.findViewById(R.id.rbA);

            revisarRadioB();

            btnSiguiente = (Button) itemView.findViewById(R.id.btnSiguiente);

            btnSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmInput(view);
                }
            });
        }
        private void revisarRadioB() {
            rbUsuario = rbU.isChecked();
            rbAuxiliar = rbA.isChecked();

            if(rbUsuario){
                respuestaRB = "Usuario";
            }else{
                respuestaRB = "Auxiliar";
            }
        }
        //########################################################################### aqui se hace la carga de la informacion



        //########################################################################### apartir de aqui se maneja la actualizacion
        private void registrarDatos(final View view) {
            SharedPreferences prefer1 = view.getContext().getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
            final int id = prefer1.getInt("id",0);
            final String miId = Integer.toString(id);

            progressDialog = new ProgressDialog(view.getContext());
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

                                //String apellidos = obj.getString("apellidos");

                                if(error == true)
                                    //Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG).show();
                                    mostrarAlertDialog(mensaje,view);
                                else{

                                    Toast.makeText(view.getContext(),"ACTUALIZACIÓN CORRECTA", Toast.LENGTH_SHORT).show();
                                    // si concede el permiso
                                    contraer(view);
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
                            mostrarAlertDialog(error.toString(),view);
                        }
                    }){
                protected Map<String, String> getParams(){
                    Map<String,String> parametros = new HashMap<>();
                    parametros.put("id",miId);
                    parametros.put("email",textInputEmail.getEditText().getText().toString().trim());
                    //parametros.put("type_user",respuestaRB);
                    parametros.put("password",textInputPassword.getEditText().getText().toString().trim());
                    parametros.put("opcion","actualizarDP");
                    return parametros;
                }
            };
            //requestQueue.add(stringRequest);
            VolleySingleton.getIntanciaVolley(view.getContext()).addToRequestQueue(stringRequest);
        }

        private void mostrarAlertDialog(String string,View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("FALLO");
            builder.setMessage(string);
            builder.setPositiveButton("Aceptar", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void contraer(View view) {
            Version version = versionList.get(getAdapterPosition());
            version.setExpandable(!version.isExpandable());
            notifyItemChanged(getAdapterPosition());
            //Intent intent = new Intent(this, RegistroIIActivity.class);
            //startActivity(intent);
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
        public void confirmInput(View v) {
            if (!validateEmail() | !validateNameI() | !validateNameII() |  !validatePassword()) {
                return;
            }
            registrarDatos(v);
        }
    }
}
