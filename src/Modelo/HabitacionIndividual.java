/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author bryan
 */
public class HabitacionIndividual extends Habitacion {
    public HabitacionIndividual(int numero) {
        super(numero);
    }

    @Override
    public String getTipo() {
        return "Individual";
    }

    @Override
    public int getCapacidadMaxima() {
        return 1;
    }

    @Override
    public long getTarifaPorNoche() {
        return 25000;
    }
}
