package com.example.seguimiento;

import androidx.annotation.NonNull;

public class Registro {
    public String nombre;
    public String area;
    public String sku;
    public String fecha;
    public String talla;
    public int cantidad; // Nuevo campo

    public Registro(String nombre, String area, String sku, String fecha, String talla) {
        this.nombre = nombre;
        this.area = area;
        this.sku = sku;
        this.fecha = fecha;
        this.talla = talla;
        this.cantidad = 1; // Inicializa en 1
    }

    @NonNull
    @Override
    public String toString() {
        return nombre + " | " + area + " | " + sku + " | " + fecha + " | Talla: " + talla + " | Cantidad: " + cantidad ;
    }
}
