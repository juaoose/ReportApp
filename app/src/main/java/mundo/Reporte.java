package mundo;

import java.util.ArrayList;

/**
 * Created by Usuario Autorizado on 29/02/2016.
 */
public class Reporte {

    private String id;

    private ArrayList<String> imagenes;

    private String grabacion;

    private String asuunto;

    public Reporte(String identificador){
        this.id = identificador;
    }

    public void addImage(String imagePath){
        imagenes.add(imagePath);
    }

    public ArrayList<String> getImages(){
        return imagenes;
    }

    public void setAsunto(String asuntoR){
        this.asuunto = asuntoR;
    }

    public String getAsunto(){
        return asuunto;
    }

    public void setGrabacion(String recordingPath){
        this.grabacion = recordingPath;
    }

    public String getGrabacion(){
        return grabacion;
    }
}
