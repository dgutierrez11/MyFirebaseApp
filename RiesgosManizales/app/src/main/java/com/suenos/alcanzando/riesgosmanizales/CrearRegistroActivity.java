package com.suenos.alcanzando.riesgosmanizales;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import Data.AccesoBaseDatos;
import Entities.ConexionBD;
import Entities.Direccion;
import Entities.NivelRiesgo;
import Entities.Ubicacion;

public class CrearRegistroActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //region Constantes
    private final static int PERMISSION_FINE_LOCATION = 101;
    protected static final String TAG = "MapsActivity";
    protected static final String TAGEror = "MapsActivity Error";
    //endregion Constantes

    //region variables para almacenar la latitud y longitud
    Double myLatitud = null;
    Double myLongitud = null;
    //endregion controles para almacenar

    //region variables para para el manejo del mapa y location
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    //endregion variables para para el manejo del mapa y location

    //region controles
    private GoogleMap mMap;
    EditText txtDescripcion;
    Spinner spNivel;
    EditText txtDireccion;
    EditText txtFecha;
    TextView lblFecha;
    Button btnAccion;
    TextView lblDescripcion;
    //endregion Controles

    //Variable para el acceso a la base de datos
    AccesoBaseDatos accesoDatos;

    //variable para determinar si el usuario escribió algo en dirección, si es así no se le asigna la dirección
    //por utilizando los datos de latitud y longitud
    boolean AsignarDireccion=true;

    int FallosObtenerDireccion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_crear_registro);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CerrarActividad();
                }
            });

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(15 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            accesoDatos = ConexionBD.getAccesoDatos(getApplicationContext());

            AsignarControles();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG ).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    //Asigna los controles de visualización a propiedades de la clase
    private void AsignarControles() {

        btnAccion = (Button) findViewById(R.id.btnAccion);

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        spNivel = (Spinner) findViewById(R.id.spNivel);
        txtDireccion = (EditText) findViewById(R.id.txtDireccion);
        lblDescripcion= (TextView) findViewById(R.id.lblDescripcion);

        LlenarSpinnerspNivel();
        EventoButtonbtnCrear();
        EventotxtDireccion();

    }

    //region Eventos de los controles de usuairo
    //Asigna el evento al control Direccion para asignar  la propiedad AsignarDireccion en false
    private void EventotxtDireccion() {
        txtDireccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AsignarDireccion=false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Asigna el evento al control botón crear
    private void EventoButtonbtnCrear() {
        btnAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    CrearRegistro();

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //endregion Eventos de los controles de usuairo

    //region métodos de la clase
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

    private void CerrarActividad() {
        this.finish();
    }

    private void LlenarSpinnerspNivel() {
        String[]  listaNivel = new String[]{
                NivelRiesgo.Verde.toString(),
                NivelRiesgo.Amarilla.toString(),
                NivelRiesgo.Naranja.toString(),
                NivelRiesgo.Roja.toString(),
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaNivel);

        spNivel.setAdapter(adapter);
    }

    private Direccion ObtenerDireccion(double latitud, double longitud) {

        try {
            Geocoder geocoder;
            Direccion direccion = null;


            if(FallosObtenerDireccion>3)
                return null;

            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitud, longitud, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            direccion = new Direccion(address, city, state, country, postalCode, knownName);

            return direccion;
        } catch (Exception ex) {
            FallosObtenerDireccion +=1;
            Log.d(TAGEror, ex.getMessage());
            return null;
        }
    }

    //endregion métodos de la clase



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

        try {

            // Add a marker in Sydney and move the camera
            LatLng currentUbication = new LatLng(-34, 151);
            //mMap.addMarker(new MarkerOptions().position(currentUbication).title("Marker in Sydney"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
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
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            //if (googleApiClient.isConnected()) {
            requestLocationUpdates();
            //}
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            Toast.makeText(this, "Entro ", Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        try {
            Log.i(TAG, "Connection Suspended");
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            Log.i(TAG, "Connection Failed: connectionResult.getErrorCode(): " + connectionResult.getErrorCode());
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }



    @Override
    public void onLocationChanged(Location location) {

        try {

            Direccion direccion;

            myLongitud = location.getLongitude();
            myLatitud = location.getLatitude();

            if (AsignarDireccion) {
                direccion = ObtenerDireccion(myLatitud, myLongitud);
                if (direccion != null) {
                    txtDireccion.setText(direccion.getAddress());
                } else {
                    Toast.makeText(getApplicationContext(), "Problema al obtener la dirección", Toast.LENGTH_LONG).show();
                }
            }

            LatLng currentUbication = new LatLng(myLatitud, myLongitud);
            //mMap.addMarker(new MarkerOptions().position(currentUbication).title(city));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUbication));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUbication, 12.0f));

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    @Override
    protected void onStart() {
        try
        {
            super.onStart();
            googleApiClient.connect();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAGEror, ex.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        }
        catch (Exception ex)
        {
            Log.d(TAGEror, ex.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

}
