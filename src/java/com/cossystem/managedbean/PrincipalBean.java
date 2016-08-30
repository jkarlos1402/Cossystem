/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author JC
 */
@ManagedBean
@SessionScoped
public class PrincipalBean implements Serializable{

    private CatUsuarios usuarioSesion;

    /**
     * Creates a new instance of PrincipalBean
     */
    public PrincipalBean() {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        usuarioSesion = (CatUsuarios) httpSession.getAttribute("session_user");
    }

    public CatUsuarios getUsuarioSesion() {
        return usuarioSesion;
    }

    public void setUsuarioSesion(CatUsuarios usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

}
