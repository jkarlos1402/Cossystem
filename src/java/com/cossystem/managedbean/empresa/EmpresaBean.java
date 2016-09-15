/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.empresa;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatEmpStatus;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empresa.TblEmpresa;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author TMXIDSJPINAM
 */
@ManagedBean
@ViewScoped
public class EmpresaBean implements Serializable {

    private TblEmpresa empresaNueva;
    private TblEmpresa empresaSeleccionada;
    private List<TblEmpresa> empresaCatalogo;
    private List<CatEmpStatus> empresaEstatusCatalogo;

    /**
     * Creates a new instance of AlertasBean
     */
    public EmpresaBean() {
        refreshEmpresaCatalogo();
    }

//    public void openDialogNota(String nombreDialog, int tipoOperacion) {
//        FacesMessage message = null;
//        switch (tipoOperacion) {
//            case 1://editar
//                if (notaSeleccionada != null) {
//                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                } else {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sorry", "Not element selected");
//                }
//                if (message != null) {
//                    FacesContext.getCurrentInstance().addMessage(null, message);
//                }
//                break;
//            case 2://nuevo
//                notaSeleccionada = null;
//                paisSelected = null;
//                notaNueva = new OffNotas();
//                break;
//            case 3://ver
//                if (notaSeleccionada != null) {
//                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                } else {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sorry", "Not element selected");
//                }
//                if (message != null) {
//                    FacesContext.getCurrentInstance().addMessage(null, message);
//                }
//                break;
//        }
//    }
//
    public void refreshEmpresaCatalogo() {        
        FacesMessage message = null;
        GenericDAO genericDAO = null;
        TreeMap mapaComponentes = new TreeMap<>();
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        CatUsuarios usuario = (CatUsuarios) httpSession.getAttribute("session_user");
        try {
            genericDAO = new GenericDAO();
            mapaComponentes.put("idEmpresa", usuario.getIdEmpresa());
            empresaEstatusCatalogo = genericDAO.findByComponents(CatEmpStatus.class, mapaComponentes);
            mapaComponentes.clear();
            empresaCatalogo = genericDAO.findAll(TblEmpresa.class);
        } catch (DataBaseException | DAOException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
//
//    public Serializable copiaNotaNuevaANotaSeleccionada() {
//        Field[] campos = notaNueva.getClass().getDeclaredFields();
//        for (Field campo : campos) {
//            try {
//                if ((Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL) != campo.getModifiers()) {
//                    campo.setAccessible(true);
//                    campo.set(notaSeleccionada, campo.get(notaNueva));
//                }
//            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(NotasBean.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(NotasBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return notaSeleccionada;
//    }
//
//    public void guardaNota() {
//        FacesMessage message;
//        GenericDAO genericDAO = null;
//        try {
//            genericDAO = new GenericDAO();
//            notaNueva.setPais(paisSelected.getClave());
//            genericDAO.saveOrUpdate(notaSeleccionada != null ? copiaNotaNuevaANotaSeleccionada() : notaNueva);
//            RequestContext.getCurrentInstance().execute("PF('dialogFormOrderNotas').hide()");
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful", "The record has been saved");
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(NotasBean.class.getName()).log(Level.SEVERE, null, ex);
//            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//        } catch (DataBaseException ex) {
//            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//            Logger.getLogger(NotasBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (DAOException ex) {
//            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//            Logger.getLogger(NotasBean.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            refreshNotas();
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
//        FacesContext.getCurrentInstance().addMessage(null, message);
//    }
//

    public void selectElemento() {
        if (empresaNueva == null) {
            empresaNueva = new TblEmpresa();
        }
        Field[] campos = empresaNueva.getClass().getDeclaredFields();        
        for (Field campo : campos) {
            try {
                if ((Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL) != campo.getModifiers()) {
                    campo.setAccessible(true);
                    campo.set(empresaNueva, campo.get(empresaSeleccionada));
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                //to do
            }
        }        
    }

    public TblEmpresa getEmpresaNueva() {
        return empresaNueva;
    }

    public void setEmpresaNueva(TblEmpresa empresaNueva) {
        this.empresaNueva = empresaNueva;
    }

    public TblEmpresa getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    public void setEmpresaSeleccionada(TblEmpresa empresaSeleccionada) {
        this.empresaSeleccionada = empresaSeleccionada;
    }

    public List<TblEmpresa> getEmpresaCatalogo() {
        return empresaCatalogo;
    }

    public void setEmpresaCatalogo(List<TblEmpresa> empresaCatalogo) {
        this.empresaCatalogo = empresaCatalogo;
    }

    public List<CatEmpStatus> getEmpresaEstatusCatalogo() {
        return empresaEstatusCatalogo;
    }

    public void setEmpresaEstatusCatalogo(List<CatEmpStatus> empresaEstatusCatalogo) {
        this.empresaEstatusCatalogo = empresaEstatusCatalogo;
    }
}
