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

    public abstract long getTarifaPorNoche();

    public String getEtiqueta() {
        return "Hab. " + numero + " - " + getTipo();
    }
    
}
