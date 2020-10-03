package com.example.project.ui.help;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.project.R;
import com.example.project.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static androidx.navigation.Navigation.findNavController;

public class HelpFragment extends Fragment {

    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen

    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    Button botonCargar,btnRegistro;

    ImageView imgFoto;

    String url="http://192.168.1.80/projectws/wsHelp.php?";


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    File fileImagen;
    Bitmap bitmap;

    ProgressDialog progressDialog;
    //JsonObjectRequest jsonObjectRequest;
    //StringRequest stringRequest;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_help, container, false);

        imgFoto= (ImageView) root.findViewById(R.id.ivImagen2);

        botonCargar= (Button) root.findViewById(R.id.btnCaptura);
        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick(view);
            }
        });

        btnRegistro = (Button) root.findViewById(R.id.btnBuscar);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarImagen(view);
            }
        });

        fotoCargada(false);

        return root;
    }


    // metodo para bloquear el boton de registro
    public void fotoCargada(Boolean valor) {
        if(valor){
            btnRegistro.setEnabled(true);

        }else{
            btnRegistro.setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void guardarImagen(final View view) {

        final SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Buscando...");
        progressDialog.show();


        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    //========================= BIEN ===============================================================================================================================================================================================================================
                    @Override
                    public void onResponse(String response) {
                        // oculatamos el progressDialog
                        progressDialog.dismiss();
                        //========================= BIEN ===============================================================================================================================================================================================================================
                        // recuperamos el id_help

                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error_foto");

                            //int id_help = obj.getInt("id_help");

                            if (error == false){
                                //Toast.makeText(getContext(),"FOTO GUARDADA",Toast.LENGTH_SHORT).show();
                                reconocimiento(view);
                            }else{
                                Toast.makeText(getContext(),"FOTO -NO- GUARDADA",Toast.LENGTH_SHORT).show();
                                Log.i("RESPUESTA: ",""+response);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(),"No se ha podido conectar"+error,Toast.LENGTH_SHORT).show();
                mostrarAlertDialog(error.toString());

                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                int miId = sp1.getInt("id",0);
                String id=""+miId;

                String imagen=convertirImgString(bitmap);

                Map<String,String> parametros=new HashMap<>();
                parametros.put("id",id);
                parametros.put("imagen",imagen);
                return parametros;
            }
        };
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }





    private void reconocimiento(final View view) {
        final SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        progressDialog.setMessage("Buscando...");
        progressDialog.show();
        String httpReco="http://192.168.1.80/projectws/compare_face.php";
        //la caja del webService
        StringRequest stringRequest= new StringRequest(Request.Method.POST, httpReco,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Se oculta el progessDialog
                        progressDialog.dismiss();
                        try {
                            JSONObject reco= new JSONObject(response);
                            Boolean  error= reco.getBoolean("error");
                            String mensaje= reco.getString("mensaje");
                            if(error==true) {
                                int id_help = reco.getInt("id_help");
                                // guardamos id del afectado
                                SharedPreferences.Editor editor = sp1.edit();
                                editor.putInt("id_help",id_help);
                                editor.commit();
                                //Toast.makeText(getContext(), mensaje + " " + id_help, Toast.LENGTH_LONG).show();
                                irPrincipal(view);
                            }else if(error==false){
                                Toast.makeText(getContext(), mensaje + " ", Toast.LENGTH_SHORT).show();
                                limpiarFoto(view);
                            }


                        }catch ( JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map <String,String> parametros = new HashMap<>();
                parametros.put("opcion","reconocimiento");
                return parametros;
            }
        };
        //Principalmente este se encarga de que nos de mas tiempo y no arroje error de tiempo con volley
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);

    }

    private void limpiarFoto(View view) {
        findNavController(view).navigate(R.id.navigation_dashboard);
    }


    private void mostrarAlertDialog(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("FALLO");
        builder.setMessage(string);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void irPrincipal(View view) {
        findNavController(view).navigate(R.id.pantallaMostrarDatosAuxilio);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void onclick(View view) {
        cargarImagen();
    }

    private void cargarImagen() {

        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getActivity());
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();

    }

    private void tomarFotografia() {

        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if (isCreada == false) {
            isCreada = miFile.mkdirs();
        }

        if (isCreada == true) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String id = consecutivo.toString() + ".jpg";

            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                    + File.separator + id;//indicamos la ruta de almacenamiento

            fileImagen = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            ////
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authorities = getContext().getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent, COD_FOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                Uri miPath=data.getData();
                imgFoto.setImageURI(miPath);

                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),miPath);
                    imgFoto.setImageBitmap(bitmap);
                    fotoCargada(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path",""+path);
                            }
                        });
                fotoCargada(true);
                bitmap= BitmapFactory.decodeFile(path);
                imgFoto.setImageBitmap(bitmap);

                break;
        }
        bitmap=redimensionarImagen(bitmap,620,800);
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho=bitmap.getWidth();
        int alto=bitmap.getHeight();

        if(ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho=anchoNuevo/ancho;
            float escalaAlto= altoNuevo/alto;

            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);

        }else{
            return bitmap;
        }
    }
}