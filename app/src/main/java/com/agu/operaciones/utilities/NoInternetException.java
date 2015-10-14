package com.agu.operaciones.utilities;

/**
 * Created by Cloudco on 14/10/15.
 */
public class NoInternetException extends Exception {

    private String mensaje = "No cuentas con conexión a internet. Intentarlo más tarde";

    public NoInternetException()
    {
        super();
    }

    public NoInternetException(String mensaje)
    {
        super(mensaje);
        this.mensaje = mensaje;
    }


    public NoInternetException(Throwable causa)
    {
        super(causa);

    }

    @Override
    public String toString(){
        return mensaje;

    }

    @Override
    public String getMessage()
    {
        return mensaje;
    }


}

