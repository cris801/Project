package com.example.project.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Registro.CardViewActivity;
import com.example.project.RegistroIActivity;
import com.google.android.material.snackbar.Snackbar;

import static androidx.navigation.Navigation.findNavController;

public class SettingFragment extends Fragment {

    TextView tvContacto,tvCerrarSesion,tvSalir,tvFoto,tvDatos;
    Snackbar snackbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_setting, container, false);

        tvDatos = (TextView) root.findViewById(R.id.textView7);
        tvDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarI();
            }
        });

        /*tvFoto = (TextView) root.findViewById(R.id.textView9);
        tvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.actualizar_foto);
            }
        });*/

        tvContacto = (TextView) root.findViewById(R.id.tvContactanos);
        tvContacto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "ResourceType"})
            @Override
            public void onClick(View view) {
                //snackbarMet("Contacto",root);
                enviarCorreo();
            }
        });

        tvCerrarSesion = (TextView) root.findViewById(R.id.tvCerrarSesion);
        tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        tvSalir = (TextView) root.findViewById(R.id.tvSalir);
        tvSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"ADIOS", Toast.LENGTH_SHORT).show();
                getActivity().finishAffinity();
            }
        });

        return root;
    }

    private void actualizarI() {
            Intent intent = new Intent(getContext(), CardViewActivity.class);
            startActivity(intent);
    }

    private void enviarCorreo() {
        SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisPreferencias",
                Context.MODE_PRIVATE);
        String nombre = sp1.getString("nombre","NO ENCONTRADO");

        Intent emailIntent =  new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"reconocimientofacial.p.e@gmail.com"}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hola, soy  "+nombre+" usuario de RFPE");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "El motivo de mi correo es ");

        startActivity(emailIntent);
    }


    private void cerrarSesion() {
        SharedPreferences sp2 = this.getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp2.edit();
        editor.putBoolean("ingreso",false);
        editor.putInt("id",00);
        editor.putString("nombre","");
        editor.commit();

        Intent intent2 = new Intent(getContext(), MainActivity.class);
        startActivity(intent2);
    }


    @SuppressLint("ResourceType")
    private void snackbarMet(String string, View root) {
        snackbar = Snackbar.make(root, string, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(2000);
        snackbar.setText(R.color.colorLetter);
        //snackbar.setBackgroundTint(R.color.colorDark);
        View snackBarView = snackbar.getView();
        //BACKGROUND
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorDark));
        // colocar arriba
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackBarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackBarView.setLayoutParams(params);
        // mostrar
        snackbar.show();
    }
}