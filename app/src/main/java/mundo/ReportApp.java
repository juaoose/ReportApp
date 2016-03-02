package mundo;

import java.util.ArrayList;

/**
 * Created by Usuario Autorizado on 02/03/2016.
 */
public class ReportApp {

    ArrayList<Reporte> reportes;
    Guardia guardia;
    private static ReportApp instancia;

    public ReportApp(){
        reportes = new ArrayList<Reporte>();
        guardia = new Guardia();
    }

    public static ReportApp darInstancia(){
        if(instancia == null){
            instancia = new ReportApp();
        }
        return instancia;
    }

    public ArrayList<Reporte> darReportes(){
        return reportes;
    }

    public void agregarReporte(Reporte r){
        reportes.add(r);
    }

    public String[] darIdReportes(){
        String[] res = new String[reportes.size()];
        for(int i = 0; i < reportes.size() ; i++){
            res[i] = reportes.get(i).id;
        }
        return res;
    }
}
