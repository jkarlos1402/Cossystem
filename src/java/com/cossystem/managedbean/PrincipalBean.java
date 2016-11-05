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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JC
 */
@ManagedBean
@ViewScoped
public class PrincipalBean implements Serializable {

    private CatUsuarios usuarioSesion;   
    private List<TblAccesoPantallas> menuCompleto;

    /**
     * Creates a new instance of PrincipalBean
     */
    public PrincipalBean() {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        usuarioSesion = (CatUsuarios) httpSession.getAttribute("session_user");
        menuCompleto = (List<TblAccesoPantallas>) httpSession.getAttribute("accesoPantallas");
    }

    public CatUsuarios getUsuarioSesion() {
        return usuarioSesion;
    }

    public void setUsuarioSesion(CatUsuarios usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    public void resetFormulario(String nombreFormulario) {
        RequestContext.getCurrentInstance().reset(nombreFormulario);
    }    

    public List<TblAccesoPantallas> getMenuCompleto() {
        return menuCompleto;
    }

    public void setMenuCompleto(List<TblAccesoPantallas> menuCompleto) {
        this.menuCompleto = menuCompleto;
    }    
}
