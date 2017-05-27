package Utiles;

import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}

