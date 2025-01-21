package com.example.seguimiento;

import androidx.annotation.NonNull;

public class Registro {
    String nombre;
    String area;
    String codigo;
    String fecha;
    String talla;
    String sku;

    public Registro(String nombre, String area, String codigo, String fecha, String talla, String sku) {
        this.nombre = nombre;
        this.area = area;
        this.codigo = codigo;
        this.fecha = fecha;
        this.talla = talla;
        this.sku = sku;
    }

    @NonNull
    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Área: " + area +
                ", Código: " + codigo + ", Fecha: " + fecha +
                ", Talla: " + talla + ", SKU: " + sku;
    }
}
