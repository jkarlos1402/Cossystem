package com.cossystem.managedbean.empleado;

import com.cossystem.managedbean.FiltroGenericBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class FiltroEmpleadoBean extends FiltroGenericBean implements Serializable{

    public FiltroEmpleadoBean() {
        super();
    }
    
}
