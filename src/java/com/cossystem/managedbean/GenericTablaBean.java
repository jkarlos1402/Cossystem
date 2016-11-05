package com.cossystem.managedbean;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.CossException;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.TblAccesoPantallas;
import com.cossystem.core.pojos.TblAccesoPantallasCampos;
import com.cossystem.core.util.Columnas;
import com.cossystem.core.util.Configuracion;
import com.cossystem.core.util.Filtro;
import com.cossystem.core.util.ManagerXLSX;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class GenericTablaBean implements Serializable {

    protected StreamedContent fileExcel;
    protected String nombreArchivo;

    protected List<TblAccesoPantallasCampos> configuracionPantalla;
    protected List<TblAccesoPantallasCampos> configuracionPantallaTransaccional;
    protected List<Filtro> filtros = null;
    protected List<Filtro> filtrosTransaccional = null;

    protected Integer idElementoSeleccionado;
    protected Object elementoSeleccionado;
    protected Class claseElemento;
    protected Class claseElementoDAO;
    protected Class claseElementoTransaccional;
    protected Class claseElementoTransaccionalDAO;
    protected Field campoIdElemento;
    protected Field campoIdElementoTransaccional;
    protected Integer idTablaEntidad = null;
    protected Integer idTablaEntidadTransaccional = null;
    protected String idMenu = null;
    protected TblAccesoPantallas menuOrigen = null;
    protected List<?> elementoCatalogo;
    protected List<Columnas> columnas;
    protected boolean permissionToWrite = true;
    protected boolean bndFiltrosDefault = true;
    protected String sentenciaWhere = "";
    protected String filtroGeneral = "";

    public GenericTablaBean() {

    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map parametros = externalContext.getRequestParameterMap();
        Properties propiedades;
        try {
            propiedades = Propiedades.obtienePropiedades();
            if (parametros.containsKey("idMenu")) {
                idMenu = (String) parametros.get("idMenu");
                menuOrigen = Configuracion.getMenu(Integer.parseInt(idMenu));
                idTablaEntidad = menuOrigen.getIdTablaVist();
                idTablaEntidadTransaccional = menuOrigen.getIdTableTran() != null ? menuOrigen.getIdTableTran() : idTablaEntidad;
                System.out.println("idTabla tran: "+idTablaEntidadTransaccional);
                configuracionPantalla = Configuracion.obtieneConfiguracion(idTablaEntidad);
                System.out.println("confPantalla: "+configuracionPantalla.size());
                configuracionPantallaTransaccional = !idTablaEntidad.equals(idTablaEntidadTransaccional) ? Configuracion.obtieneConfiguracion(idTablaEntidadTransaccional) : configuracionPantalla;
                System.out.println("confPantallaTran: "+configuracionPantallaTransaccional.size());
                if (configuracionPantalla != null && !configuracionPantalla.isEmpty()) {
                    String nombreClase = Configuracion.buscaNombreClasePojo(configuracionPantalla.get(0).getNTabla(), propiedades.getProperty("paquetePojos"));
                    Field[] camposElemento;
                    System.out.println("nombre clase: "+nombreClase);
                    if (nombreClase != null) {
                        try {
                            claseElemento = Class.forName(nombreClase);
                            camposElemento = claseElemento != null ? claseElemento.getDeclaredFields() : null;
                            if (camposElemento != null) {
                                for (Field campoElemento : camposElemento) {
                                    if (campoElemento.isAnnotationPresent(Id.class)) {
                                        campoIdElemento = campoElemento;
                                        break;
                                    }
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            claseElemento = null;
                        }
                        System.out.println("clase elemento: "+claseElemento.getName());
                        try {
                            if (propiedades.containsKey("paqueteDAO")) {
                                claseElementoDAO = Class.forName(propiedades.get("paqueteDAO") + "." + claseElemento.getSimpleName() + "DAO");
                            }
                        } catch (ClassNotFoundException ex) {
                            claseElementoDAO = null;
                        }
                    }
                }
                if (configuracionPantallaTransaccional != null && !configuracionPantallaTransaccional.isEmpty() && !configuracionPantallaTransaccional.equals(configuracionPantalla)) {
                    String nombreClase = Configuracion.buscaNombreClasePojo(configuracionPantallaTransaccional.get(0).getNTabla(), propiedades.getProperty("paquetePojos"));
                    if (nombreClase != null) {
                        try {
                            claseElementoTransaccional = Class.forName(nombreClase);
                        } catch (ClassNotFoundException ex) {
                            claseElementoTransaccional = null;
                        }
                        try {
                            if (propiedades.containsKey("paqueteDAO")) {
                                claseElementoTransaccionalDAO = Class.forName(propiedades.get("paqueteDAO") + "." + claseElementoTransaccional.getSimpleName() + "DAO");
                            }
                        } catch (ClassNotFoundException ex) {
                            claseElementoTransaccionalDAO = null;
                        }
                    }
                }else{
                    claseElementoTransaccional = claseElemento;
                }
                columnas = new ArrayList<>();
                if (configuracionPantalla != null) {
                    for (TblAccesoPantallasCampos config : configuracionPantalla) {
                        if (config.getVisibleGrid()) {
                            Field[] camposClase = claseElemento.getDeclaredFields();
                            for (Field campoClase : camposClase) {
                                if ((campoClase.isAnnotationPresent(Column.class) && ((Column) campoClase.getAnnotation(Column.class)).name().equals(config.getNColumna())) || (campoClase.isAnnotationPresent(JoinColumn.class) && ((JoinColumn) campoClase.getAnnotation(JoinColumn.class)).name().equals(config.getNColumna()))) {
                                    columnas.add(new Columnas(config.getDescripcion(), campoClase.getName()));
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (IOException | CossException ex) {
            //to do
        }
    }

    public void openDialogFrmElemento(final String nombreDialog, final String tipoOperacion, FrmGenericElementoBean frmBean, FiltroGenericBean filtroBean) {
        FacesMessage message = null;
        switch (tipoOperacion) {
            case "nuevo":
                nuevoElemento(frmBean);
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "editar":
                if (idElementoSeleccionado != null) {
                    frmBean.setIdElemento(idElementoSeleccionado);
                    frmBean.setPermissionToWrite(true);
                    frmBean.setClaseElemento(claseElementoTransaccional);
                    frmBean.setClaseElementoDAO(claseElementoTransaccionalDAO);
                    frmBean.setIdTablaElemento(idTablaEntidadTransaccional);
                    frmBean.setConfiguracionElemento(configuracionPantallaTransaccional);
                    frmBean.setMenuOrigen(menuOrigen);
                    frmBean.init();
                    frmBean.setNombreAccionFrm(menuOrigen.getTitulo() + " - Editar registro");
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "ver":
                if (idElementoSeleccionado != null) {
                    frmBean.setIdElemento(idElementoSeleccionado);
                    frmBean.setPermissionToWrite(false);
                    frmBean.setClaseElemento(claseElementoTransaccional);
                    frmBean.setClaseElementoDAO(claseElementoTransaccionalDAO);
                    frmBean.setIdTablaElemento(idTablaEntidadTransaccional);
                    frmBean.setConfiguracionElemento(configuracionPantallaTransaccional);
                    frmBean.setMenuOrigen(menuOrigen);
                    frmBean.init();
                    frmBean.setNombreAccionFrm(menuOrigen.getTitulo() + " - Ver registro");
                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
                }
                if (message != null) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                break;
            case "imprimir":
                frmBean.setNombreAccionFrm(menuOrigen.getTitulo() + " - Reporte");
                break;
            case "cargaExcel":
                frmBean.setNombreAccionFrm(menuOrigen.getTitulo() + " - Carga de excel");
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
            case "consulta":
                filtroBean.setIdClaseEntidad(idTablaEntidad);                
                filtroBean.setClaseEntidad(claseElemento);
                filtroBean.setClaseEntidadTransaccional(claseElementoTransaccional);
                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
                break;
        }
    }

    public void nuevoElemento(FrmGenericElementoBean frmBean) {
        elementoSeleccionado = null;
        idElementoSeleccionado = null;
        frmBean.setIdElemento(null);
        frmBean.setClaseElemento(claseElemento);
        frmBean.setClaseElementoDAO(claseElementoDAO);
        frmBean.setIdTablaElemento(idTablaEntidad);
        frmBean.setConfiguracionElemento(configuracionPantalla);
        frmBean.setPermissionToWrite(true);
        frmBean.setMenuOrigen(menuOrigen);
        frmBean.init();
        frmBean.setNombreAccionFrm(menuOrigen.getTitulo() + " - Agregar registro");
    }

    public void refreshElementoCatalogo(FrmGenericElementoBean frmBean, FiltroGenericBean filtroBean, boolean bndAddFiltroDefault) {
        FacesMessage message = null;
        GenericDAO genericDAO = null;
        Field[] camposClaseElemento = claseElemento.getDeclaredFields();
        Field[] camposClaseElementoTransaccional = claseElementoTransaccional.getDeclaredFields();
        try {
            if (claseElementoDAO != null) {
                genericDAO = (GenericDAO) claseElementoDAO.newInstance();
            } else {
                genericDAO = new GenericDAO();
            }
            filtros = filtroBean != null ? filtroBean.getFiltros() : null;
            filtrosTransaccional = filtroBean != null ? filtroBean.getFiltrosTransaccional() : null;
            if (filtros == null) {
                filtros = new ArrayList<>();
            }
            if (filtrosTransaccional == null) {
                filtrosTransaccional = new ArrayList<>();
            }
            Filtro filtroTemp;
            if (menuOrigen.getFiltro() != null) {
                if (bndFiltrosDefault && bndAddFiltroDefault) {
                    String[] filtrosDefault = menuOrigen.getFiltro().split(",");
                    String[] componentesFiltro;
                    String nombreCampo;
                    for (String filtroDefault : filtrosDefault) {
                        componentesFiltro = filtroDefault.split("=");
                        if (componentesFiltro.length == 2) {
                            for (Field campo : camposClaseElemento) {
                                if (campo.isAnnotationPresent(Column.class)) {
                                    nombreCampo = ((Column) campo.getAnnotation(Column.class)).name();
                                } else if (campo.isAnnotationPresent(JoinColumn.class)) {
                                    nombreCampo = ((JoinColumn) campo.getAnnotation(JoinColumn.class)).name();
                                } else {
                                    nombreCampo = null;
                                }
                                if (nombreCampo != null && componentesFiltro[0].trim().equalsIgnoreCase(nombreCampo)) {
                                    TblAccesoPantallasCampos confCampo = null;
                                    for (TblAccesoPantallasCampos conf : configuracionPantalla) {
                                        if (conf.getNColumna().equalsIgnoreCase(nombreCampo)) {
                                            confCampo = conf;
                                            break;
                                        }
                                    }
                                    filtroTemp = Filtro.generaFiltro(campo, componentesFiltro[1], confCampo);
                                    filtroTemp.setVisible(false);
                                    filtros.add(filtroTemp);
                                    break;
                                }
                            }
                            for (Field campo : camposClaseElementoTransaccional) {
                                if (campo.isAnnotationPresent(Column.class)) {
                                    nombreCampo = ((Column) campo.getAnnotation(Column.class)).name();
                                } else if (campo.isAnnotationPresent(JoinColumn.class)) {
                                    nombreCampo = ((JoinColumn) campo.getAnnotation(JoinColumn.class)).name();
                                } else {
                                    nombreCampo = null;
                                }
                                if (nombreCampo != null && componentesFiltro[0].trim().equalsIgnoreCase(nombreCampo)) {
                                    TblAccesoPantallasCampos confCampo = null;
                                    for (TblAccesoPantallasCampos conf : configuracionPantallaTransaccional) {
                                        if (conf.getNColumna().equalsIgnoreCase(nombreCampo)) {
                                            confCampo = conf;
                                            break;
                                        }
                                    }
                                    filtroTemp = Filtro.generaFiltro(campo, componentesFiltro[1], confCampo);
                                    filtroTemp.setVisible(false);
                                    filtrosTransaccional.add(filtroTemp);
                                    break;
                                }
                            }
                        }
                    }
                    bndFiltrosDefault = false;
                }
            }
            if (filtroBean != null && bndAddFiltroDefault) {
                filtroBean.setFiltros(filtros);
                filtroBean.setFiltrosTransaccional(filtrosTransaccional);
                boolean bndAdd = true;
                for (Filtro fitroTmp : filtroBean.getFiltros()) {
                    if (fitroTmp.isVisible()) {
                        bndAdd = false;
                        break;
                    }
                }
                if (bndAdd) {
                    filtroBean.getFiltros().add(new Filtro());
                    filtroBean.getFiltrosTransaccional().add(new Filtro());
                }
            }
            if (filtrosTransaccional != null) {
                sentenciaWhere = Filtro.generaClausulaWhere(filtrosTransaccional);
                System.out.println("sentencia where: " + sentenciaWhere);
            }
            elementoCatalogo = genericDAO.findByComponents(claseElemento, filtroBean != null ? filtroBean.getFiltros() : null);
        } catch (DAOException | DataBaseException | IllegalAccessException | ParseException | InstantiationException ex) {
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
        Field[] campos = claseElemento.getDeclaredFields();
        Method[] metodos = claseElemento.getMethods();
        for (Field campo : campos) {
            try {
                if (campo.isAnnotationPresent(Id.class)) {
                    for (Method metodo : metodos) {
                        if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
                            idElementoSeleccionado = (Integer) metodo.invoke(elementoSeleccionado);
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

    public void cerroDialogElemento(String nombreFormulario, FrmGenericElementoBean frmBean) {
        RequestContext.getCurrentInstance().reset(nombreFormulario);
        frmBean.cerrarConexion();
    }

    public void eliminaElemento(final String nombreTabla, FrmGenericElementoBean frmBean, FiltroGenericBean filtroBean) {
        FacesMessage message;
        GenericDAO genericDAO = null;
        Properties propiedades;
        if (elementoSeleccionado != null) {
            try {
                Class claseDAO;
                try {
                    propiedades = Propiedades.obtienePropiedades();
                    if (propiedades.containsKey("paqueteDAO")) {
                        claseDAO = Class.forName(propiedades.getProperty("paqueteDAO") + "." + claseElemento.getSimpleName() + "DAO");
                    } else {
                        claseDAO = null;
                    }
                } catch (ClassNotFoundException | IOException ex) {
                    claseDAO = null;
                }
                if (claseDAO != null) {
                    genericDAO = (GenericDAO) claseDAO.newInstance();
                } else {
                    genericDAO = new GenericDAO();
                }
                campoIdElemento.setAccessible(true);
                Integer idEliminado = (Integer) campoIdElemento.get(elementoSeleccionado);
                genericDAO.deleteById(claseElemento, idEliminado);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El elemento ha sido eliminado");
            } catch (DAOException | DataBaseException | InstantiationException | IllegalAccessException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            } finally {
                if (genericDAO != null) {
                    genericDAO.closeDAO();
                }
                refreshElementoCatalogo(frmBean, filtroBean, true);
                RequestContext.getCurrentInstance().execute("PF('" + nombreTabla + "').filter()");
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00F3n", "Ning\u00FAn elemento seleccionado");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String generaExcelElemento(FiltroGenericBean filtroBean) throws DAOException, DataBaseException, CossException, IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        String contextPathResources = servletContext.getRealPath("");
        Properties propiedades = Propiedades.obtienePropiedades();
        return contextPathResources + File.separator + "tempExcel" + File.separator + ManagerXLSX.generaArchivoExcel(claseElementoTransaccional, claseElementoTransaccionalDAO, filtroBean != null ? filtroBean.getFiltrosTransaccional() : filtrosTransaccional, contextPathResources + File.separator + "tempExcel", menuOrigen, propiedades);
    }

    public Integer getIdElementoSeleccionado() {
        return idElementoSeleccionado;
    }

    public void setIdElementoSeleccionado(Integer idElementoSeleccionado) {
        this.idElementoSeleccionado = idElementoSeleccionado;
    }

    public Object getElementoSeleccionado() {
        return elementoSeleccionado;
    }

    public void setElementoSeleccionado(Object elementoSeleccionado) {
        this.elementoSeleccionado = elementoSeleccionado;
    }

    public List<?> getElementoCatalogo() {
        return elementoCatalogo;
    }

    public StreamedContent getFileExcel(FiltroGenericBean filtroBean) {
        FacesMessage message = null;
        String nombreArchivoExcel;
        InputStream stream;
        try {
            nombreArchivoExcel = generaExcelElemento(filtroBean);
            stream = new FileInputStream(nombreArchivoExcel);
            Calendar fechaHoy = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            nombreArchivo = nombreArchivoExcel;
            fileExcel = new DefaultStreamedContent(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", menuOrigen.getDescripcion() + sdf.format(fechaHoy.getTime()) + ".xlsx");
        } catch (IOException | DAOException | DataBaseException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al descargar archivo", ex.getMessage());
        } catch (CossException ex) {
            Logger.getLogger(GenericTablaBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public void handleFileExcel(FileUploadEvent event, FrmGenericElementoBean frmBean, FiltroGenericBean filtroBean) {
        FacesMessage message;
        FacesContext context = FacesContext.getCurrentInstance();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File archivoTemp = null;
        try {
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String contextPathResources = servletContext.getRealPath("");
            File dirTemp = new File(contextPathResources + File.separator + "tempCargaExcel");
            Properties propiedades = Propiedades.obtienePropiedades();
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
            if (propiedades.containsKey("paqueteDAO")) {
                ManagerXLSX.cargaCatalogoExcel(claseElementoTransaccional, rutaArchivoTemp, (String) propiedades.get("paqueteDAO"), Integer.parseInt(propiedades.getProperty("rowInicioExcel")));
            } else {
                ManagerXLSX.cargaCatalogoExcel(claseElementoTransaccional, rutaArchivoTemp, "noHayPaqueteDao", Integer.parseInt(propiedades.getProperty("rowInicioExcel")));
            }
            refreshElementoCatalogo(frmBean, filtroBean, true);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "La carga del archivo excel se realiz\u00f3 correctamente");
        } catch (IOException | CossException | IllegalArgumentException ex) {
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

    public void todosLosRegistros(FrmGenericElementoBean frmBean, FiltroGenericBean filtroBean) {
        List<Filtro> filtrosAEliminar = new ArrayList<>();
        for (Filtro filtro : filtroBean.getFiltros()) {
            if (filtro.isEliminable()) {
                filtrosAEliminar.add(filtro);
            }
        }
        for (Filtro filtro : filtrosAEliminar) {
            filtroBean.getFiltros().remove(filtro);
        }
        if (filtroBean.getFiltros().isEmpty()) {
            filtroBean.getFiltros().add(new Filtro());
        }
        filtrosAEliminar.clear();
        for (Filtro filtro : filtroBean.getFiltrosTransaccional()) {
            if (filtro.isEliminable()) {
                filtrosAEliminar.add(filtro);
            }
        }
        for (Filtro filtro : filtrosAEliminar) {
            filtroBean.getFiltrosTransaccional().remove(filtro);
        }
        if (filtroBean.getFiltrosTransaccional().isEmpty()) {
            filtroBean.getFiltrosTransaccional().add(new Filtro());
        }
        refreshElementoCatalogo(frmBean, filtroBean, false);
    }

    public TblAccesoPantallas getMenuOrigen() {
        return menuOrigen;
    }

    public void setMenuOrigen(TblAccesoPantallas menuOrigen) {
        this.menuOrigen = menuOrigen;
    }

    public List<TblAccesoPantallasCampos> getConfiguracionPantalla() {
        return configuracionPantalla;
    }

    public void setConfiguracionPantalla(List<TblAccesoPantallasCampos> configuracionPantalla) {
        this.configuracionPantalla = configuracionPantalla;
    }

    public Field getCampoIdElemento() {
        return campoIdElemento;
    }

    public void setCampoIdElemento(Field campoIdElemento) {
        this.campoIdElemento = campoIdElemento;
    }

    public List<Columnas> getColumnas() {
        return columnas;
    }

    public void setColumnas(List<Columnas> columnas) {
        this.columnas = columnas;
    }

    public boolean isPermissionToWrite() {
        return permissionToWrite;
    }

    public void setPermissionToWrite(boolean permissionToWrite) {
        this.permissionToWrite = permissionToWrite;
    }

    public String getSentenciaWhere() {
        return sentenciaWhere;
    }

    public void setSentenciaWhere(String sentenciaWhere) {
        this.sentenciaWhere = sentenciaWhere;
    }

    public String getFiltroGeneral() {
        return filtroGeneral;
    }

    public void setFiltroGeneral(String filtroGeneral) {
        this.filtroGeneral = filtroGeneral;
    }

}
