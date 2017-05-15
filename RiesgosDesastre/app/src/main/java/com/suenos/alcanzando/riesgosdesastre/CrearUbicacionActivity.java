package com.suenos.alcanzando.riesgosdesastre;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import Entities.ConexionBD;
import Entities.Ubicacion;
import data.AccesoBaseDatos;

public class CrearUbicacionActivity extends AppCompatActivity {

    EditText txtDescripcion;
    EditText txtDireccion;
    EditText txtLongitud;
    EditText txtLatitud;
    EditText txtNivel;


    Button btnCrear;
    AccesoBaseDatos accesoDatos ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_crear_ubicacion);

            AsignarControles();


            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void AsignarControles() {
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtDireccion = (EditText) findViewById(R.id.txtDireccion);
        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        txtNivel = (EditText) findViewById(R.id.txtNivel);
        txtDireccion = (EditText) findViewById(R.id.txtDireccion);

        btnCrear = (Button) findViewById(R.id.btnCrear);

        CrearEventoClicBtnCrear();
    }

    private void CrearEventoClicBtnCrear() {
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Ubicacion ubicacion = new Ubicacion();
                    ubicacion.setDescripcion(txtDescripcion.getText().toString());
                    ubicacion.setDireccion(txtDireccion.getText().toString());
                    ubicacion.setLongitud(Float.valueOf( txtLongitud.getText().toString()));
                    ubicacion.setLatitud(Float.valueOf( txtLatitud.getText().toString()));
                    ubicacion.setNivelRiesgo(Integer.valueOf( txtNivel.getText().toString()));
                    //
                    accesoDatos.CrearUbicacion(ubicacion);
                    Toast.makeText(getApplicationContext(), "Registro Almacenado con Exito", Toast.LENGTH_LONG).show();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
