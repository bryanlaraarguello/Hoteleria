/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Controladores;
import Modelo.IndicadoresHotel;
/**
 *
 * @author bryan
 */
public interface ProcesosListener {
    /** Indicadores intermedios calculados por el hilo ActualizadorIndicadores. */
    void indicadoresActualizados(IndicadoresHotel indicadores);

    /** Mensaje cuando el hilo SupervisorReservas termina correctamente. */
    void supervisionFinalizada(String mensaje);

    /** Ambos hilos terminaron. Recibe los indicadores definitivos. */
    void procesosFinalizados(IndicadoresHotel indicadoresFinales);
}

