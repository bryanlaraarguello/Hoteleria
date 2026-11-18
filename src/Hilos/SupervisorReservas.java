package Hilos;

import java.time.LocalDate;

import Controladores.ControladorHotel;
import Modelo.GestorHotel;

public class SupervisorReservas implements Runnable {

    public static final int CICLOS = 5;
    public static final long PAUSA_MS = 1000;

    private final GestorHotel gestor;
    private final ControladorHotel controlador;

    public SupervisorReservas(GestorHotel gestor, ControladorHotel controlador) {
        this.gestor = gestor;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        String nombre = Thread.currentThread().getName();
        int totalVencidas = 0;
        System.out.println("[" + nombre + "] SupervisorReservas: inicio de ejecución");
        try {
            for (int ciclo = 1; ciclo <= CICLOS; ciclo++) {
                int actualizadas = gestor.marcarVencidas(LocalDate.now());
                totalVencidas = totalVencidas + actualizadas;
                System.out.println("[" + nombre + "] SupervisorReservas: ciclo " + ciclo + "/" + CICLOS
                        + " -> reservas marcadas como Vencida: " + actualizadas);
                if (ciclo < CICLOS) {
                    Thread.sleep(PAUSA_MS);
                }
            }
            System.out.println("[" + nombre + "] SupervisorReservas: finalizado correctamente");
            controlador.publicarResumenSupervision("Supervisión de reservas finalizada correctamente.\n"
                    + "Reservas marcadas como Vencida: " + totalVencidas);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + nombre + "] SupervisorReservas: interrumpido, se detiene antes de terminar");
        } finally {
            controlador.hiloTerminado();
        }
    }
}

