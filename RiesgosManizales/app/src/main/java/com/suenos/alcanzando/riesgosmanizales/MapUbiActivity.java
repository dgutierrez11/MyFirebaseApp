package com.suenos.alcanzando.riesgosmanizales;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import Data.AccesoBaseDatos;
import Entities.Accion;
import Entities.ConexionBD;
import Entities.Direccion;
import Entities.NivelRiesgo;
import Entities.Ubicacion;
import Utiles.Comunes;

public class MapUbiActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final static int PERMISSION_FINE_LOCATION = 101;
    protected  static  final  String TAG= "MapsActivity";
    Double myLatitud=null;
    Double  myLongitud=null;


    private GoogleApiClient googleApiClient;
    //private LocationRequest locationRequest;

    //Controles de usariio
    ZoomControls zcZoom;
    //Button btMark;
    EditText etLocationEntity;
    //
    //controles para almacenar
    EditText txtDescripcion;
    Spinner spNivel;
    EditText txtDireccion;
    EditText txtFecha;
    TextView lblFecha;
    Button btnAccion;

    String city="";

    private LocationManager locationManager;

    String [] listaNivel;

    Accion CurrentAccion = Accion.Crear;

    AccesoBaseDatos accesoDatos ;

    Ubicacion ubicacionCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map_ubi);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000,
                        10, this);
            }

            AsignarControles();
            ObtenerDatosVisualizacion();
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void ObtenerDatosVisualizacion() {

        Intent intent = getIntent();
        ubicacionCurrent = (Ubicacion) intent.getSerializableExtra("ubicacion");
        Accion accion = (Accion) intent.getSerializableExtra("CurrentAccion");
        String fecha;
        if (accion != null) {
            CurrentAccion = accion;
        }

        switch (CurrentAccion) {
            case Visualizar:
                //Asigna los valores a los controles
                txtDescripcion.setText(ubicacionCurrent.getDescripcion());
                spNivel.setSelection(Comunes.getIndexSpinner(spNivel, ubicacionCurrent.getNivelRiesgo()));
                txtDireccion.setText(ubicacionCurrent.getDireccion());
                fecha = String.valueOf(Comunes.FechaFormato(ubicacionCurrent.getFechaHora(), "yyyy-MM-dd HH:mm:ss"));
                txtFecha.setText(fecha);
                lblFecha.setVisibility(View.VISIBLE);
                txtFecha.setVisibility(View.VISIBLE);
                //
                txtFecha.setEnabled(false);
                btnAccion.setText("Eliminar");
                break;
            case Crear:
                btnAccion.setText("Crear");
                break;
        }
    }



    private void AsignarControles() {
        zcZoom = (ZoomControls) findViewById(R.id.zcZoom);
        //btMark = (Button) findViewById(R.id.btMark);

        btnAccion = (Button) findViewById(R.id.btnAccion);

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        spNivel = (Spinner) findViewById(R.id.spNivel);
        txtDireccion = (EditText) findViewById(R.id.txtDireccion);
        txtFecha = (EditText) findViewById(R.id.txtFecha);
        lblFecha = (TextView) findViewById(R.id.lblFecha);

        EventoButtonzcZoom();
        //EventoButtonbtMark();
        LlenarSpinnerspNivel();
        EventoButtonbtnCrear();


    }

    private void EventoButtonbtnCrear() {
        btnAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    switch (CurrentAccion) {
                        case Crear:
                            CrearRegistro();
                            break;
                        case Visualizar:
                            EliminarRegistro();
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void EliminarRegistro()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seguro desea eliminar")
                .setTitle("Confirmar");

        // Add the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                boolean respuesta= accesoDatos.EliminarRegistroUbicacion(ubicacionCurrent.getID());

                if(respuesta) {
                    Toast.makeText(getApplicationContext(), "El registro se eliminó de manera satisfactoria", Toast.LENGTH_LONG).show();
                    CerrarActividad();
                }
                else
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

    private void CerrarActividad()
    {
        this.finish();
    }

    private void CrearRegistro() throws Exception {

        String direccion = txtDireccion.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String nivelRiesto = spNivel.getSelectedItem().toString();

        if (descripcion.length() == 0)
            throw new Exception("El valor para descripcion es incorrecto");

        if (direccion.length() == 0)
            throw new Exception("El valor para direccion es incorrecto");

        Ubicacion ubicacion = new Ubicacion(descripcion, nivelRiesto, myLatitud, myLongitud, direccion);

        accesoDatos.CrearUbicacion(ubicacion);
        Toast.makeText(getApplicationContext(), "Registro Almacenado con Exito", Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void LlenarSpinnerspNivel() {
        listaNivel = new String[]{
                NivelRiesgo.Verde.toString(),
                NivelRiesgo.Amarilla.toString(),
                NivelRiesgo.Naranja.toString(),
                NivelRiesgo.Roja.toString(),
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaNivel);

        spNivel.setAdapter(adapter);
    }

    /*private void EventoButtonbtMark() {
        btMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent i = new Intent(getApplicationContext(), ListadoActivity.class);
                    //i.putExtra("ubicacion", (Serializable) ubicacion);
                    startActivity(i);

                    //LatLng mLocation = new LatLng(myLatitud, myLongitud);
                    //mMap.addMarker(new MarkerOptions().position(mLocation).title("Marker in Sydney"));
                }
                catch(Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("onRequestPe..", ex.getMessage());
                }
            }
        });
    }
    */

    private void EventoButtonzcZoom() {

        zcZoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        zcZoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Direccion direccion;
        String city="";
        try {

            // Add a marker in Sydney and move the camera
            LatLng currentUbication = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(currentUbication).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }
            }

            switch (CurrentAccion) {
                case Crear:
                    currentUbication = new LatLng(-34, 151);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
                    break;
                case Visualizar:
                    direccion = ObtenerDireccion(ubicacionCurrent.getLatitud(), ubicacionCurrent.getLongitud());
                    if(direccion!=null) {
                        city = direccion.getCity() + ", " + direccion.getState();
                    }
                    currentUbication = new LatLng(ubicacionCurrent.getLatitud(), ubicacionCurrent.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(currentUbication).title(city));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
                    Toast.makeText(getApplicationContext(), "Latitud: " + ubicacionCurrent.getLatitud() + ". Longitud: " + ubicacionCurrent.getLongitud(), Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onRequestPe..", ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        try {
            super.onRequestPermissionsResult(requestCode, permission, grantResult);

            switch (requestCode) {
                case PERMISSION_FINE_LOCATION:
                    if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Esta aplicación requiere permiso de localización", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onRequestPe..", ex.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            if (googleApiClient.isConnected()) {
                //requestLocationUpdates();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onConnected", ex.getMessage());
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        try {
            Log.i(TAG, "Connection Suspended");
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onConnectionSuspended", ex.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            Log.i(TAG, "Connection Failed: connectionResult.getErrorCode(): " + connectionResult.getErrorCode());
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("onConnectionFailed", ex.getMessage());
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        Direccion direccion;

        if (CurrentAccion != Accion.Visualizar) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            myLongitud = location.getLongitude();
            myLatitud = location.getLatitude();

        /*String msg = "New Latitude: "+location.getLatitude()+"New Longitude: "+location.getLongitude();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        */
            direccion = ObtenerDireccion(myLatitud, myLongitud);
            if(direccion!=null) {
                txtDireccion.setText(direccion.getAddress());
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Problema al obtener la dirección", Toast.LENGTH_LONG).show();
            }
            LatLng currentUbication = new LatLng(myLatitud, myLongitud);
            //mMap.addMarker(new MarkerOptions().position(currentUbication).title(city));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
        }
    }

    private Direccion ObtenerDireccion(double latitud, double longitud) {

        try {
            Geocoder geocoder;
            Direccion direccion;

            List<android.location.Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitud, longitud, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            direccion = new Direccion(address, city, state, country, postalCode, knownName);

            return direccion;
        } catch (Exception ex) {
            return  null;
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
