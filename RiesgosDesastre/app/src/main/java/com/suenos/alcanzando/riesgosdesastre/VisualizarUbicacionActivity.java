package com.suenos.alcanzando.riesgosdesastre;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Entities.ConexionBD;
import Entities.Ubicacion;
import Utiles.Comunes;
import data.AccesoBaseDatos;

public class VisualizarUbicacionActivity extends AppCompatActivity {

    EditText txtID_View;
    EditText txtFecha_View;
    EditText txtDescripcion_View;
    EditText txtNivel_View;
    EditText txtDireccion_View;
    Button btnEliminar_View;

    Ubicacion ubicacionCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_visualizar_ubicacion);

            AsignarControles();

            //Si está visualizando
            Intent intent = getIntent();
            ubicacionCurrent = (Ubicacion) intent.getSerializableExtra("ubicacion");
            if (ubicacionCurrent != null) {
                CargarDatosEntidad(ubicacionCurrent);
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void CargarDatosEntidad(Ubicacion ubicacion) {

        txtID_View.setText(String.valueOf(ubicacion.getID()));
        txtFecha_View.setText(Comunes.FechaFormato(ubicacion.getFechaHora(), "ddMMyyyy hh:ss"));
        txtDescripcion_View.setText(ubicacion.getDescripcion());
        txtNivel_View.setText(String.valueOf(ubicacion.getNivelRiesgo()));
        txtDireccion_View.setText(ubicacion.getDireccion());
    }

    private void AsignarControles() {
        txtID_View = (EditText) findViewById(R.id.txtID_View);
        txtFecha_View = (EditText) findViewById(R.id.txtFecha_View);
        txtDescripcion_View = (EditText) findViewById(R.id.txtDescripcion_View);
        txtNivel_View = (EditText) findViewById(R.id.txtNivel_View);
        txtDireccion_View = (EditText) findViewById(R.id.txtDireccion_View);
        btnEliminar_View = (Button) findViewById(R.id.btnEliminar_View);

        EventoClickBtnEliminar();
    }

    private void EventoClickBtnEliminar() {

        btnEliminar_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AccesoBaseDatos accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());
                    if (accesoDatos.EliminarRegistroUbicacion(ubicacionCurrent.getID())) {
                        Toast.makeText(getApplicationContext(), "Registro eliminado con éxito", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
