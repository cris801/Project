package com.example.project.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.R;
import com.example.project.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;
import static androidx.navigation.Navigation.findNavController;

public class HomeFragment extends Fragment {

    ImageView ivFoto;
    RequestQueue request;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisPreferencias",
                Context.MODE_PRIVATE);
        String nombre = sp1.getString("nombre","NO ENCONTRADO");
        int id = sp1.getInt("id",0);

        ivFoto = (ImageView)  root.findViewById(R.id.ivFoto);
        request = Volley.newRequestQueue(this.getContext());

        cargarFoto(root,id);

        TextView tvNameUser = (TextView) root.findViewById(R.id.textViewUserName);
        tvNameUser.setText("Nombre del usuario:  \n"+nombre);


        final Button btnDatos = (Button) root.findViewById(R.id.btnDatos);
        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.pantallaMostrarDatos);
            }
        });

        return root;
    }

    private void cargarFoto(final View root, int id) {
        String miId = Integer.toString(id);
        String url = "http://192.168.1.80/projectws/imagenes/"+miId+".jpg";
        //String url = "http://192.168.1.80/projectws/imagenes/1.jpg";


        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ivFoto.setImageBitmap(response);
                    }
                },0,0, ImageView.ScaleType.CENTER,null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"error al cargar la imagen",Toast.LENGTH_SHORT);
            }
        });
        request.add(imageRequest);
    }
}