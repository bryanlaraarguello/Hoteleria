package Modelo;

public class IndicadoresHotel {
    private final int totalHabitaciones;
    private final int habitacionesOcupadas;
    private final int habitacionesNoOcupadas;
    private final int reservasConfirmadas;
    private final int reservasVencidas;
    private final long ingresosEsperados;

    public IndicadoresHotel(int totalHabitaciones, int habitacionesOcupadas, int habitacionesNoOcupadas,
            int reservasConfirmadas, int reservasVencidas, long ingresosEsperados) {
        this.totalHabitaciones = totalHabitaciones;
        this.habitacionesOcupadas = habitacionesOcupadas;
        this.habitacionesNoOcupadas = habitacionesNoOcupadas;
        this.reservasConfirmadas = reservasConfirmadas;
        this.reservasVencidas = reservasVencidas;
        this.ingresosEsperados = ingresosEsperados;
    }

    public int getTotalHabitaciones() {
        return totalHabitaciones;
    }

    public int getHabitacionesOcupadas() {
        return habitacionesOcupadas;
    }

    public int getHabitacionesNoOcupadas() {
        return habitacionesNoOcupadas;
    }

    public int getReservasConfirmadas() {
        return reservasConfirmadas;
    }

    public int getReservasVencidas() {
        return reservasVencidas;
    }

    public long getIngresosEsperados() {
        return ingresosEsperados;
    }

    public String getResumen() {
        return "Habitaciones: " + totalHabitaciones + " | Ocupadas: " + habitacionesOcupadas
                + " | No ocupadas: " + habitacionesNoOcupadas + " | Confirmadas: " + reservasConfirmadas
                + " | Vencidas: " + reservasVencidas + " | Ingresos esperados: " + ingresosEsperados;
    }
}
