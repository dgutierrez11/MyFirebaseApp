package com.suenos.alcanzando.riesgosdesastre;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Entities.ConexionBD;
import Entities.Ubicacion;
import Entities.UbicacionFiltros;
import Utiles.Comunes;
import data.AccesoBaseDatos;

public class ListadoActivity extends AppCompatActivity {

    AccesoBaseDatos accesoDatos ;
    ListView lvUbicaciones;
    FloatingActionButton fab;
    Button btnBuscar;
    EditText txtDescFiltro;
    private List<Ubicacion> listaUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_listado);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());

            AsignarControles();
            LlenarLista();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void EventoButtonFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent i = new Intent(getApplicationContext(), CrearUbicacionActivity.class);
                Intent i = new Intent(getApplicationContext(), UbicacionActivity.class);
                startActivity(i);
            }
        });
    }

    private void LlenarLista() {
        UbicacionFiltros filtros = new UbicacionFiltros();
        //filtros.setDescripcion(txtDescFiltro.getText().toString());

        listaUbicaciones =  accesoDatos.BuscarUbicacionesFiltros(filtros);
        ArrayList<String> listData = TransformarDatos(listaUbicaciones); //accesoDatos.BuscarTodosUbicaciones();//.BuscarUbicacionesFiltros(filtros);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listData);
        lvUbicaciones.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Registros encontrados: " + listData.size(), Toast.LENGTH_LONG).show();
    }

    private ArrayList TransformarDatos(List<Ubicacion> lista)
    {
        int cantidad= lista.size();
        ArrayList<String> arrayl = new ArrayList();
        Ubicacion ubicacion;

        for(int i=0; i<cantidad;i++)
        {
            ubicacion =  lista.get(i);
            arrayl.add(ubicacion.getDescripcion());
        }
        return arrayl;
    }


    private void AsignarControles() {
        lvUbicaciones = (ListView) findViewById(R.id.lvUbicaciones);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        txtDescFiltro = (EditText) findViewById(R.id.txtDescFiltro);

        CrearEventoClickBtnBuscar();
        CrearEventoClicklvUbicaciones();
        EventoButtonFab();
    }

    private void CrearEventoClicklvUbicaciones() {
        lvUbicaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Ubicacion ubicacion = listaUbicaciones.get(position);

                    Intent i = new Intent(getApplicationContext(), VisualizarUbicacionActivity.class);
                    i.putExtra("ubicacion", (Serializable) ubicacion);
                    startActivity(i);

                    Toast.makeText(getApplicationContext(), "Posicón seleccionada : " + ubicacion.getDescripcion(), Toast.LENGTH_LONG).show();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CrearEventoClickBtnBuscar() {
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LlenarLista();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Visualizar", ex.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void VisualizarLista()
    {
        UbicacionFiltros filtros = new UbicacionFiltros();
        //filtros.setDescripcion(txtDescFiltro.getText().toString());
        listaUbicaciones = accesoDatos.BuscarUbicacionesFiltros(filtros);

        ArrayAdapter<Ubicacion> adapter = new MyListAdapter();
        lvUbicaciones.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Ubicacion>{

        public MyListAdapter()
        {
            super(ListadoActivity.this, R.layout.item_view_ubicacion, listaUbicaciones);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            try {

                TextView tvDescripcion = (TextView) findViewById(R.id.tvDescricpion_item);

                if (itemView == null)
                    itemView = getLayoutInflater().inflate(R.layout.item_view_ubicacion, parent, false);

                Ubicacion ubicacion = listaUbicaciones.get(position);

                Log.d("position", String.valueOf(position));
                Log.d("ubicacion", String.valueOf(ubicacion.getID()));


                if (ubicacion != null) {
                    tvDescripcion.setText("pruebita");
                }


            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Visualizar", ex.getMessage());
            }
            return itemView;
        }
    }
}
