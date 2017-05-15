package Entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by carolina on 13/05/2017.
 */

public class Ubicacion implements Serializable {

    private  int ID;
    private Date   FechaHora ;
    private String Descripcion ;
    private int  NivelRiesgo ;
    private float  Latitud;
    private float  Longitud ;
    private String Direccion ;

    public Ubicacion()
    {

    }

    public Ubicacion(int ID, Date fechaHora, String descripcion, int nivelRiesgo, float latitud, float longitud, String direccion) {
        this.ID = ID;
        FechaHora = fechaHora;
        Descripcion = descripcion;
        NivelRiesgo = nivelRiesgo;
        Latitud = latitud;
        Longitud = longitud;
        Direccion = direccion;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getFechaHora() {
        return FechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        FechaHora = fechaHora;
    }

    public String getDescripcion() {
        if (Descripcion == null)
            Descripcion = "";
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public int getNivelRiesgo() {
        return NivelRiesgo;
    }

    public void setNivelRiesgo(int nivelRiesgo) {
        NivelRiesgo = nivelRiesgo;
    }

    public float getLatitud() {
        return Latitud;
    }

    public void setLatitud(float latitud) {
        Latitud = latitud;
    }

    public float getLongitud() {
        return Longitud;
    }

    public void setLongitud(float longitud) {
        Longitud = longitud;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }
}
