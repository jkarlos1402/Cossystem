/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.empleado;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.CatArea;
import com.cossystem.core.pojos.catalogos.CatCECO;
import com.cossystem.core.pojos.catalogos.CatCPESTADO;
import com.cossystem.core.pojos.catalogos.CatDepartamento;
import com.cossystem.core.pojos.catalogos.CatEmpNivel;
import com.cossystem.core.pojos.catalogos.CatEmpOrigen;
import com.cossystem.core.pojos.catalogos.CatEmpPuestos;
import com.cossystem.core.pojos.catalogos.CatSexo;
import com.cossystem.core.pojos.catalogos.CatStatus;
import com.cossystem.core.pojos.catalogos.CatTipoConexion;
import com.cossystem.core.pojos.catalogos.CatUbicacion;
import com.cossystem.core.pojos.catalogos.CatUsuarios;
import com.cossystem.core.pojos.empleado.TblEmpleados;
import com.cossystem.core.pojos.empleado.TblEmpleadosDiarioActividad;
import com.cossystem.core.pojos.empleado.TblEmpleadosDiarioActividadDet;
import com.cossystem.core.pojos.empresa.TblEmpresa;
import com.cossystem.core.pojos.empresa.TblEmpresaPosicion;
import com.cossystem.core.pojos.empresa.TblEmpresaProyectos;
import com.cossystem.managedbean.PrincipalBean;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JC
 */
@ManagedBean
@ViewScoped
public class EmpleadoFrmBean implements Serializable {

    private CatUsuarios usuarioSesion;
    private TblEmpresa empresa;
    private boolean permissionToWrite = true;
    private String tituloSubCatalogo = "";

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
        System.out.println("init frm empleado bean");
        GenericDAO genericDAO = null;
        TreeMap mapaComponentes = new TreeMap<>();
        try {
            genericDAO = new GenericDAO();
            if (principalBean != null) {
                usuarioSesion = principalBean.getUsuarioSesion();
                empresa = usuarioSesion.getIdEmpresa();
            }
            if (empleado == null) {
                empleado = new TblEmpleados();
                empleado.setIdEmpresa(empresa);
            } else if (empleado.getIdEmpleado() == null) {
                empleado.setIdEmpresa(empresa);
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
        } catch (DAOException | DataBaseException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    public void listenerSelectArea() {
        GenericDAO genericDAO = null;
        try {
            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idArea", empleado.getIdArea());
            mapaComponentes.put("idStatus", true);
            catalogoDepartamento = genericDAO.findByComponents(CatDepartamento.class, mapaComponentes);
        } catch (DataBaseException | DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    public void listenerSelectDepartamento() {
        GenericDAO genericDAO = null;
        try {
            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
            mapaComponentes.put("idStatus", true);
            catalogoCentros = genericDAO.findByComponents(CatCECO.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idDepartamento", empleado.getIdDepartamento());
            mapaComponentes.put("idStatus", true);
            catalogoPuestos = genericDAO.findByComponents(CatEmpPuestos.class, mapaComponentes);
        } catch (DataBaseException | DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    public void listenerSelectActividad() {
        GenericDAO genericDAO = null;
        try {
            genericDAO = new GenericDAO();
            TreeMap mapaComponentes = new TreeMap<>();
            mapaComponentes.clear();
            mapaComponentes.put("idActividad", empleado.getIdActividad());
            catalogoActividadDet = genericDAO.findByComponents(TblEmpleadosDiarioActividadDet.class, mapaComponentes);
        } catch (DataBaseException | DAOException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
    }

    public void resetDialog() {
        RequestContext.getCurrentInstance().reset("formFrmEmpleado");
    }

    public void guardarElemento() {
        FacesContext context = FacesContext.getCurrentInstance();
        GenericDAO genericDAO = null;
        FacesMessage message = null;
        if (empleado != null) {
            try {
                genericDAO = new GenericDAO();
                genericDAO.saveOrUpdate(empleado);
                RequestContext.getCurrentInstance().reset("formFrmEmpleado");
                empleado = new TblEmpleados();
                RequestContext.getCurrentInstance().execute("PF('dialogFrmEmpleado').hide()");
                RequestContext.getCurrentInstance().execute("PF('tablaEmpleado').filter()");
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "Elemento guardado correctamente");
            } catch (Exception ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
            } finally {
                if (genericDAO != null) {
                    genericDAO.closeDAO();
                }
            }
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

}
