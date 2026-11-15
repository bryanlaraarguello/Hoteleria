/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author bryan
 */
public abstract class Habitacion {
    
private final int numero;

    public Habitacion(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public abstract String getTipo();

    public abstract int getCapacidadMaxima();

    /** Tarifa por noche en colones enteros. */
    public abstract long getTarifaPorNoche();

    /** Texto para mostrar la habitación en un JComboBox. */
    public String getEtiqueta() {
        return "Hab. " + numero + " - " + getTipo();
    }
    
}
