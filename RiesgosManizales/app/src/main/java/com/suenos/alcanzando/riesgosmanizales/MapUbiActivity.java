package com.suenos.alcanzando.riesgosmanizales;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

import Data.AccesoBaseDatos;
import Entities.Accion;
import Entities.ConexionBD;
import Entities.NivelRiesgo;
import Entities.Ubicacion;

public class MapUbiActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final static int PERMISSION_FINE_LOCATION = 101;
    protected  static  final  String TAG= "MapsActivity";
    Double myLatitud=null;
    Double  myLongitud=null;


    private GoogleApiClient googleApiClient;
    //private LocationRequest locationRequest;

    //Controles de usariio
    ZoomControls zcZoom;
    Button btMark;
    EditText etLocationEntity;
    //
    //controles para almacenar
    EditText txtDescripcion;
    Spinner spNivel;
    EditText txtDireccion;

    Button btnCrear;

    String city="";

    private LocationManager locationManager;

    String [] listaNivel;

    Accion CurrentAccion = Accion.Crear;

    AccesoBaseDatos accesoDatos ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

/*
        locationRequest = new LocationRequest();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    */


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000,
                        10, this);

            }

        AsignarControles();
    }

    private void AsignarControles() {
        zcZoom = (ZoomControls) findViewById(R.id.zcZoom);
        btMark = (Button) findViewById(R.id.btMark);

        btnCrear= (Button) findViewById(R.id.btnCrear);

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        spNivel = (Spinner) findViewById(R.id.spNivel);
        txtDireccion = (EditText) findViewById(R.id.txtDireccion);

        EventoButtonzcZoom();
        EventoButtonbtMark();
        LlenarSpinnerspNivel();
        EventoButtonbtnCrear();

        btnCrear.setText(CurrentAccion.toString());
    }

    private void EventoButtonbtnCrear() {
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    switch (CurrentAccion) {
                        case Crear:
                            CrearRegistro();
                            break;
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CrearRegistro() throws Exception {

        String direccion = txtDireccion.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String nivelRiesto = spNivel.getSelectedItem().toString();

        if (descripcion.length() == 0)
            throw new Exception("El valor para descripcion es incorrecto");

        if (direccion.length() == 0)
            throw new Exception("El valor para direccion es incorrecto");

        Ubicacion ubicacion = new Ubicacion(descripcion, nivelRiesto, myLongitud, myLatitud, direccion);

        accesoDatos.CrearUbicacion(ubicacion);
        Toast.makeText(getApplicationContext(), "Registro Almacenado con Exito", Toast.LENGTH_LONG).show();
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

    private void EventoButtonbtMark() {
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myLongitud = location.getLongitude();
        myLatitud = location.getLatitude();



        /*String msg = "New Latitude: "+location.getLatitude()+"New Longitude: "+location.getLongitude();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        */

        txtDireccion.setText(ObtenerDireccion());

        LatLng currentUbication = new LatLng(myLatitud, myLongitud);
        mMap.addMarker(new MarkerOptions().position(currentUbication).title(city));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
    }

    private String ObtenerDireccion()  {
        try {
            Geocoder geocoder;
            List<android.location.Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(myLatitud, myLongitud, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            String res=" address: "+address;
            res+=". city: "+city;
            res+=". state: "+state;
            res+=". country: "+country;
            res+=". postalCode: "+postalCode;
            res+=". knownName: "+knownName;

            return address;
        }
        catch (Exception ex)
        {
            return ex.getMessage();
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
