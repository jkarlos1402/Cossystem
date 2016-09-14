package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.dao.catalogo.TblEmpleadosDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empleado.TblEmpleados;
import com.cossystem.core.util.ManagerXLSX;
import com.cossystem.managedbean.PrincipalBean;
import com.cossystem.managedbean.filtro.FiltroEntidad;
import com.cossystem.util.Propiedades;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author TMXIDSJPINAM
 */
@ManagedBean
@ViewScoped
public class EmpleadoTablaBean implements Serializable {

    //Todos los beans administrados de tabla deben contener los siguientes atributos
    private CatUsuarios usuarioSesion;
    private String rutaReporte = "blank.xhtml";
    private StreamedContent fileExcel;
    private String nombreArchivo;

    private TblEmpleados elementoNuevo = new TblEmpleados();
    private TblEmpleados elementoSeleccionado;
    private List<TblEmpleados> elementoCatalogo;

    @ManagedProperty(value = "#{principalBean}")
    private PrincipalBean principalBean;

    @ManagedProperty(value = "#{empleadoFrmBean}")
    private EmpleadoFrmBean empleadoFrmBean;

    @ManagedProperty(value = "#{filtroEntidadTblEmpleados}")
    private FiltroEntidad filtroEntidad;

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
                nuevoElemento();
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "editar":
                if (elementoSeleccionado != null) {
                    empleadoFrmBean.setEmpleado(elementoSeleccionado);
                    empleadoFrmBean.setPermissionToWrite(true);
                    empleadoFrmBean.init();
                    empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Editar registro");
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
                    empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Ver registro");
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "imprimir":
                empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Reporte");
                //RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "cargaExcel":
                empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Carga de excel");
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "consulta":
                //empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Consulta");
                filtroEntidad.setClaseEntidad(TblEmpleados.class);
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
        }
    }

    public void nuevoElemento() {
        elementoSeleccionado = null;
        elementoNuevo = new TblEmpleados();
        empleadoFrmBean.setEmpleado(elementoNuevo);
        empleadoFrmBean.setPermissionToWrite(true);
        empleadoFrmBean.init();
        empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Agregar registro");
    }

    public void refreshElementoCatalogo() {
        FacesMessage message = null;
        TreeMap mapaComponentes = new TreeMap<>();
        GenericDAO genericDAO = null;
        try {
            genericDAO = new TblEmpleadosDAO();
            if (filtroEntidad != null && filtroEntidad.getFiltros() != null && !filtroEntidad.getFiltros().isEmpty() && filtroEntidad.getFiltros().get(0).getComparador() != null) {
                elementoCatalogo = genericDAO.findByComponents(TblEmpleados.class, filtroEntidad.getFiltros());
            } else {
                mapaComponentes.put("idEmpresa", usuarioSesion.getIdEmpresa());
                elementoCatalogo = genericDAO.findByComponents(TblEmpleados.class, mapaComponentes);
            }
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

    public void cerroDialogElemento(String nombreFormulario) {
        RequestContext.getCurrentInstance().reset(nombreFormulario);
    }

    public void eliminaElemento(final String nombreTabla) {
        FacesMessage message;
        GenericDAO genericDAO = null;
        Properties properties;
        if (elementoSeleccionado != null) {
            try {
                properties = Propiedades.obtienePropiedades();
                genericDAO = new TblEmpleadosDAO();
                Integer idEliminado = elementoSeleccionado.getIdEmpleado();
                genericDAO.delete(elementoSeleccionado);
                File dirFotos = new File(properties.getProperty("fotosPath") + File.separator + idEliminado);
                if(dirFotos.isDirectory()){
                    dirFotos.delete();
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El elemento ha sido eliminado");
            } catch (DAOException | DataBaseException | IOException ex) {
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

    public String generaExcelElemento() throws DAOException, DataBaseException {
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        String contextPathResources = servletContext.getRealPath("");
        Map filtros = new TreeMap();
        filtros.put("idEmpresa", usuarioSesion.getIdEmpresa());
        return contextPathResources + File.separator + "tempExcel" + File.separator + ManagerXLSX.generaArchivoExcel(TblEmpleados.class, filtros, contextPathResources + File.separator + "tempExcel");
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

    public String getRutaReporte() {
        return rutaReporte;
    }

    public void setRutaReporte(String rutaReporte) {
        this.rutaReporte = rutaReporte;
    }

    public StreamedContent getFileExcel() {
        FacesMessage message = null;
        String nombreArchivoExcel = null;
        InputStream stream = null;
        try {
            nombreArchivoExcel = generaExcelElemento();
            stream = new FileInputStream(nombreArchivoExcel);
            Calendar fechaHoy = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            nombreArchivo = nombreArchivoExcel;
            fileExcel = new DefaultStreamedContent(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "catalogoEmpleados" + sdf.format(fechaHoy.getTime()) + ".xlsx");
        } catch (IOException | DAOException | DataBaseException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al descargar archivo", ex.getMessage());
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return fileExcel;
    }

    public void setFileExcel(StreamedContent fileExcel) {
        this.fileExcel = fileExcel;
    }

    public void borraTempExcel() {
        File file = new File(nombreArchivo);
        if (file.isFile()) {
            file.delete();
        }
    }

    public void handleFileExcel(FileUploadEvent event) {
        FacesMessage message;
        FacesContext context = FacesContext.getCurrentInstance();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File archivoTemp = null;
        try {
            UploadedFile file = event.getFile();
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String contextPathResources = servletContext.getRealPath("");
            File dirTemp = new File(contextPathResources + File.separator + "tempCargaExcel");
            if (!dirTemp.isDirectory()) {
                dirTemp.mkdirs();
            }
            String rutaArchivoTemp = contextPathResources + File.separator + "tempCargaExcel" + File.separator + "tempCarga" + Calendar.getInstance().getTimeInMillis() + ".xlsx";
            archivoTemp = new File(rutaArchivoTemp);
            outputStream = new FileOutputStream(archivoTemp);
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            ManagerXLSX.cargaCatalogoExcel(TblEmpleados.class, rutaArchivoTemp);
            refreshElementoCatalogo();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "La carga del archivo excel se realiz\u00f3 correctamente");
        } catch (Exception ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + ex.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + e.getMessage());
                }

            }
            if (archivoTemp != null) {
                archivoTemp.delete();
            }
        }
        context.addMessage(null, message);
    }

    public FiltroEntidad getFiltroEntidad() {
        return filtroEntidad;
    }

    public void setFiltroEntidad(FiltroEntidad filtroEntidad) {
        this.filtroEntidad = filtroEntidad;
    }

    public void todosLosRegistros() {
        filtroEntidad.init();
        refreshElementoCatalogo();
    }
}
