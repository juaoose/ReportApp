package mundo;

import android.text.format.Time;

/**
 * Created by ca.escobar2434 on 3/1/16.
 */
public class Guardia {
    public static Guardia instancia;
    private long inicioTurno;
    private String numeroSupervisor;
    private int idGuardia;


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
    }

    public Guardia() {
        numeroSupervisor = "";
        idGuardia=0;
    }

    public String darNumeroSupervisor() {
        return numeroSupervisor;
    }
}
