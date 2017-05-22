package Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Entities.Ubicacion;
import Entities.UbicacionFiltros;
import Utiles.Comunes;

/**
 * Created by carolina on 21/05/2017.
 */

public class AccesoBaseDatos extends SQLiteOpenHelper {
    public AccesoBaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "BDRiesgo", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table ubicacion(" +
                "ID integer primary key autoincrement," +//0
                "FechaHora DATETIME," +//1
                "Descripcion text," +//2
                "NivelRiesgo text," + //3
                "Latitud REAL," +
                "Longitud REAL," +
                "Direccion text" +
                ")";

        //Nivel de riesgo
        /*
       1. Verde	 No existe la posibilidad predecible de un suceso natural.
       2. Amarilla	 Posibilidad de un suceso natural en los próximos días o semanas.
       3. Naranja
       4. Posibilidad de un suceso natural en las próximas horas.
       5. Roja Suceso natural inminente o en curso.
       */

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Método para abrir la BD
    public void AbrirBD() {
        this.getWritableDatabase();
    }

    //Método para cerrar la BD
    public void CerrarBD() {
        this.close();
    }

    public void CrearUbicacion(Ubicacion ubicacion) {

        String fecha = String.valueOf(Comunes.FechaFormato(new Date(), "yyyy-MM-dd HH:mm:ss"));

        Log.d("CrearUbicacion","fecha: "+fecha);

        ContentValues valores = new ContentValues();
        valores.put("FechaHora", fecha);
        valores.put("Descripcion", ubicacion.getDescripcion());
        valores.put("NivelRiesgo", ubicacion.getNivelRiesgo());
        valores.put("Latitud", ubicacion.getLatitud());
        valores.put("Longitud", ubicacion.getLongitud());
        valores.put("Direccion", ubicacion.getDireccion());

        Log.d("CrearUbicacion","fecha: "+fecha);

        this.getWritableDatabase().insert("ubicacion", null, valores);
    }

    public boolean EliminarRegistroUbicacion(int ID) {
        return this.getWritableDatabase().delete("ubicacion", "ID = ?", new String[]{String.valueOf(ID)})>0;
    }

    private Cursor BuscarUbicaciones(UbicacionFiltros filtros) {
        Cursor mcursor = null;
        String condicion = "";
        String descripcion = filtros.getDescripcion();

        if ((descripcion != "") && (descripcion != null))
            condicion = " and Descripcion like '%" + descripcion + "%'";

        if (condicion.length() > 0) {
            condicion = condicion.substring(4);
        }

        Log.d("BD", "condicion: " + condicion);

        mcursor = this.getReadableDatabase().query(
                "ubicacion",
                new String[]{"ID", "FechaHora", "Descripcion", "NivelRiesgo", "Latitud", "Longitud", "Direccion"},
                condicion,
                null, null, null, null
        );

        return mcursor;
    }

/*
    public ArrayList BuscarUbicacionesFiltros(UbicacionFiltros filtros) {

        Log.d("BD", "BuscarUbicacionesFiltros");

        ArrayList<String> lista = new ArrayList();

        Cursor mcursor = BuscarUbicaciones(filtros);

        if (mcursor.moveToFirst()) {
            do {
                lista.add(mcursor.getString(1));
            } while (mcursor.moveToNext());
        }

        return lista;
    }
    */


    public List<Ubicacion> BuscarUbicacionesFiltros(UbicacionFiltros filtros) {

        Log.d("BD", "BuscarUbicacionesFiltros");
        Ubicacion ubicacion;
        List<Ubicacion> lista = new ArrayList<Ubicacion>();

        Cursor mcursor = BuscarUbicaciones(filtros);

        if (mcursor.moveToFirst()) {
            do {
                lista.add(new Ubicacion(
                        mcursor.getInt(0),
                        new Date(),
                        mcursor.getString(2),
                        mcursor.getString(3),
                        mcursor.getDouble(4),
                        mcursor.getDouble(5),
                        mcursor.getString(6)
                ));
            } while (mcursor.moveToNext());
        }

        Log.d("BD", "Size: "+lista.size());

        return lista;
    }


    public ArrayList BuscarTodosUbicaciones() {
        int cantidad = 0;
        Log.d("todos", "BuscarUbicacionesFiltros");
        ArrayList<String> lista = new ArrayList();
        Cursor mcursor = null;
        String query = "SELECT * FROM ubicacion";

        mcursor = this.getReadableDatabase().rawQuery(query, null);

        if (mcursor.moveToFirst()) {
            do {
                Log.d("todos", "BuscarUbicacionesFiltros");
                lista.add(mcursor.getString(2));

                Log.d("todos", "BuscarUbicacionesFiltros");
                cantidad += 1;
            } while (mcursor.moveToNext());
        }
        Log.d("todos", "cantidad: " + cantidad);
        return lista;
    }




}

