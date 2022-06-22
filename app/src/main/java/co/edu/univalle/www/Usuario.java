package co.edu.univalle.www;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String strCorreo = "";

    public String getCorreo(){
        return strCorreo;
    }

    public void setCorreo(String strCorreo){
        this.strCorreo = strCorreo;
    }

}
