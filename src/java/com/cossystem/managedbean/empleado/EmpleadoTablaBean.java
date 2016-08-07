package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatEmpStatus;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empleado.TblEmpleados;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.TreeMap;
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
public class EmpleadoTablaBean implements Serializable {

    //Todos los beans administrados deben contener los siguientes atributos
    private final GenericDAO genericDAO;
    private final CatUsuarios usuarioSesion;
    private String nombreDialogFrm = "";

    private TblEmpleados elementoNuevo;
    private TblEmpleados elementoSeleccionado;
    private List<TblEmpleados> elementoCatalogo;

    /**
     * Creates a new instance of AlertasBean
     */
    public EmpleadoTablaBean() {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        usuarioSesion = (CatUsuarios) httpSession.getAttribute("session_user");
        genericDAO = (GenericDAO) httpSession.getAttribute("genericdao_user");
        refreshElementoCatalogo();
    }

    public void openDialogFrmElemento(final String nombreDialog, final String tipoOperacion) {
        FacesMessage message = null;
        switch (tipoOperacion) {
            case "nuevo":
                elementoSeleccionado = null;
                elementoNuevo = new TblEmpleados();
                nombreDialogFrm = "Cat\u00E1logo de Empleados - Agregar registros";
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "editar":
                if (elementoSeleccionado != null) {
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atencion", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "ver":
                if (elementoSeleccionado != null) {
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atencion", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
        }
    }

    public void refreshElementoCatalogo() {
        FacesMessage message = null;
        TreeMap mapaComponentes = new TreeMap<>();
        try {
            mapaComponentes.put("idEmpresa", usuarioSesion.getIdEmpresa());
            elementoCatalogo = genericDAO.findByComponents(TblEmpleados.class, mapaComponentes);
        } catch (DAOException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
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
        if (elementoNuevo == null) {
            elementoNuevo = new TblEmpleados();
        }
        Field[] campos = elementoNuevo.getClass().getDeclaredFields();
        System.out.println("empleado: " + elementoNuevo);
        System.out.println("empleado seleccionada: " + elementoSeleccionado);
        for (Field campo : campos) {
            try {
                if ((Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL) != campo.getModifiers()) {
                    campo.setAccessible(true);
                    campo.set(elementoNuevo, campo.get(elementoSeleccionado));
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                System.out.println("error: " + ex.getMessage());
                //to do
            }
        }
        System.out.println("empleado1: " + elementoNuevo);
        System.out.println("empleado seleccionada1: " + elementoSeleccionado);
    }
//
//    public void cerroDialog() {
//        RequestContext.getCurrentInstance().reset("formOrderNotas:panelFormOrderNotas");
//    }
//

    public void eliminaElemento(final String nombreTabla) {
        FacesMessage message;
        if (elementoSeleccionado != null) {
            try {
                genericDAO.delete(elementoSeleccionado);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El elemento ha sido eliminado");
            } catch (DAOException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            } finally {
                refreshElementoCatalogo();
                RequestContext.getCurrentInstance().execute("PF('" + nombreTabla + "').filter()");
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00F3n", "Ning\u00FAn elemento seleccionado");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public TblEmpleados getElementoNuevo() {
        return elementoNuevo;
    }

    public void setElementoNuevo(TblEmpleados elementoNuevo) {
        this.elementoNuevo = elementoNuevo;
    }

    public TblEmpleados getElementoSeleccionado() {
        return elementoSeleccionado;
    }

    public void setElementoSeleccionado(TblEmpleados elementoSeleccionado) {
        this.elementoSeleccionado = elementoSeleccionado;
    }

    public List<TblEmpleados> getElementoCatalogo() {
        return elementoCatalogo;
    }

    public void setElementoCatalogo(List<TblEmpleados> elementoCatalogo) {
        this.elementoCatalogo = elementoCatalogo;
    }

    public String getNombreDialogFrm() {
        return nombreDialogFrm;
    }

    public void setNombreDialogFrm(String nombreDialogFrm) {
        this.nombreDialogFrm = nombreDialogFrm;
    }

}
