package co.edu.univalle.www.modelo;

import android.content.Context;
import android.util.Log;

import co.edu.univalle.www.DBLocalConection;

public class Sesion {

    String loggedUser;
    boolean isLogged;
    DBLocalConection conexion;

    public Sesion(Context context){
        conexion = DBLocalConection.getInstance(context);
        checkForUser();
    }



    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
        this.isLogged = true;
        conexion.insertarInformacionSesion(loggedUser);
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void logOut(){
        conexion.eliminarInformacionSesion(loggedUser);
        loggedUser = "";
        isLogged = false;

    }

    public void checkForUser(){
        String id = conexion.obtenerIdUsuario();
        if(!id.equals("")){
            setLoggedUser(id);
        }

        Log.d("APPMSG: Resultado consulta", id);
        //Log.d("APPMSG: Resultado consulta", conexion.obtenerIdUsuario().toString());

    }
}
