package Utiles;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static android.R.attr.value;

/**
 * Created by carolina on 14/05/2017.
 */

public class Comunes {
    public static String FechaFormato(Date fecha, String formato) {

        String f;
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);

        f = dateFormat.format(fecha);
        return f;
    }

    public static int CantidadElementos(ArrayList lista) {
        int count = 0;

        if (lista != null && !lista.isEmpty()) {
            Iterator iter = lista.iterator();
            double itemValue = 0;
            String itemId = "";

            Log.d("CantidadElementos", "count: " + count);

            while (iter.hasNext()) {
                count += 1;
                Log.d("CantidadElementos", "count: " + count);
            }
        }

        return count;
    }
}
