/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.login;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empleado.TblEmpleadosDiarioActividad;
import com.cossystem.core.pojos.empresa.TblEmpresa;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author TMXIDSJPINAM
 */
@ManagedBean
@ViewScoped
public class LoginBean implements Serializable {

    private String user;
    private String password;
    private TblEmpresa empresa;

    private List<TblEmpresa> catEmpresas;

    public LoginBean() {
        GenericDAO genericDAO = null;
        try {
            genericDAO = new GenericDAO();
            catEmpresas = genericDAO.findAll(TblEmpresa.class);
        } catch (DataBaseException | DAOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public TblEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(TblEmpresa empresa) {
        this.empresa = empresa;
    }

    public List<TblEmpresa> getCatEmpresas() {
        return catEmpresas;
    }

    public void setCatEmpresas(List<TblEmpresa> catEmpresas) {
        this.catEmpresas = catEmpresas;
    }

    /**
     *
     * @return
     */
    public String logIn() {
        FacesContext context = FacesContext.getCurrentInstance();
        GenericDAO genericDAO = null;
        List<CatUsuarios> usuarios;
        CatUsuarios usuarioSesion;
        TreeMap mapaComponentes;
        Calendar fechaHoy = Calendar.getInstance();
        fechaHoy.set(Calendar.HOUR, 0);
        fechaHoy.set(Calendar.MINUTE, 0);
        fechaHoy.set(Calendar.SECOND, 0);
        fechaHoy.set(Calendar.MILLISECOND, 0);                       
        try {
            genericDAO = new GenericDAO();            
            mapaComponentes = new TreeMap<>();
            mapaComponentes.put("usuario", user);
            mapaComponentes.put("password", password);
            mapaComponentes.put("idEmpresa", empresa);
            usuarios = genericDAO.findByComponents(CatUsuarios.class, mapaComponentes);
            if (usuarios != null && !usuarios.isEmpty() && usuarios.get(0).getPassword().equals(password)) {
                HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                usuarioSesion = usuarios.get(0);
                mapaComponentes.clear();
                mapaComponentes.put("idEmpleado", usuarioSesion.getIdEmpleado());
                mapaComponentes.put("fecha", fechaHoy.getTime());
                mapaComponentes.put("idEmpresa", usuarioSesion.getIdEmpresa());
                List<TblEmpleadosDiarioActividad> actividadesHoy = genericDAO.findByComponents(TblEmpleadosDiarioActividad.class, mapaComponentes);
                TblEmpleadosDiarioActividad actividadHoy;
                if (actividadesHoy == null || actividadesHoy.isEmpty()) {
                    actividadHoy = new TblEmpleadosDiarioActividad();
                    actividadHoy.setIdEmpresa(usuarioSesion.getIdEmpresa());
                    actividadHoy.setIdEmpleado(usuarioSesion.getIdEmpleado());
                    actividadHoy.setFecha(fechaHoy.getTime());
                    actividadHoy.setActividadDescripcion("INGRESO AL SISTEMA ");
                    actividadHoy.setIdStatusAsistencia(1);
                    actividadHoy.setNota("");
                    genericDAO.saveOrUpdate(actividadHoy);
                    if (usuarioSesion.getIdEmpleado() != null) {
                        usuarioSesion.getIdEmpleado().setIdActividad(actividadHoy);
                    }
                }
                usuarioSesion.setIdconectado(1);
                usuarioSesion.setNumvisitas(usuarioSesion.getNumvisitas() + 1);
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                usuarioSesion.setNumIp(request.getRemoteAddr());
                usuarioSesion.setFechaInicial(new Date());
                genericDAO.saveOrUpdate(usuarioSesion);
                httpSession.setAttribute("session_user", usuarioSesion);                
//                httpSession.setAttribute("genericdao_user", new GenericDAO());                
                return "correct";
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: User or password incorrect", ""));
                return "Login";
            }
        } catch (DAOException | DataBaseException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
            return "Login";
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    /**
     *
     * @return
     */
    public String logout() {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        return "Login";
    }

}
