/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean;

import com.cossystem.core.pojos.TblAccesoPantallas;
import com.cossystem.core.pojos.CatUsuarios;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JC
 */
@ManagedBean
@ViewScoped
public class RutaPaginaBean implements Serializable {

    private String rutaPagina = "/blank.xhtml";

    /**
     * Creates a new instance of PrincipalBean
     */
    public RutaPaginaBean() {
    }

    public String getRutaPagina() {
        return rutaPagina;
    }

    public void setRutaPagina(String rutaPagina) {
        this.rutaPagina = rutaPagina;
    }

    public void cambiaContenido(ActionEvent actionEvent) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> parametros = context.getExternalContext().getRequestParameterMap();
        for (String param : parametros.keySet()) {
            System.out.println("parametro: " + param);
        }
        String rutaPaginaTemp = null;
        if(parametros.containsKey("nombreRuta")){
            rutaPaginaTemp = parametros.get("nombreRuta");
        }
        System.out.println("cambia contenido");
        if (rutaPaginaTemp != null && !"".equals(rutaPaginaTemp.trim())) {
            String pathInicial = FacesContext.getCurrentInstance().getExternalContext().getRealPath("");
            File archivoPagina = new File(pathInicial + File.separator + rutaPaginaTemp);
            if (archivoPagina.isFile()) {
                this.setRutaPagina(rutaPaginaTemp);
            } else {
                this.setRutaPagina("/blank.xhtml");
            }
        } else {
            this.setRutaPagina("/blank.xhtml");
        }
        System.out.println(this.getRutaPagina());
    }
}
