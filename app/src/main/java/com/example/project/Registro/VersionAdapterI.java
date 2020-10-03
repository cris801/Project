package com.example.project.Registro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.R;
import com.example.project.RegistroIIActivity;
import com.example.project.RegistroIIIActivity;
import com.example.project.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class VersionAdapterI extends RecyclerView.Adapter<VersionAdapterI.VersionVH> {

    List<Version> versionList;

    public VersionAdapterI(List<Version> versionList) {
        this.versionList = versionList;
    }

    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_i, parent,false);
        searchData(view);
        return new VersionAdapterI.VersionVH(view);
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
        String URL="http://192.168.1.80/projectws/actualizarIC.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // oculatamos el progressDialog
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                                //tvNombreC.setText((jsonObject.getString("name"))+" "+jsonObject.getString("lastname"));
                                padecimiento.setText(jsonObject.getString("suffering"));
                                medicamento.setText(jsonObject.getString("medicine"));
                                indicaciones.setText(jsonObject.getString("indications"));
                                clinica.setText(jsonObject.getString("name_clinic"));
                                nota.setText(jsonObject.getString("note"));
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
                //parametros.put("opcion","buscarIC");
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

    TextInputEditText padecimiento,medicamento,indicaciones,clinica,nota;

    public class VersionVH extends RecyclerView.ViewHolder {

        TextView codeNameTxt;

        Button guardar;
        TextInputLayout nombre;

        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        //String URL="http://192.168.1.80/projectws/clinical_information.php";
        String HttpURI = "http://192.168.1.80/projectws/actualizar.php";

        ProgressDialog progressDialog;
        //RequestQueue requestQueue;

        private final Pattern TEXT_PATTERN =
                Pattern.compile("^[a-zA-Z ]+$");

        private TextInputLayout textInputPadecimiento;
        private TextInputLayout textInputMedicamento;
        private TextInputLayout textInputIndicaciones;
        private TextInputLayout textInputClinica;
        private TextInputLayout textInputNota;

        //TextInputEditText padecimiento;


        @SuppressLint({"WrongViewCast", "WrongConstant"})
        public VersionVH(@NonNull View itemView) {
            super(itemView);

            codeNameTxt = itemView.findViewById(R.id.code_name_i);

            linearLayout = itemView.findViewById(R.id.linear_layout_i);
            expandableLayout = itemView.findViewById(R.id.expendable_layout_i);

            nombre = (TextInputLayout) itemView.findViewById(R.id.text_input_padecimiento);

            guardar = (Button) itemView.findViewById(R.id.buttonRegistrar);
            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmInput(view);
                }
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Version version = versionList.get(getAdapterPosition());
                    version.setExpandable(!version.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            textInputPadecimiento = itemView.findViewById(R.id.text_input_padecimiento);
            //padecimiento.setText("aqui mero va el nombre- viernes");
            textInputMedicamento = itemView.findViewById(R.id.text_input_medicamento);
            textInputIndicaciones = itemView.findViewById(R.id.text_input_indicaciones);
            textInputClinica = itemView.findViewById(R.id.text_input_clinica);
            textInputNota = itemView.findViewById(R.id.text_input_nota);

            padecimiento = itemView.findViewById(R.id.editTextPadecimiento_i);
            medicamento = itemView.findViewById(R.id.editTextMedicamentos_i);
            indicaciones = itemView.findViewById(R.id.editTextIndicaciones_i);
            clinica = itemView.findViewById(R.id.editTextNomClinica_i);
            nota = itemView.findViewById(R.id.editTextNota_i);

            // inicializamos a requestQueue
            //requestQueue = Volley.newRequestQueue(RegistroIIActivity.this);
            progressDialog = new ProgressDialog(itemView.getContext());


        }

        //########################################################################### apartir de aqui se maneja la actualizacion

        private void actualizarDC(final View view) {
            SharedPreferences prefer1 = view.getContext().getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
            final int id = prefer1.getInt("id",0);
            final String miId = Integer.toString(id);

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
                                    mostrarAlertDialog(mensaje,view);
                                else{
                                    //Toast.makeText(view.getContext(),"REGISTRO CORRECTO", Toast.LENGTH_SHORT).show();
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
                    parametros.put("suffering",textInputPadecimiento.getEditText().getText().toString());
                    parametros.put("medicine",textInputMedicamento.getEditText().getText().toString());
                    parametros.put("indications",textInputIndicaciones.getEditText().getText().toString());
                    parametros.put("name_clinic",textInputClinica.getEditText().getText().toString());
                    parametros.put("note",textInputNota.getEditText().getText().toString());
                    parametros.put("opcion","actualizarIC");
                    return parametros;
                }
            };
            //requestQueue.add(stringRequest);
            // AQUI HACEMOS EL LLAMADO A NUESTRO VOLLEY
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

            Toast.makeText(view.getContext(),"ACTUALIZACIÓN CORRECTA", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(this, RegistroIIActivity.class);
            //startActivity(intent);
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

            actualizarDC(v);

        }
    }
}
