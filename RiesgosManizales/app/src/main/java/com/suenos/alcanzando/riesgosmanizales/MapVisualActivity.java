package com.suenos.alcanzando.riesgosmanizales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import Data.AccesoBaseDatos;
import Entities.ConexionBD;
import Entities.Direccion;
import Entities.Ubicacion;
import Utiles.Comunes;

public class MapVisualActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapVisualizacion;

    //region Constantes
    private final static int PERMISSION_FINE_LOCATION = 101;
    protected static final String TAG = "MapsActivity";
    protected static final String TAGEror = "Map Visualizacion Error";
    //endregion Constantes

    //region Controles
    TextView lblFechaVis;
    TextView lblDescripcion;
    TextView lblNivel;
    TextView lblDireccion;
    TextView lblAmpliarDes;
    TextView lblAmpliarDir;
    Button btnEliminar;
    //endregion endControles

    AccesoBaseDatos accesoDatos;

    Ubicacion ubicacionCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map_visual);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    CerrarVentana();
                }
            });

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapVisualizacion);
            mapFragment.getMapAsync(this);


            AsignarControles();
            ObtenerDatosVisualizacion();

            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    private void AsignarControles() {
        lblFechaVis = (TextView) findViewById(R.id.lblFechaVis);
        lblDescripcion = (TextView) findViewById(R.id.lblDescripcion);
        lblAmpliarDes = (TextView) findViewById(R.id.lblAmpliarDes);
        lblNivel = (TextView) findViewById(R.id.lblNivel);
        lblDireccion = (TextView) findViewById(R.id.lblDireccion);
        lblAmpliarDir = (TextView) findViewById(R.id.lblAmpliarDir);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);

        SpannableString contentDes = new SpannableString("Ampliar");
        contentDes.setSpan(new UnderlineSpan(), 0, "Ampliar".length(), 0);//where first 0 shows the starting and udata.length() shows the ending span.if you want to span only part of it than you can change these values like 5,8 then it will underline part of it.
        lblAmpliarDes.setText(contentDes);
        lblAmpliarDir.setText(contentDes);

        EventolblDescripcion();
        EventolblDireccion();
        EventoButtonbtnEliminar();
    }

    private void EventoButtonbtnEliminar() {
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EliminarRegistro();
                }
                catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAGEror, ex.getMessage());
                }
            }
        });
    }

    private void EliminarRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seguro desea eliminar")
                .setTitle("Confirmar");

        // Add the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                boolean respuesta = accesoDatos.EliminarRegistroUbicacion(ubicacionCurrent.getID());

                if (respuesta) {
                    Toast.makeText(getApplicationContext(), "El registro se eliminó de manera satisfactoria", Toast.LENGTH_LONG).show();
                    CerrarVentana();
                } else
                    Toast.makeText(getApplicationContext(), "El registro no pudo ser eliminado", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void EventolblDireccion() {
        lblAmpliarDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarDialogo("Dirección", ubicacionCurrent.getDireccion());
            }
        });
    }

    private void EventolblDescripcion() {

        lblAmpliarDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarDialogo("Descripción", ubicacionCurrent.getDescripcion());
            }
        });
    }

    private void MostrarDialogo(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setTitle(titulo);

        // Add the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void ObtenerDatosVisualizacion() {

        Intent intent = getIntent();
        ubicacionCurrent = (Ubicacion) intent.getSerializableExtra("ubicacion");
        String fecha;

        //Asigna los valores a los controles

        lblDescripcion.setText(ubicacionCurrent.getDescripcion());
        lblNivel.setText(ubicacionCurrent.getNivelRiesgo());
        lblDireccion.setText(ubicacionCurrent.getDireccion());
        fecha = String.valueOf(Comunes.FechaFormato(ubicacionCurrent.getFechaHora(), "yyyy-MM-dd HH:mm:ss"));
        lblFechaVis.setText(fecha);
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mapVisualizacion = googleMap;
            LatLng currentUbication;
            Direccion direccion;
            String nombreLugar = "";

            direccion = Comunes.ObtenerDireccion(this, ubicacionCurrent.getLatitud(), ubicacionCurrent.getLongitud());
            if (direccion != null) {
                nombreLugar = direccion.getCity() + ", " + direccion.getState();
            }
            currentUbication = new LatLng(ubicacionCurrent.getLatitud(), ubicacionCurrent.getLongitud());
            mapVisualizacion.addMarker(new MarkerOptions().position(currentUbication).title(nombreLugar));
            mapVisualizacion.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
            mapVisualizacion.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ubicacionCurrent.getLatitud(), ubicacionCurrent.getLongitud()), 12.0f));
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    private void CerrarVentana()
    {
        this.finish();
    }
}
