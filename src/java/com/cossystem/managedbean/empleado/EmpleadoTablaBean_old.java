package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.CossException;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.TblAccesoPantallas;
import com.cossystem.core.pojos.TblAccesoPantallasCampos;
import com.cossystem.core.pojos.CatUsuarios;
import com.cossystem.core.util.Configuracion;
import com.cossystem.core.util.Filtro;
import com.cossystem.core.util.ManagerXLSX;
import com.cossystem.managedbean.PrincipalBean;
import com.cossystem.managedbean.filtro.FiltroEntidad;
import com.cossystem.core.util.Columnas;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
import org.primefaces.model.UploadedFile;

//@ManagedBean
//@ViewScoped
public class EmpleadoTablaBean_old implements Serializable {

//    private CatUsuarios usuarioSesion;
//    private StreamedContent fileExcel;
//    private String nombreArchivo;
//    private int idMenu;
//    private TblAccesoPantallas menuOrigen;
//    private List<TblAccesoPantallasCampos> configuracionPantalla;
//    private List<TblAccesoPantallasCampos> configuracionPantallaTransaccional;
//    private List<Filtro> filtros = null;
//
//    private Integer idElementoSeleccionado;
//    private Object elementoSeleccionado;
//    private Class claseElemento;
//    private Field campoIdElemento;
//    private Class claseElementoTransaccional;
//    private Field campoIdElementoTransaccional;
//    private List<?> elementoCatalogo;
//    private List<Columnas> columnas;
//
//    @ManagedProperty(value = "#{principalBean}")
//    private PrincipalBean principalBean;
//
//    @ManagedProperty(value = "#{empleadoFrmBean}")
//    private EmpleadoFrmBean empleadoFrmBean;
//
//    @ManagedProperty(value = "#{filtroEntidadTblEmpleados}")
//    private FiltroEntidad filtroEntidad;
//
//    /**
//     * Creates a new instance of AlertasBean
//     */
//    public EmpleadoTablaBean_old() {
//
//    }
//
//    @PostConstruct
//    public void init() {
//        usuarioSesion = principalBean.getUsuarioSesion();
//        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//        Map<String, String> parametros = externalContext.getRequestParameterMap();
//        for (String param : parametros.keySet()) {
//            System.out.println("parametro: " + param + " - " + parametros.get(param));
//        }
//        if (parametros.containsKey("idMenu")) {
//            idMenu = Integer.parseInt((String) parametros.get("idMenu"));
//        }
//        List<TblAccesoPantallas> menu = principalBean.getMenuCompleto();
//        TblAccesoPantallas menuTemp = new TblAccesoPantallas();
//        menuTemp.setIdMenu(idMenu);
//        System.out.println("idMenu: " + idMenu);
//        menuOrigen = obtieneMenu(menuTemp, menu);
//        System.out.println("menu origen: " + menuOrigen);
//        configuracionPantalla = Configuracion.obtieneConfiguracion(menuOrigen.getIdTablaVist());
//        if (menuOrigen.getIdTablaVist().equals(menuOrigen.getIdTableTran())) {
//            configuracionPantallaTransaccional = configuracionPantalla;
//        } else {
//            configuracionPantallaTransaccional = Configuracion.obtieneConfiguracion(menuOrigen.getIdTableTran());
//        }
//        if (configuracionPantalla != null && !configuracionPantalla.isEmpty()) {
//            String[] nombreTablaArray = configuracionPantalla.get(0).getNTabla().split("_");
//            String nombreClase = "";
//            for (String nombreTablaParcial : nombreTablaArray) {
//                nombreClase += nombreTablaParcial.replaceFirst((nombreTablaParcial.charAt(0) + ""), (nombreTablaParcial.charAt(0) + "").toUpperCase());
//            }
//            Field[] camposElemento;
//            if (!"".equals(nombreClase)) {
//                try {
//                    claseElemento = Class.forName("com.cossystem.core.pojos." + nombreClase);
//                    camposElemento = claseElemento.getDeclaredFields();
//                    for (Field campoElemento : camposElemento) {
//                        if (campoElemento.isAnnotationPresent(Id.class)) {
//                            campoIdElemento = campoElemento;
//                            break;
//                        }
//                    }
//                } catch (ClassNotFoundException ex) {
//                    claseElemento = null;
//                }
//            }
//            nombreTablaArray = configuracionPantallaTransaccional.get(0).getNTabla().split("_");
//            nombreClase = "";
//            for (String nombreTablaParcial : nombreTablaArray) {
//                nombreClase += nombreTablaParcial.replaceFirst((nombreTablaParcial.charAt(0) + ""), (nombreTablaParcial.charAt(0) + "").toUpperCase());
//            }
//            if (!"".equals(nombreClase)) {
//                try {
//                    claseElementoTransaccional = Class.forName("com.cossystem.core.pojos." + nombreClase);
//                    camposElemento = claseElementoTransaccional.getDeclaredFields();
//                    for (Field campoElemento : camposElemento) {
//                        if (campoElemento.isAnnotationPresent(Id.class)) {
//                            campoIdElementoTransaccional = campoElemento;
//                            break;
//                        }
//                    }
//                } catch (ClassNotFoundException ex) {
//                    claseElementoTransaccional = null;
//                }
//            }
//
//            System.out.println("claseElemento ");
//            System.out.println(claseElemento != null ? claseElemento.getSimpleName() : claseElemento);
//        }
//        columnas = new ArrayList<>();
//        if (configuracionPantalla != null) {
//            for (TblAccesoPantallasCampos config : configuracionPantalla) {
//                if (config.getVisibleGrid()) {
//                    Field[] camposClase = claseElemento.getDeclaredFields();
//                    for (Field campoClase : camposClase) {
//                        if ((campoClase.isAnnotationPresent(Column.class) && ((Column) campoClase.getAnnotation(Column.class)).name().equals(config.getNColumna())) || (campoClase.isAnnotationPresent(JoinColumn.class) && ((JoinColumn) campoClase.getAnnotation(JoinColumn.class)).name().equals(config.getNColumna()))) {
//                            columnas.add(new Columnas(config.getDescripcion(), campoClase.getName()));
//                            break;
//                        }
//                    }
//
//                }
//            }
//        }
//        refreshElementoCatalogo();
//    }
//
//    private TblAccesoPantallas obtieneMenu(TblAccesoPantallas menuTemp, List<TblAccesoPantallas> menuTodo) {
//        TblAccesoPantallas menuObtenido = null;
//        for (TblAccesoPantallas tblAccesoPantallas : menuTodo) {
//            if (tblAccesoPantallas.equals(menuTemp)) {
//                menuObtenido = tblAccesoPantallas;
//                break;
//            }
//            if (tblAccesoPantallas.getMenusHijo() != null) {
//                menuObtenido = obtieneMenu(menuTemp, tblAccesoPantallas.getMenusHijo());
//                if (menuObtenido != null) {
//                    break;
//                }
//            }
//        }
//        return menuObtenido;
//    }
//
//    public void openDialogFrmElemento(final String nombreDialog, final String tipoOperacion) {
//        FacesMessage message = null;
//        switch (tipoOperacion) {
//            case "nuevo":
//                nuevoElemento();
//                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                break;
//            case "editar":
//                if (elementoSeleccionado != null) {
//                    empleadoFrmBean.setIdElemento(idElementoSeleccionado);
//                    empleadoFrmBean.setPermissionToWrite(true);
//                    empleadoFrmBean.init();
//                    empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Editar registro");
//                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                } else {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
//                }
//                if (message != null) {
//                    FacesContext.getCurrentInstance().addMessage(null, message);
//                }
//                break;
//            case "ver":
//                if (elementoSeleccionado != null) {
//                    empleadoFrmBean.setIdElemento(idElementoSeleccionado);
//                    empleadoFrmBean.setPermissionToWrite(false);
//                    empleadoFrmBean.init();
//                    empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Ver registro");
//                    RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                } else {
//                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00f3n", "Ning\u00FAn elemento seleccionado.");
//                }
//                if (message != null) {
//                    FacesContext.getCurrentInstance().addMessage(null, message);
//                }
//                break;
//            case "imprimir":
//                empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Reporte");
//                //RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                break;
//            case "cargaExcel":
//                empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Carga de excel");
//                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                break;
//            case "consulta":
//                //empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Consulta");
//                filtroEntidad.setIdClaseEntidad(menuOrigen.getIdTablaVist());
//                filtroEntidad.setClaseEntidad(claseElemento);
//                RequestContext.getCurrentInstance().execute("PF('" + nombreDialog + "').show()");
//                break;
//        }
//    }
//
//    public void nuevoElemento() {
//        elementoSeleccionado = null;
//        idElementoSeleccionado = null;
//        empleadoFrmBean.setIdElemento(null);
//        empleadoFrmBean.setPermissionToWrite(true);
//        empleadoFrmBean.init();
//        empleadoFrmBean.setNombreAccionFrm("Cat\u00E1logo de Empleados - Agregar registro");
//    }
//
//    public void refreshElementoCatalogo() {
//        FacesMessage message = null;
//        GenericDAO genericDAO = null;
//        Field[] camposClaseElemento = claseElemento.getDeclaredFields();
//        try {
//            Class claseDAO = null;
//            try {
//                claseDAO = Class.forName("com.cossystem.core.dao." + claseElemento.getSimpleName() + "DAO");
//            } catch (ClassNotFoundException ex) {
//                claseDAO = null;
//            }
//            if (claseDAO != null) {
//                genericDAO = (GenericDAO) claseDAO.newInstance();
//            } else {
//                genericDAO = new GenericDAO();
//            }
//            if (filtros == null) {
//                filtros = new ArrayList<>();
//            }
//            Filtro filtroTemp;
//            if (usuarioSesion.getIdEmpresa() != null) {
//                for (Field campo : camposClaseElemento) {
//                    if (campo.getType().getSimpleName().equals(usuarioSesion.getIdEmpresa().getClass().getSimpleName())) {
//                        filtroTemp = Filtro.generaFiltro(campo, usuarioSesion.getIdEmpresa(), null, claseElemento);
//                        filtroTemp.setVisible(false);
//                        filtros.add(filtroTemp);
//                        break;
//                    }
//                }
//            }
//            if (menuOrigen.getFiltro() != null) {
//                System.out.println("si tiene filtros");
//                String[] filtrosDefault = menuOrigen.getFiltro().split(",");
//                String[] componentesFiltro;
//                String nombreCampo;
//                System.out.println("tiene: " + filtrosDefault.length + " filtros");
//                for (String filtroDefault : filtrosDefault) {
//                    componentesFiltro = filtroDefault.split("=");
//                    if (componentesFiltro.length == 2) {
//                        System.out.println("si entro a filtro");
//                        System.out.println(componentesFiltro[0] + "- " + componentesFiltro[1]);
//                        for (Field campo : camposClaseElemento) {
//                            if (campo.isAnnotationPresent(Column.class)) {
//                                nombreCampo = ((Column) campo.getAnnotation(Column.class)).name();
//                            } else if (campo.isAnnotationPresent(JoinColumn.class)) {
//                                nombreCampo = ((JoinColumn) campo.getAnnotation(JoinColumn.class)).name();
//                            } else {
//                                nombreCampo = null;
//                            }
//                            System.out.println("nombre campo: " + nombreCampo);
//                            if (nombreCampo != null && componentesFiltro[0].trim().equalsIgnoreCase(nombreCampo)) {
//                                System.out.println("se econtro campo: " + nombreCampo);
//                                TblAccesoPantallasCampos confCampo = null;
//                                for (TblAccesoPantallasCampos conf : configuracionPantalla) {
//                                    if (conf.getNColumna().equalsIgnoreCase(nombreCampo)) {
//                                        confCampo = conf;
//                                        break;
//                                    }
//                                }
//                                filtroTemp = Filtro.generaFiltro(campo, componentesFiltro[1], campo.getType().getSimpleName().equals(Date.class.getSimpleName()) ? confCampo != null ? confCampo.getFormatoCampo() : null : null, claseElemento);
//                                filtroTemp.setVisible(false);
//                                filtros.add(filtroTemp);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            if (filtroEntidad != null && (filtroEntidad.getFiltros() == null || filtroEntidad.getFiltros() != null && filtroEntidad.getFiltros().isEmpty())) {
//                filtroEntidad.setFiltros(filtros);
//            }
//            elementoCatalogo = genericDAO.findByComponents(claseElemento, filtroEntidad != null ? filtroEntidad.getFiltros() : filtros);
//        } catch (DAOException | DataBaseException | InstantiationException | IllegalAccessException | ParseException ex) {
//            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//        } finally {
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
//        if (message != null) {
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
//    }
//
//    public void selectElemento() {
//        FacesMessage message = null;
//        Field[] campos = claseElemento.getClass().getDeclaredFields();
//        Method[] metodos = claseElemento.getClass().getMethods();
//        for (Field campo : campos) {
//            try {
//                if (campo.isAnnotationPresent(Id.class)) {
//                    for (Method metodo : metodos) {
//                        if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
//                            idElementoSeleccionado = (Integer) metodo.invoke(elementoSeleccionado);
//                            break;
//                        }
//                    }
//                }
//            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
//                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//            }
//        }
//        if (message != null) {
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
//    }
//
//    public void cerroDialogElemento(String nombreFormulario) {
//        RequestContext.getCurrentInstance().reset(nombreFormulario);
//        empleadoFrmBean.cerrarConexion();
//    }
//
//    public void eliminaElemento(final String nombreTabla) {
//        FacesMessage message;
//        GenericDAO genericDAO = null;
//        Properties properties;
//        if (elementoSeleccionado != null) {
//            try {
//                properties = Propiedades.obtienePropiedades();
//                Class claseDAO = Configuracion.buscarClase("com.cossystem.core.dao", claseElementoTransaccional.getSimpleName() + "DAO");
//                if (claseDAO != null) {
//                    genericDAO = (GenericDAO) claseDAO.newInstance();
//                } else {
//                    genericDAO = new GenericDAO();
//                }
//                campoIdElemento.setAccessible(true);
//                Integer idEliminado = campoIdElemento.getInt(elementoSeleccionado);
//                genericDAO.deleteById(claseElementoTransaccional, idEliminado);
//                File dirFotos = new File(properties.getProperty("fotosPath") + File.separator + idEliminado);
//                if (dirFotos.isDirectory()) {
//                    dirFotos.delete();
//                }
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El elemento ha sido eliminado");
//            } catch (DAOException | DataBaseException | IOException | InstantiationException | IllegalAccessException ex) {
//                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
//            } finally {
//                if (genericDAO != null) {
//                    genericDAO.closeDAO();
//                }
//                refreshElementoCatalogo();
//                RequestContext.getCurrentInstance().execute("PF('" + nombreTabla + "').filter()");
//            }
//        } else {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenci\u00F3n", "Ning\u00FAn elemento seleccionado");
//        }
//        FacesContext.getCurrentInstance().addMessage(null, message);
//    }
//
//    public String generaExcelElemento() throws DAOException, DataBaseException, CossException {
//        FacesContext context = FacesContext.getCurrentInstance();
//        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//        String contextPathResources = servletContext.getRealPath("");
//        return contextPathResources + File.separator + "tempExcel" + File.separator + ManagerXLSX.generaArchivoExcel(claseElementoTransaccional, filtroEntidad != null ? filtroEntidad.getFiltros() : filtros, contextPathResources + File.separator + "tempExcel");
//    }
//
//    public Integer getIdElementoSeleccionado() {
//        return idElementoSeleccionado;
//    }
//
//    public void setIdElementoSeleccionado(Integer idElementoSeleccionado) {
//        this.idElementoSeleccionado = idElementoSeleccionado;
//    }
//
//    public Object getElementoSeleccionado() {
//        return elementoSeleccionado;
//    }
//
//    public void setElementoSeleccionado(Object elementoSeleccionado) {
//        this.elementoSeleccionado = elementoSeleccionado;
//    }
//
//    public List<?> getElementoCatalogo() {
//        return elementoCatalogo;
//    }
//
//    public CatUsuarios getUsuarioSesion() {
//        return usuarioSesion;
//    }
//
//    public PrincipalBean getPrincipalBean() {
//        return principalBean;
//    }
//
//    public void setPrincipalBean(PrincipalBean principalBean) {
//        this.principalBean = principalBean;
//    }
//
//    public EmpleadoFrmBean getEmpleadoFrmBean() {
//        return empleadoFrmBean;
//    }
//
//    public void setEmpleadoFrmBean(EmpleadoFrmBean empleadoFrmBean) {
//        this.empleadoFrmBean = empleadoFrmBean;
//    }
//
//    public StreamedContent getFileExcel() {
//        FacesMessage message = null;
//        String nombreArchivoExcel;
//        InputStream stream;
//        try {
//            nombreArchivoExcel = generaExcelElemento();
//            stream = new FileInputStream(nombreArchivoExcel);
//            Calendar fechaHoy = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
//            nombreArchivo = nombreArchivoExcel;
//            fileExcel = new DefaultStreamedContent(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "catalogoEmpleados" + sdf.format(fechaHoy.getTime()) + ".xlsx");
//        } catch (IOException | DAOException | DataBaseException | CossException ex) {
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al descargar archivo", ex.getMessage());
//        }
//        if (message != null) {
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
//        return fileExcel;
//    }
//
//    public void setFileExcel(StreamedContent fileExcel) {
//        this.fileExcel = fileExcel;
//    }
//
//    public void borraTempExcel() {
//        File file = new File(nombreArchivo);
//        if (file.isFile()) {
//            file.delete();
//        }
//    }
//
//    public void handleFileExcel(FileUploadEvent event) {
//        FacesMessage message;
//        FacesContext context = FacesContext.getCurrentInstance();
//        OutputStream outputStream = null;
//        InputStream inputStream = null;
//        File archivoTemp = null;
//        try {
//            UploadedFile file = event.getFile();
//            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//            String contextPathResources = servletContext.getRealPath("");
//            File dirTemp = new File(contextPathResources + File.separator + "tempCargaExcel");
//            if (!dirTemp.isDirectory()) {
//                dirTemp.mkdirs();
//            }
//            String rutaArchivoTemp = contextPathResources + File.separator + "tempCargaExcel" + File.separator + "tempCarga" + Calendar.getInstance().getTimeInMillis() + ".xlsx";
//            archivoTemp = new File(rutaArchivoTemp);
//            outputStream = new FileOutputStream(archivoTemp);
//            inputStream = event.getFile().getInputstream();
//            int read = 0;
//            byte[] bytes = new byte[1024];
//            while ((read = inputStream.read(bytes)) != -1) {
//                outputStream.write(bytes, 0, read);
//            }
//            ManagerXLSX.cargaCatalogoExcel(claseElementoTransaccional, rutaArchivoTemp);
//            refreshElementoCatalogo();
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "La carga del archivo excel se realiz\u00f3 correctamente");
//        } catch (CossException | IOException ex) {
//            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + ex.getMessage());
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + e.getMessage());
//                }
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "La carga del archivo excel fall\u00f3: " + e.getMessage());
//                }
//
//            }
//            if (archivoTemp != null) {
//                archivoTemp.delete();
//            }
//        }
//        context.addMessage(null, message);
//    }
//
//    public FiltroEntidad getFiltroEntidad() {
//        return filtroEntidad;
//    }
//
//    public void setFiltroEntidad(FiltroEntidad filtroEntidad) {
//        this.filtroEntidad = filtroEntidad;
//    }
//
//    public void todosLosRegistros() {
//        filtroEntidad.init();
//        refreshElementoCatalogo();
//    }
//
//    public TblAccesoPantallas getMenuOrigen() {
//        return menuOrigen;
//    }
//
//    public void setMenuOrigen(TblAccesoPantallas menuOrigen) {
//        this.menuOrigen = menuOrigen;
//    }
//
//    public List<TblAccesoPantallasCampos> getConfiguracionPantalla() {
//        return configuracionPantalla;
//    }
//
//    public void setConfiguracionPantalla(List<TblAccesoPantallasCampos> configuracionPantalla) {
//        this.configuracionPantalla = configuracionPantalla;
//    }
//
//    public Field getCampoIdElemento() {
//        return campoIdElemento;
//    }
//
//    public void setCampoIdElemento(Field campoIdElemento) {
//        this.campoIdElemento = campoIdElemento;
//    }
//
//    public Field getCampoIdElementoTransaccional() {
//        return campoIdElementoTransaccional;
//    }
//
//    public void setCampoIdElementoTransaccional(Field campoIdElementoTransaccional) {
//        this.campoIdElementoTransaccional = campoIdElementoTransaccional;
//    }
//
//    public List<Columnas> getColumnas() {
//        return columnas;
//    }
//
//    public void setColumnas(List<Columnas> columnas) {
//        this.columnas = columnas;
//    }
}
