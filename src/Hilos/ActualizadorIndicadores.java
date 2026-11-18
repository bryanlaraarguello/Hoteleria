/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hilos;


import Controladores.ControladorHotel;
import Modelo.GestorHotel;
import Modelo.IndicadoresHotel;

public class ActualizadorIndicadores implements Runnable {

    public static final int CICLOS = 5;
    public static final long PAUSA_MS = 1000;

    private final GestorHotel gestor;
    private final ControladorHotel controlador;

    public ActualizadorIndicadores(GestorHotel gestor, ControladorHotel controlador) {
        this.gestor = gestor;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        String nombre = Thread.currentThread().getName();
        System.out.println("[" + nombre + "] ActualizadorIndicadores: inicio de ejecución");
        try {
            for (int ciclo = 1; ciclo <= CICLOS; ciclo++) {
                IndicadoresHotel indicadores = gestor.calcularIndicadores();
                System.out.println("[" + nombre + "] ActualizadorIndicadores: ciclo " + ciclo + "/" + CICLOS
                        + " -> " + indicadores.getResumen());
                controlador.publicarIndicadores(indicadores);
                if (ciclo < CICLOS) {
                    Thread.sleep(PAUSA_MS);
                }
            }
            System.out.println("[" + nombre + "] ActualizadorIndicadores: finalizado correctamente");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + nombre + "] ActualizadorIndicadores: interrumpido, se detiene antes de terminar");
        } finally {
            controlador.hiloTerminado();
        }
    }
}

