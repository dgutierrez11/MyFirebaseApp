package Entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by carolina on 21/05/2017.
 */

public class Ubicacion implements Serializable {
    private int ID;
    private Date FechaHora;
    private String Descripcion;
    private String NivelRiesgo;
    private double Latitud;
    private double Longitud;
    private String Direccion;

    public Ubicacion() {

    }
    //Constructor utilizaso para la creación
    public Ubicacion(String descripcion, String nivelRiesgo, double latitud, double longitud, String direccion) {
        Descripcion = descripcion;
        NivelRiesgo = nivelRiesgo;
        Latitud = latitud;
        Longitud = longitud;
        Direccion = direccion;
    }

    public Ubicacion(int ID, Date fechaHora, String descripcion, String nivelRiesgo, double latitud, double longitud, String direccion) {
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

    public String getNivelRiesgo() {
        return NivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        NivelRiesgo = nivelRiesgo;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

}
