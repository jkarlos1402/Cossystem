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
    private List<CatEmpOrigen> catalogoEmpleadoOrigen;
    private List<CatArea> catalogoArea;
    private List<CatUbicacion> catalogoUbicacion;
    private List<CatEmpNivel> catalogoNivel;
    private List<TblEmpleadosDiarioActividad> catalogoActividad;
    private List<TblEmpleadosDiarioActividadDet> catalogoActividadDet;
    private List<CatSexo> catalogoSexo;
    private boolean bndCatalogoSexo = true;
    private List<CatStatus> catalogoEstatus;
    private List<CatCPESTADO> catalogoEstados;
    private List<CatTipoConexion> catalogoTipoConexion;
    private List<TblEmpresaPosicion> catalogoPosicion;
    private List<CatDepartamento> catalogoDepartamento;
    private List<CatEmpPuestos> catalogoPuestos;
    private List<CatCECO> catalogoCentros;
    private List<TblEmpresaProyectos> catalogoProyectos;

    @ManagedProperty(value = "#{principalBean}")
    private PrincipalBean principalBean;

    public EmpleadoFrmBean() {
        // to do
    }

    @PostConstruct
    public void init() {
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

            mapaComponentes.clear();
            catalogoEstados = genericDAO.findByComponents(CatCPESTADO.class, mapaComponentes);
            mapaComponentes.clear();
            catalogoEstatus = genericDAO.findByComponents(CatStatus.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            catalogoTipoConexion = genericDAO.findByComponents(CatTipoConexion.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idempresa", empleado.getIdEmpresa());
            catalogoEmpleadoOrigen = genericDAO.findByComponents(CatEmpOrigen.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            mapaComponentes.put("idStatus", true);
            catalogoArea = genericDAO.findByComponents(CatArea.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            mapaComponentes.put("idStatus", true);
            catalogoUbicacion = genericDAO.findByComponents(CatUbicacion.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            catalogoNivel = genericDAO.findByComponents(CatEmpNivel.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            mapaComponentes.put("contacto", true);
            catalogoContacto = genericDAO.findByComponents(TblEmpleados.class, mapaComponentes);
            if (bndCatalogoSexo) {
                System.out.println("se obtendra sexo");
                mapaComponentes.clear();
                catalogoSexo = genericDAO.findByComponents(CatSexo.class, mapaComponentes);
                bndCatalogoSexo = false;
            }
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            catalogoActividad = genericDAO.findByComponents(TblEmpleadosDiarioActividad.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            catalogoPosicion = genericDAO.findByComponents(TblEmpresaPosicion.class, mapaComponentes);
            mapaComponentes.clear();
            mapaComponentes.put("idEmpresa", empleado.getIdEmpresa());
            mapaComponentes.put("idStatus", true);
            catalogoProyectos = genericDAO.findByComponents(TblEmpresaProyectos.class, mapaComponentes);

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
        } catch (DAOException ex) {
            System.out.println("error.... " + ex.getMessage());
        } catch (DataBaseException ex) {
            Logger.getLogger(EmpleadoFrmBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (genericDAO != null) {
                genericDAO.closeDAO();
            }
        }
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

    public List<CatEmpOrigen> getCatalogoEmpleadoOrigen() {
        return catalogoEmpleadoOrigen;
    }

    public void setCatalogoEmpleadoOrigen(List<CatEmpOrigen> catalogoEmpleadoOrigen) {
        this.catalogoEmpleadoOrigen = catalogoEmpleadoOrigen;
    }

    public List<CatArea> getCatalogoArea() {
        return catalogoArea;
    }

    public void setCatalogoArea(List<CatArea> catalogoArea) {
        this.catalogoArea = catalogoArea;
    }

    public List<CatUbicacion> getCatalogoUbicacion() {
        return catalogoUbicacion;
    }

    public void setCatalogoUbicacion(List<CatUbicacion> catalogoUbicacion) {
        this.catalogoUbicacion = catalogoUbicacion;
    }

    public List<CatEmpNivel> getCatalogoNivel() {
        return catalogoNivel;
    }

    public void setCatalogoNivel(List<CatEmpNivel> catalogoNivel) {
        this.catalogoNivel = catalogoNivel;
    }

    public List<TblEmpleadosDiarioActividad> getCatalogoActividad() {
        return catalogoActividad;
    }

    public void setCatalogoActividad(List<TblEmpleadosDiarioActividad> catalogoActividad) {
        this.catalogoActividad = catalogoActividad;
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

    public List<CatCPESTADO> getCatalogoEstados() {
        return catalogoEstados;
    }

    public void setCatalogoEstados(List<CatCPESTADO> catalogoEstados) {
        this.catalogoEstados = catalogoEstados;
    }

    public List<CatTipoConexion> getCatalogoTipoConexion() {
        return catalogoTipoConexion;
    }

    public void setCatalogoTipoConexion(List<CatTipoConexion> catalogoTipoConexion) {
        this.catalogoTipoConexion = catalogoTipoConexion;
    }

    public List<TblEmpresaPosicion> getCatalogoPosicion() {
        return catalogoPosicion;
    }

    public void setCatalogoPosicion(List<TblEmpresaPosicion> catalogoPosicion) {
        this.catalogoPosicion = catalogoPosicion;
    }

    public PrincipalBean getPrincipalBean() {
        return principalBean;
    }

    public void setPrincipalBean(PrincipalBean principalBean) {
        this.principalBean = principalBean;
    }

    public List<CatDepartamento> getCatalogoDepartamento() {
        return catalogoDepartamento;
    }

    public void setCatalogoDepartamento(List<CatDepartamento> catalogoDepartamento) {
        this.catalogoDepartamento = catalogoDepartamento;
    }

    public TblEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(TblEmpresa empresa) {
        this.empresa = empresa;
    }

    public List<CatEmpPuestos> getCatalogoPuestos() {
        return catalogoPuestos;
    }

    public void setCatalogoPuestos(List<CatEmpPuestos> catalogoPuestos) {
        this.catalogoPuestos = catalogoPuestos;
    }

    public List<CatCECO> getCatalogoCentros() {
        return catalogoCentros;
    }

    public void setCatalogoCentros(List<CatCECO> catalogoCentros) {
        this.catalogoCentros = catalogoCentros;
    }

    public List<TblEmpresaProyectos> getCatalogoProyectos() {
        return catalogoProyectos;
    }

    public void setCatalogoProyectos(List<TblEmpresaProyectos> catalogoProyectos) {
        this.catalogoProyectos = catalogoProyectos;
    }

    public List<TblEmpleadosDiarioActividadDet> getCatalogoActividadDet() {
        return catalogoActividadDet;
    }

    public void setCatalogoActividadDet(List<TblEmpleadosDiarioActividadDet> catalogoActividadDet) {
        this.catalogoActividadDet = catalogoActividadDet;
    }

    public boolean isPermissionToWrite() {
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
}
