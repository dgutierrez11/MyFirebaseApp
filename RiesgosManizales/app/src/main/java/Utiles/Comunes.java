package Utiles;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Entities.Direccion;

/**
 * Created by carolina on 21/05/2017.
 */

public class Comunes {
    public static String FechaFormato(Date fecha, String formato) {

        String f;
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);

        f = dateFormat.format(fecha);
        return f;
    }

    public static int getIndexSpinner(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }


    public static Direccion ObtenerDireccion(Context contexto, double latitud, double longitud) {

        try {
            Geocoder geocoder;
            Direccion direccion = null;

            List<Address> addresses;
            geocoder = new Geocoder(contexto, Locale.getDefault());

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

            return null;
        }
    }

}

