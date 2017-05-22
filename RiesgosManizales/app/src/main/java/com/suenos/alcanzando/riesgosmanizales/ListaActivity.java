package com.suenos.alcanzando.riesgosmanizales;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import Data.AccesoBaseDatos;
import Entities.ConexionBD;
import Entities.Ubicacion;
import Entities.UbicacionFiltros;

public class ListaActivity extends AppCompatActivity {

    AccesoBaseDatos accesoDatos;
    ListView lvUbicaciones;
    FloatingActionButton fab;
    Button btnBuscar;
    EditText txtDescFiltro;
    private List<Ubicacion> listaUbicaciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lista);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getApplicationContext(), MapUbiActivity.class);
                    //i.putExtra("ubicacion", (Serializable) ubicacion);
                    startActivity(i);


                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            ///
            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());

            AsignarControles();

            LlenarLista();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onCreate", ex.getMessage());
        }
    }

    private void AsignarControles() {
        lvUbicaciones = (ListView) findViewById(R.id.lvUbicaciones);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        txtDescFiltro = (EditText) findViewById(R.id.txtBuscar);

        CrearEventoClickBtnBuscar();
        CrearEventoClicklvUbicaciones();
        //EventoButtonFab();
        registerForContextMenu(lvUbicaciones);
    }

    private void CrearEventoClickBtnBuscar() {

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LlenarLista();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Visualizar", ex.getMessage());
                }
            }
        });
    }

    private void CrearEventoClicklvUbicaciones() {
        lvUbicaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Ubicacion ubicacion = listaUbicaciones.get(position);

                    /*
                    Intent i = new Intent(getApplicationContext(), VisualizarUbicacionActivity.class);
                    i.putExtra("ubicacion", (Serializable) ubicacion);
                    startActivity(i);
                    */

                    //Toast.makeText(getApplicationContext(), "PosicÃ³n seleccionada : " + ubicacion.getDescripcion(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
        lvUbicaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ubicacion ubicacion = listaUbicaciones.get(position);



                Toast.makeText(getApplicationContext(), "Desde setOnItemSelectedListener seleccionada : " + ubicacion.getDescripcion(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

/*
        lvUbicaciones.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });*/
    }

    private void LlenarLista() {
        UbicacionFiltros filtros = new UbicacionFiltros();
        filtros.setDescripcion(txtDescFiltro.getText().toString());

        listaUbicaciones = accesoDatos.BuscarUbicacionesFiltros(filtros);

        Log.d("LlenarLista", "Size: " + listaUbicaciones.size());

        ArrayList<String> listData = TransformarDatos(listaUbicaciones); //accesoDatos.BuscarTodosUbicaciones();//.BuscarUbicacionesFiltros(filtros);
        Log.d("LlenarLista", "Size: listData " + listData.size());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listData);
        lvUbicaciones.setAdapter(adapter);
        //Toast.makeText(getApplicationContext(), "Registros encontrados: " + listData.size(), Toast.LENGTH_LONG).show();


    }

    private ArrayList TransformarDatos(List<Ubicacion> lista) {
        int cantidad = lista.size();
        ArrayList<String> arrayl = new ArrayList();
        Ubicacion ubicacion;

        for (int i = 0; i < cantidad; i++) {
            ubicacion = lista.get(i);
            arrayl.add(ubicacion.getDescripcion());
        }
        return arrayl;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        String descripcion;

        if (v.getId() == R.id.lvUbicaciones) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            descripcion = "Eliminar.  " + listaUbicaciones.get(info.position).getDescripcion();

            menu.setHeaderTitle(descripcion);

            String[] menuItems = {"Aceptar"};

            for (int i = 0; i < 1; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int menuItemIndex = item.getItemId();

        Ubicacion ubicacion =  listaUbicaciones.get(info.position);

        boolean respuesta= accesoDatos.EliminarRegistroUbicacion(ubicacion.getID());

        if(respuesta) {
            Toast.makeText(getApplicationContext(), "El registro se eliminó de manera satisfactoria", Toast.LENGTH_LONG).show();
            LlenarLista();
        }
        else
            Toast.makeText(getApplicationContext(), "El registro no pudo ser eliminado", Toast.LENGTH_LONG).show();

        return true;
    }
}
