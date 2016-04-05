package mundo;

import android.text.format.Time;

/**
 * Created by ca.escobar2434 on 3/1/16.
 */
public class Guardia {
    private static Guardia instancia;
    private long inicioTurno;
    private String numeroSupervisor;
    private int idGuardia;
    private boolean mostrarRecomendacion;


    public static Guardia darGuardia() {
        if (instancia == null) {
            instancia = new Guardia();
        }
        return instancia;
    }

    public void inicializar(String numeroSupervisor, int idGuardia) {
        this.numeroSupervisor = numeroSupervisor;
        inicioTurno = System.currentTimeMillis();
        this.idGuardia = idGuardia;
        mostrarRecomendacion = true;
    }

    public Guardia() {
        numeroSupervisor = "";
        idGuardia=0;
        mostrarRecomendacion = true;
    }

    public String darNumeroSupervisor() {
        return numeroSupervisor;
    }

    public void recordarMensaje(boolean showAgain) {
        mostrarRecomendacion = showAgain;
    }
    public boolean mostrarRecomendacion()
    {
        return mostrarRecomendacion;
    }
}

