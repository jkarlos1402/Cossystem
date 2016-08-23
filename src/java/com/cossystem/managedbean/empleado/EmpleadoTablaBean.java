package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empleado.TblEmpleados;
import com.cossystem.managedbean.PrincipalBean;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author TMXIDSJPINAM
 */
@ManagedBean
@ViewScoped
public class EmpleadoTablaBean implements Serializable {

    //Todos los beans administrados de tabla deben contener los siguientes atributos
    private CatUsuarios usuarioSesion;
    private String nombreDialogFrm = "";

    private TblEmpleados elementoNuevo = new TblEmpleados();
    private TblEmpleados elementoSeleccionado;
    private List<TblEmpleados> elementoCatalogo;

    @ManagedProperty(value = "#{principalBean}")
    private PrincipalBean principalBean;

    @ManagedProperty(value = "#{empleadoFrmBean}")
    private EmpleadoFrmBean empleadoFrmBean;

    /**
     * Creates a new instance of AlertasBean
     */
    public EmpleadoTablaBean() {

    }

    @PostConstruct
    public void init() {
        usuarioSesion = principalBean.getUsuarioSesion();
        refreshElementoCatalogo();
    }

    public void openDialogFrmElemento(final String nombreDialog, final String tipoOperacion) {
        FacesMessage message = null;
        switch (tipoOperacion) {
            case "nuevo":
                elementoSeleccionado = null;
                elementoNuevo = new TblEmpleados();
                empleadoFrmBean.setEmpleado(elementoNuevo);
                empleadoFrmBean.setPermissionToWrite(true);
                empleadoFrmBean.init();
                nombreDialogFrm = "Cat\u00E1logo de Empleados - Agregar registro";
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "editar":
                if (elementoSeleccionado != null) {
                    empleadoFrmBean.setEmpleado(elementoSeleccionado);
                    empleadoFrmBean.setPermissionToWrite(true);
                    empleadoFrmBean.init();
                    nombreDialogFrm = "Cat\u00E1logo de Empleados - Editar registro";
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "ver":
                if (elementoSeleccionado != null) {
                    empleadoFrmBean.setEmpleado(elementoSeleccionado);
                    empleadoFrmBean.setPermissionToWrite(false);
                    empleadoFrmBean.init();
                    nombreDialogFrm = "Cat\u00E1logo de Empleados - Ver registro";
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "imprimir":
                nombreDialogFrm = "Cat\u00E1logo de Empleados - Reporte";
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
        }
    }

    public void refreshElementoCatalogo() {
        FacesMessage message = null;
        TreeMap mapaComponentes = new TreeMap<>();
        GenericDAO genericDAO = null;
        try {
            genericDAO = new GenericDAO();
            mapaComponentes.put("idEmpresa", usuarioSesion.getIdEmpresa());
            elementoCatalogo = genericDAO.findByComponents(TblEmpleados.class, mapaComponentes);
        } catch (DAOException | DataBaseException ex) {
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
        FacesMessage message = null;
        if (elementoNuevo == null) {
            elementoNuevo = new TblEmpleados();
        }
        Field[] campos = elementoNuevo.getClass().getDeclaredFields();
        Method[] metodos = elementoNuevo.getClass().getMethods();
        for (Field campo : campos) {
            try {
                if ((Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL) != campo.getModifiers()) {
                    campo.setAccessible(true);
                    for (Method metodo : metodos) {
                        if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
                            campo.set(elementoNuevo, metodo.invoke(elementoSeleccionado));
                            break;
                        }
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            }
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void cerroDialogElemento() {
        System.out.println("se limpiara");
        RequestContext.getCurrentInstance().reset("formFrmEmpleado");
    }

    public void eliminaElemento(final String nombreTabla) {
        FacesMessage message;
        GenericDAO genericDAO = null;
        if (elementoSeleccionado != null) {
            try {
                genericDAO = new GenericDAO();
                genericDAO.delete(elementoSeleccionado);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El elemento ha sido eliminado");
            } catch (DAOException | DataBaseException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            } finally {
                if (genericDAO != null) {
                    genericDAO.closeDAO();
                }
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

    public CatUsuarios getUsuarioSesion() {
        return usuarioSesion;
    }

    public PrincipalBean getPrincipalBean() {
        return principalBean;
    }

    public void setPrincipalBean(PrincipalBean principalBean) {
        this.principalBean = principalBean;
    }

    public EmpleadoFrmBean getEmpleadoFrmBean() {
        return empleadoFrmBean;
    }

    public void setEmpleadoFrmBean(EmpleadoFrmBean empleadoFrmBean) {
        this.empleadoFrmBean = empleadoFrmBean;
    }

}
