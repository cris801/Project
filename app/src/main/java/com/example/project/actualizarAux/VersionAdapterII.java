package com.example.project.actualizarAux;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionAdapterII extends RecyclerView.Adapter<VersionAdapterII.VersionVH> {

    List<Version> versionList;

    public VersionAdapterII(List<Version> versionList) {
        this.versionList = versionList;
    }

    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ii, parent,false);
        return new VersionVH(view);
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

    public class VersionVH extends RecyclerView.ViewHolder {

        TextView codeNameTxt;


        LinearLayout linearLayout;
        RelativeLayout expandableLayout;


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




        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        File fileImagen;
        Bitmap bitmap;

        ProgressDialog progreso;

        JsonObjectRequest jsonObjectRequest;
        RequestQueue request;

        StringRequest stringRequest;
        @SuppressLint("WrongViewCast")
        public VersionVH(@NonNull View itemView) {
            super(itemView);

            codeNameTxt = itemView.findViewById(R.id.code_name_ii);

            linearLayout = itemView.findViewById(R.id.linear_layout_ii);
            expandableLayout = itemView.findViewById(R.id.expendable_layout_ii);


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Version version = versionList.get(getAdapterPosition());
                    version.setExpandable(!version.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            SharedPreferences sp1 = itemView.getContext().getSharedPreferences("MisPreferencias",
                    Context.MODE_PRIVATE);
            String nombre = sp1.getString("nombre","NO ENCONTRADO");
            int id = sp1.getInt("id",0);

            imgFoto = (ImageView) itemView.findViewById(R.id.ivImagen3);
            request = Volley.newRequestQueue(itemView.getContext());

            cargarFoto(id);


            botonCargar = (Button) itemView.findViewById(R.id.btnCapturar_i);

            botonCargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onclick(view);
                }
            });

            btnRegistro = (Button) itemView.findViewById(R.id.btnBuscar_i);

            btnRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guardarImagen(view);
                }
            });

            fotoCargada(false);

        }


        public void fotoCargada(Boolean valor) {
            if(valor){
                btnRegistro.setEnabled(true);

            }else{
                btnRegistro.setEnabled(false);
            }
        }


        private void cargarFoto(int id) {
            String miId = Integer.toString(id);
            String url = "http://192.168.1.80/projectws/imagenes/"+miId+".jpg";
            //String url = "http://192.168.1.80/projectws/imagenes/1.jpg";


            ImageRequest imageRequest = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            imgFoto.setImageBitmap(response);
                        }
                    },0,0, ImageView.ScaleType.CENTER,null, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(itemView.getContext(),"error al cargar la imagen",Toast.LENGTH_SHORT);
                }
            });
            request.add(imageRequest);
        }


        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        private void guardarImagen(final View view) {

            progreso=new ProgressDialog(view.getContext());
            progreso.setMessage("Cargando...");
            progreso.show();


            String url="http://192.168.1.80/projectws/photo.php?";

            stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    progreso.hide();

                    if (response.trim().equalsIgnoreCase("registra")){

                        Toast.makeText(view.getContext(),"Se ha registrado con exito",Toast.LENGTH_SHORT).show();
                        irPrincipal(view);
                    }else{
                        Toast.makeText(view.getContext(),"No se ha registrado ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getApplicationContext(),"No se ha podido conectar"+error,Toast.LENGTH_SHORT).show();
                    mostrarAlertDialog(error.toString(),view);
                    progreso.hide();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences prefer1 = view.getContext().getSharedPreferences("MisPreferencias",
                            Context.MODE_PRIVATE);

                    int miId = prefer1.getInt("id",0);

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
            VolleySingleton.getIntanciaVolley(view.getContext()).addToRequestQueue(stringRequest);
        }

        private void irPrincipal(View view) {
            Intent intent =new Intent(view.getContext(), MainActivity.class);
            ((Activity)view.getContext()).startActivity(intent);
        }

        private void mostrarAlertDialog(String string, View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("FALLO");
            builder.setMessage(string);
            builder.setPositiveButton("Aceptar", null);

            AlertDialog dialog = builder.create();
            dialog.show();
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

        public void onclick(final View view) {
            cargarImagen(view);
        }

        private void cargarImagen(final View view) {

            final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
            final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(view.getContext());
            alertOpciones.setTitle("Seleccione una Opción");
            alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (opciones[i].equals("Tomar Foto")){
                        tomarFotografia(view);
                    }else{
                        if (opciones[i].equals("Cargar Imagen")){
                            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            // Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/");
                            ((Activity)view.getContext()).startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                        }else{
                            dialogInterface.dismiss();
                        }
                    }
                }
            });
            alertOpciones.show();

        }

        private void tomarFotografia(View view) {

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
                    String authorities = view.getContext().getPackageName() + ".provider";
                    Uri imageUri = FileProvider.getUriForFile(view.getContext(), authorities, fileImagen);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
                }
                ((Activity)view.getContext()).startActivityForResult(intent, COD_FOTO);
            }
        }


        //@Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                    imgFoto.setImageURI(miPath);

                    try {
                        Toast.makeText(itemView.getContext(),"tomar de galeria ",Toast.LENGTH_SHORT).show();
                        fotoCargada(true);
                        bitmap=MediaStore.Images.Media.getBitmap(itemView.getContext().getContentResolver(),miPath);
                        imgFoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_FOTO:
                    MediaScannerConnection.scanFile(itemView.getContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Path",""+path);
                                }
                            });
                    Toast.makeText(itemView.getContext(),"tomar con camara",Toast.LENGTH_SHORT).show();
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
}
