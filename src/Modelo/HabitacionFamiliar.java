package Modelo;
public class HabitacionFamiliar extends Habitacion {

    public HabitacionFamiliar(int numero) {
        super(numero);
    }

    @Override
    public String getTipo() {
        return "Familiar";
    }

    @Override
    public int getCapacidadMaxima() {
        return 4;
    }

    @Override
    public long getTarifaPorNoche() {
        return 65000;
    }
}