package com.rubenbp.android.a3dviewer;

/**
 * Created by ruben on 22/11/2017.
 */

public class Modelo {


    private int id;
    private String nombre;
    private String tipo;

    public Modelo(int id, String nombre, String tipo)
    {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
