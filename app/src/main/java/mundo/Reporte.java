package mundo;

import java.util.ArrayList;

/**
 * Created by Juan Gomez on 29/02/2016.
 */
public class Reporte {

    //--------------------------------
    // Atributos
    //--------------------------------

    public String id;

    private ArrayList<String> imagenes;

    private String grabacion;

    private String asuunto;

    //--------------------------------
    // Metodos
    //--------------------------------

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
