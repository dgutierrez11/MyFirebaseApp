package Utiles;

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
}

