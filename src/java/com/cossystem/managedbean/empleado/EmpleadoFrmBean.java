/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.dao.TblEmpleadosDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.CatArea;
import com.cossystem.core.pojos.CatCECO;
import com.cossystem.core.pojos.CatCPESTADO;
import com.cossystem.core.pojos.CatDepartamento;
import com.cossystem.core.pojos.CatEmpNivel;
import com.cossystem.core.pojos.CatEmpOrigen;
import com.cossystem.core.pojos.CatEmpPuestos;
import com.cossystem.core.pojos.CatSexo;
import com.cossystem.core.pojos.CatStatus;
import com.cossystem.core.pojos.CatTipoConexion;
import com.cossystem.core.pojos.CatUbicacion;
import com.cossystem.core.pojos.CatUsuarios;
import com.cossystem.core.pojos.TblEmpleados;
import com.cossystem.core.pojos.TblEmpleadosDiarioActividad;
import com.cossystem.core.pojos.TblEmpleadosDiarioActividadDet;
import com.cossystem.core.pojos.TblEmpleadosFotos;
import com.cossystem.core.pojos.TblEmpresa;
import com.cossystem.core.pojos.TblEmpresaPosicion;
import com.cossystem.core.pojos.TblEmpresaProyectos;
import com.cossystem.managedbean.PrincipalBean;
import com.cossystem.util.Propiedades;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author JC
 */
@ManagedBean
@ViewScoped
public class EmpleadoFrmBean implements Serializable {

    private GenericDAO genericDAO = null;

    private CatUsuarios usuarioSesion;
    private TblEmpresa empresa;
    private boolean permissionToWrite = true;
    private String tituloSubCatalogo = "";
    private String nombreAccionFrm = "";

    private Integer idElemento;
    private TblEmpleados empleado;

    private List<TblEmpleados> catalogoContacto;
    private boolean bndCatalogoContacto = true;
    private List<CatEmpOrigen> catalogoEmpleadoOrigen;
    private boolean bndCatalogoEmpleadoOrigen = true;
    private List<CatArea> catalogoArea;
    private boolean bndCatalogoArea = true;
    private List<CatUbicacion> catalogoUbicacion;
    private boolean bndCatalogoUbicacion = true;
    private List<CatEmpNivel> catalogoNivel;
    private boolean bndCatalogoNivel = true;
    private List<TblEmpleadosDiarioActividad> catalogoActividad;
    private boolean bndCatalogoActividad = true;
    private List<TblEmpleadosDiarioActividadDet> catalogoActividadDet;
    private boolean bndCatalogoActividadDet = true;
    private List<CatSexo> catalogoSexo;
    private boolean bndCatalogoSexo = true;
    private List<CatStatus> catalogoEstatus;
    private boolean bndCatalogoEstatus = true;
    private List<CatCPESTADO> catalogoEstados;
    private boolean bndCatalogoEstados = true;
    private List<CatTipoConexion> catalogoTipoConexion;
    private boolean bndCatalogoTipoConexion = true;
    private List<TblEmpresaPosicion> catalogoPosicion;
    private boolean bndCatalogoPosicion = true;
    private List<CatDepartamento> catalogoDepartamento;
    private boolean bndCatalogoDepartamento = true;
    private List<CatEmpPuestos> catalogoPuestos;
    private boolean bndCatalogoPuestos = true;
    private List<CatCECO> catalogoCentros;
    private boolean bndCatalogoCentros = true;
    private List<TblEmpresaProyectos> catalogoProyectos;
    private boolean bndCatalogoProyectos = true;

    @ManagedProperty(value = "#{principalBean}")
    private PrincipalBean principalBean;

    public EmpleadoFrmBean() {
        // to do
    }

    @PostConstruct
    public void init() {
        TreeMap mapaComponentes = new TreeMap<>();
        try {
            if (genericDAO == null) {
                genericDAO = new TblEmpleadosDAO();
            }
            if (principalBean != null) {
                usuarioSesion = principalBean.getUsuarioSesion();
                empresa = usuarioSesion.getIdEmpresa();
            }
            if (empleado == null) {
                empleado = new TblEmpleados();
                empleado.setIdEmpresa(empresa);
            } else if (empleado.getIdEmpleado() == null) {
                empleado.setIdEmpresa(empresa);
            } else if (empleado.getIdEmpleado() != null) {                
                genericDAO.getSession().refresh(empleado);
            }
            if (bndCatalogoEstados) {
                mapaComponentes.clear();
                catalogoEstados = genericDAO.findByComponents(CatCPESTADO.class, mapaComponentes);
                bndCatalogoEstados = false;
            }
            if (bndCatalogoEstatus) {
                mapaComponentes.clear();
                catalogoEstatus = genericDAO.findByComponents(CatStatus.class, mapaComponentes);
                bndCatalogoEstatus = false;
            }
            if (bndCatalogoTipoConexion) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                catalogoTipoConexion = genericDAO.findByComponents(CatTipoConexion.class, mapaComponentes);
                bndCatalogoTipoConexion = false;
            }
            if (bndCatalogoEmpleadoOrigen) {
                mapaComponentes.clear();
                mapaComponentes.put("idempresa", empleado.getIdEmpresa());
                catalogoEmpleadoOrigen = genericDAO.findByComponents(CatEmpOrigen.class, mapaComponentes);
                bndCatalogoEmpleadoOrigen = false;
            }
            if (bndCatalogoArea) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                mapaComponentes.put("idStatus", true);
                catalogoArea = genericDAO.findByComponents(CatArea.class, mapaComponentes);
                bndCatalogoArea = false;
            }
            if (bndCatalogoUbicacion) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                mapaComponentes.put("idStatus", true);
                catalogoUbicacion = genericDAO.findByComponents(CatUbicacion.class, mapaComponentes);
                bndCatalogoUbicacion = false;
            }
            if (bndCatalogoNivel) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                catalogoNivel = genericDAO.findByComponents(CatEmpNivel.class, mapaComponentes);
                bndCatalogoNivel = false;
            }
            if (bndCatalogoContacto) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                mapaComponentes.put("contacto", true);
                catalogoContacto = genericDAO.findByComponents(TblEmpleados.class, mapaComponentes);
                bndCatalogoContacto = false;
            }
            if (bndCatalogoSexo) {
                mapaComponentes.clear();
                catalogoSexo = genericDAO.findByComponents(CatSexo.class, mapaComponentes);
                bndCatalogoSexo = false;
            }
            if (bndCatalogoActividad) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                catalogoActividad = genericDAO.findByComponents(TblEmpleadosDiarioActividad.class, mapaComponentes);
                bndCatalogoActividad = false;
            }
            if (bndCatalogoPosicion) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                catalogoPosicion = genericDAO.findByComponents(TblEmpresaPosicion.class, mapaComponentes);
                bndCatalogoPosicion = false;
            }
            if (bndCatalogoProyectos) {
                mapaComponentes.clear();
                mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
                mapaComponentes.put("idStatus", true);
                catalogoProyectos = genericDAO.findByComponents(TblEmpresaProyectos.class, mapaComponentes);
                bndCatalogoProyectos = false;
            }
            if (empleado.getIdEmpleado() != null) {
                if (empleado.getIdArea() != null) {
                    mapaComponentes.clear();
                    mapaComponentes.put("idArea", empleado.getIdArea());
                    mapaComponentes.put("idStatus", true);
                    catalogoDepartamento = genericDAO.findByComponents(CatDepartamento.class, mapaComponentes);
                }
                if (empleado.getIdDepartamento() != null) {
                    mapaComponentes.clear();
                    mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
                    mapaComponentes.put("idStatus", true);
                    catalogoCentros = genericDAO.findByComponents(CatCECO.class, mapaComponentes);
                    mapaComponentes.clear();
                    mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
                    mapaComponentes.put("idStatus", true);
                    catalogoPuestos = genericDAO.findByComponents(CatEmpPuestos.class, mapaComponentes);
                }
                if (empleado.getIdActividad() != null) {
                    mapaComponentes.clear();
                    mapaComponentes.put("idActividad", empleado.getIdActividad());
                    catalogoActividadDet = genericDAO.findByComponents(TblEmpleadosDiarioActividadDet.class, mapaComponentes);
                }
            }
            FacesContext context = FacesContext.getCurrentInstance();
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String contextPathResources = servletContext.getRealPath("");
            Properties propiedadesIniciales = new Properties();
            propiedadesIniciales.load(new FileInputStream(contextPathResources + File.separator + "WEB-INF" + File.separator + "initConfig.properties"));
            Properties propiedades = new Properties();
            propiedades.load(new FileInputStream(propiedadesIniciales.getProperty("configPath") + File.separator + "config.properties"));
        } catch (DAOException | DataBaseException | IOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        } finally {
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
    }

    public void listenerSelectArea() {
//        GenericDAO genericDAO = null;
        try {
//            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idArea", empleado.getIdArea());
            mapaComponentes.put("idStatus", true);
            catalogoDepartamento = genericDAO.findByComponents(CatDepartamento.class, mapaComponentes);
        } catch (DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally {
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
    }

    public void listenerSelectDepartamento() {
//        GenericDAO genericDAO = null;
        try {
//            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
            mapaComponentes.put("idStatus", true);
            catalogoCentros = genericDAO.findByComponents(CatCECO.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
            mapaComponentes.put("idStatus", true);
            catalogoPuestos = genericDAO.findByComponents(CatEmpPuestos.class, mapaComponentes);
        } catch (DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally {
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
    }

    public void listenerSelectActividad() {
//        GenericDAO genericDAO = null;
        try {
//            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idActividad", empleado.getIdActividad());
            catalogoActividadDet = genericDAO.findByComponents(TblEmpleadosDiarioActividadDet.class, mapaComponentes);
        } catch (DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally {
//            if (genericDAO != null) {
//                genericDAO.closeDAO();
//            }
//        }
    }

    public void resetDialog() {
        RequestContext.getCurrentInstance().reset("formFrmEmpleado");
    }

    public void guardarElemento() {
        FacesContext context = FacesContext.getCurrentInstance();
//        GenericDAO genericDAO = null;
        FacesMessage message = null;
        Properties properties = null;
        if (empleado != null) {
            try {
                properties = Propiedades.obtienePropiedades();
//                genericDAO = new TblEmpleadosDAO();
                genericDAO.saveOrUpdate(empleado);
                File dirFotos = new File(properties.getProperty("fotosPath") + File.separator + empleado.getIdEmpleado());
                File[] archivosDirFotos;
                if (dirFotos.isDirectory()) {
                    archivosDirFotos = dirFotos.listFiles();
                    labelFoto:
                    for (File archivoDirFotos : archivosDirFotos) {
                        if (archivoDirFotos.isFile()) {
                            for (int i = 0; i < (empleado.getTblEmpleadosFotosList() != null ? empleado.getTblEmpleadosFotosList().size() : 0); i++) {
                                if (empleado.getTblEmpleadosFotosList().get(i).getNombreFoto().equals(archivoDirFotos.getName())) {
                                    continue labelFoto;
                                }

                            }
                            archivoDirFotos.delete();
                        }
                    }
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "Elemento guardado correctamente");
            } catch (DAOException | IOException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            }
//            finally {
//                if (genericDAO != null) {
//                    genericDAO.closeDAO();
//                }
//            }
        }
        if (message != null) {
            context.addMessage(null, message);
        }
    }

    public CatUsuarios getUsuarioSesion() {
        return usuarioSesion;
    }

    public void setUsuarioSesion(CatUsuarios usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    public TblEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(TblEmpresa empresa) {
        this.empresa = empresa;
    }

    public boolean getPermissionToWrite() {
        return permissionToWrite;
    }

    public void setPermissionToWrite(boolean permissionToWrite) {
        this.permissionToWrite = permissionToWrite;
    }

    public String getTituloSubCatalogo() {
        return tituloSubCatalogo;
    }

    public void setTituloSubCatalogo(String tituloSubCatalogo) {
        this.tituloSubCatalogo = tituloSubCatalogo;
    }

    public TblEmpleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(TblEmpleados empleado) {
        this.empleado = empleado;
    }

    public List<TblEmpleados> getCatalogoContacto() {
        return catalogoContacto;
    }

    public void setCatalogoContacto(List<TblEmpleados> catalogoContacto) {
        this.catalogoContacto = catalogoContacto;
    }

    public boolean getBndCatalogoContacto() {
        return bndCatalogoContacto;
    }

    public void setBndCatalogoContacto(boolean bndCatalogoContacto) {
        this.bndCatalogoContacto = bndCatalogoContacto;
    }

    public List<CatEmpOrigen> getCatalogoEmpleadoOrigen() {
        return catalogoEmpleadoOrigen;
    }

    public void setCatalogoEmpleadoOrigen(List<CatEmpOrigen> catalogoEmpleadoOrigen) {
        this.catalogoEmpleadoOrigen = catalogoEmpleadoOrigen;
    }

    public boolean getBndCatalogoEmpleadoOrigen() {
        return bndCatalogoEmpleadoOrigen;
    }

    public void setBndCatalogoEmpleadoOrigen(boolean bndCatalogoEmpleadoOrigen) {
        this.bndCatalogoEmpleadoOrigen = bndCatalogoEmpleadoOrigen;
    }

    public List<CatArea> getCatalogoArea() {
        return catalogoArea;
    }

    public void setCatalogoArea(List<CatArea> catalogoArea) {
        this.catalogoArea = catalogoArea;
    }

    public boolean getBndCatalogoArea() {
        return bndCatalogoArea;
    }

    public void setBndCatalogoArea(boolean bndCatalogoArea) {
        this.bndCatalogoArea = bndCatalogoArea;
    }

    public List<CatUbicacion> getCatalogoUbicacion() {
        return catalogoUbicacion;
    }

    public void setCatalogoUbicacion(List<CatUbicacion> catalogoUbicacion) {
        this.catalogoUbicacion = catalogoUbicacion;
    }

    public boolean getBndCatalogoUbicacion() {
        return bndCatalogoUbicacion;
    }

    public void setBndCatalogoUbicacion(boolean bndCatalogoUbicacion) {
        this.bndCatalogoUbicacion = bndCatalogoUbicacion;
    }

    public List<CatEmpNivel> getCatalogoNivel() {
        return catalogoNivel;
    }

    public void setCatalogoNivel(List<CatEmpNivel> catalogoNivel) {
        this.catalogoNivel = catalogoNivel;
    }

    public boolean getBndCatalogoNivel() {
        return bndCatalogoNivel;
    }

    public void setBndCatalogoNivel(boolean bndCatalogoNivel) {
        this.bndCatalogoNivel = bndCatalogoNivel;
    }

    public List<TblEmpleadosDiarioActividad> getCatalogoActividad() {
        return catalogoActividad;
    }

    public void setCatalogoActividad(List<TblEmpleadosDiarioActividad> catalogoActividad) {
        this.catalogoActividad = catalogoActividad;
    }

    public boolean getBndCatalogoActividad() {
        return bndCatalogoActividad;
    }

    public void setBndCatalogoActividad(boolean bndCatalogoActividad) {
        this.bndCatalogoActividad = bndCatalogoActividad;
    }

    public List<TblEmpleadosDiarioActividadDet> getCatalogoActividadDet() {
        return catalogoActividadDet;
    }

    public void setCatalogoActividadDet(List<TblEmpleadosDiarioActividadDet> catalogoActividadDet) {
        this.catalogoActividadDet = catalogoActividadDet;
    }

    public boolean getBndCatalogoActividadDet() {
        return bndCatalogoActividadDet;
    }

    public void setBndCatalogoActividadDet(boolean bndCatalogoActividadDet) {
        this.bndCatalogoActividadDet = bndCatalogoActividadDet;
    }

    public List<CatSexo> getCatalogoSexo() {
        return catalogoSexo;
    }

    public void setCatalogoSexo(List<CatSexo> catalogoSexo) {
        this.catalogoSexo = catalogoSexo;
    }

    public boolean getBndCatalogoSexo() {
        return bndCatalogoSexo;
    }

    public void setBndCatalogoSexo(boolean bndCatalogoSexo) {
        this.bndCatalogoSexo = bndCatalogoSexo;
    }

    public List<CatStatus> getCatalogoEstatus() {
        return catalogoEstatus;
    }

    public void setCatalogoEstatus(List<CatStatus> catalogoEstatus) {
        this.catalogoEstatus = catalogoEstatus;
    }

    public boolean getBndCatalogoEstatus() {
        return bndCatalogoEstatus;
    }

    public void setBndCatalogoEstatus(boolean bndCatalogoEstatus) {
        this.bndCatalogoEstatus = bndCatalogoEstatus;
    }

    public List<CatCPESTADO> getCatalogoEstados() {
        return catalogoEstados;
    }

    public void setCatalogoEstados(List<CatCPESTADO> catalogoEstados) {
        this.catalogoEstados = catalogoEstados;
    }

    public boolean getBndCatalogoEstados() {
        return bndCatalogoEstados;
    }

    public void setBndCatalogoEstados(boolean bndCatalogoEstados) {
        this.bndCatalogoEstados = bndCatalogoEstados;
    }

    public List<CatTipoConexion> getCatalogoTipoConexion() {
        return catalogoTipoConexion;
    }

    public void setCatalogoTipoConexion(List<CatTipoConexion> catalogoTipoConexion) {
        this.catalogoTipoConexion = catalogoTipoConexion;
    }

    public boolean getBndCatalogoTipoConexion() {
        return bndCatalogoTipoConexion;
    }

    public void setBndCatalogoTipoConexion(boolean bndCatalogoTipoConexion) {
        this.bndCatalogoTipoConexion = bndCatalogoTipoConexion;
    }

    public List<TblEmpresaPosicion> getCatalogoPosicion() {
        return catalogoPosicion;
    }

    public void setCatalogoPosicion(List<TblEmpresaPosicion> catalogoPosicion) {
        this.catalogoPosicion = catalogoPosicion;
    }

    public boolean getBndCatalogoPosicion() {
        return bndCatalogoPosicion;
    }

    public void setBndCatalogoPosicion(boolean bndCatalogoPosicion) {
        this.bndCatalogoPosicion = bndCatalogoPosicion;
    }

    public List<CatDepartamento> getCatalogoDepartamento() {
        return catalogoDepartamento;
    }

    public void setCatalogoDepartamento(List<CatDepartamento> catalogoDepartamento) {
        this.catalogoDepartamento = catalogoDepartamento;
    }

    public boolean getBndCatalogoDepartamento() {
        return bndCatalogoDepartamento;
    }

    public void setBndCatalogoDepartamento(boolean bndCatalogoDepartamento) {
        this.bndCatalogoDepartamento = bndCatalogoDepartamento;
    }

    public List<CatEmpPuestos> getCatalogoPuestos() {
        return catalogoPuestos;
    }

    public void setCatalogoPuestos(List<CatEmpPuestos> catalogoPuestos) {
        this.catalogoPuestos = catalogoPuestos;
    }

    public boolean getBndCatalogoPuestos() {
        return bndCatalogoPuestos;
    }

    public void setBndCatalogoPuestos(boolean bndCatalogoPuestos) {
        this.bndCatalogoPuestos = bndCatalogoPuestos;
    }

    public List<CatCECO> getCatalogoCentros() {
        return catalogoCentros;
    }

    public void setCatalogoCentros(List<CatCECO> catalogoCentros) {
        this.catalogoCentros = catalogoCentros;
    }

    public boolean getBndCatalogoCentros() {
        return bndCatalogoCentros;
    }

    public void setBndCatalogoCentros(boolean bndCatalogoCentros) {
        this.bndCatalogoCentros = bndCatalogoCentros;
    }

    public List<TblEmpresaProyectos> getCatalogoProyectos() {
        return catalogoProyectos;
    }

    public void setCatalogoProyectos(List<TblEmpresaProyectos> catalogoProyectos) {
        this.catalogoProyectos = catalogoProyectos;
    }

    public boolean getBndCatalogoProyectos() {
        return bndCatalogoProyectos;
    }

    public void setBndCatalogoProyectos(boolean bndCatalogoProyectos) {
        this.bndCatalogoProyectos = bndCatalogoProyectos;
    }

    public PrincipalBean getPrincipalBean() {
        return principalBean;
    }

    public void setPrincipalBean(PrincipalBean principalBean) {
        this.principalBean = principalBean;
    }

    public String getNombreAccionFrm() {
        return nombreAccionFrm;
    }

    public void setNombreAccionFrm(String nombreAccionFrm) {
        this.nombreAccionFrm = nombreAccionFrm;
    }

    public void nuevoElemento() {
        empleado = new TblEmpleados();
        permissionToWrite = true;
        init();
        nombreAccionFrm = "Cat\u00E1logo de Empleados - Agregar registro";
    }

    public void subirFoto(FileUploadEvent event) {
        FacesMessage message = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Properties propiedades = Propiedades.obtienePropiedades();
            String rutaFotos = (String) propiedades.get("fotosPath");
            String nombreCarpetaFotos = empleado.getIdEmpleado().toString();
            File dirFotos = new File(rutaFotos + File.separator + nombreCarpetaFotos);
            if (!dirFotos.isDirectory()) {
                dirFotos.mkdirs();
            }
            String ext = getExtension(event.getFile().getFileName());
            String nombreFoto = "foto-" + empleado.getIdEmpleado() + "-" + Calendar.getInstance().getTime().getTime() + "." + ext;
            outputStream = new FileOutputStream(dirFotos.getAbsolutePath() + File.separator + nombreFoto);
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            TblEmpleadosFotos foto = new TblEmpleadosFotos();
            foto.setIdEmpleado(empleado);
            foto.setIdEmpresa(empleado.getIdEmpresa());
            foto.setNombreFoto(nombreFoto);
            foto.setIdStatus(true);
            foto.setPrincipal(false);
            if (empleado.getTblEmpleadosFotosList() == null) {
                empleado.setTblEmpleadosFotosList(new ArrayList<TblEmpleadosFotos>());
            }
            empleado.getTblEmpleadosFotosList().add(foto);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Fotos cargadas, guarde los cambios");
        } catch (IOException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
                }
            }
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void selectPrincipal(int index) {
        if (empleado.getTblEmpleadosFotosList() != null && empleado.getTblEmpleadosFotosList().size() > index) {
            for (int i = 0; i < empleado.getTblEmpleadosFotosList().size(); i++) {
                if (i != index) {
                    empleado.getTblEmpleadosFotosList().get(i).setPrincipal(false);
                }
            }
        }
    }

    public void eliminaFoto(String nombreFoto) {
        FacesMessage message = null;       
        if (empleado.getTblEmpleadosFotosList() != null && !empleado.getTblEmpleadosFotosList().isEmpty()) {
            for (int i = 0; i < empleado.getTblEmpleadosFotosList().size(); i++) {
                if (empleado.getTblEmpleadosFotosList().get(i).getNombreFoto().equals(nombreFoto)) {
                    empleado.getTblEmpleadosFotosList().remove(i);
                }
            }            
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Foto eliminada, guarde para que los cambios se reflejen");
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    private static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    @PreDestroy
    public void cerrarConexion() {        
        if (genericDAO != null) {
            genericDAO.closeDAO();
            genericDAO = null;
        }
    }
}
