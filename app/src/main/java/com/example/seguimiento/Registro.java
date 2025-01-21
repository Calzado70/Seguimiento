package com.example.seguimiento;

public class Registro {
    public String nombre;
    public String area;
    public String codigo;
    public String fecha;
    public String talla;
    public String sku;
    public int cantidad; // Nuevo campo

    public Registro(String nombre, String area, String codigo, String fecha, String talla, String sku) {
        this.nombre = nombre;
        this.area = area;
        this.codigo = codigo;
        this.fecha = fecha;
        this.talla = talla;
        this.sku = sku;
        this.cantidad = 1; // Inicializa en 1
    }

    @Override
    public String toString() {
        return nombre + " | " + area + " | " + codigo + " | " + fecha + " | Talla: " + talla + " | SKU: " + sku + " | Cantidad: " + cantidad;
    }
}
