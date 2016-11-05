package com.cossystem.managedbean;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.CatUsuarios;
import com.cossystem.core.pojos.TblAccesoPantallas;
import com.cossystem.core.pojos.TblAccesoPantallasCampos;
import com.cossystem.core.pojos.TblEmpresa;
import com.cossystem.core.util.CampoComponente;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.hibernate.metadata.ClassMetadata;
import org.primefaces.context.RequestContext;

public class FrmGenericElementoBean {

    protected GenericDAO genericDAO = null;

    protected CatUsuarios usuarioSesion;
    protected TblEmpresa empresa;

    protected boolean permissionToWrite = true;
    protected String tituloSubCatalogo = "";
    protected String nombreAccionFrm = "";

    protected Integer idElemento;
    protected Integer idTablaElemento;
    protected Class<?> claseElemento;
    protected Class<?> claseElementoDAO;
    protected Field campoIdElemento;
    
    protected List<CampoComponente> camposClaseElemento = new ArrayList<>();
    protected List<TblAccesoPantallasCampos> configuracionElemento = new ArrayList<>();

    protected Serializable elemento;

    protected TblAccesoPantallas menuOrigen = null;

    public FrmGenericElementoBean() {
        // to do
    }

    @PostConstruct
    public void init() {
        if (claseElemento != null) {
            CampoComponente componente = null;
            try {
                if (claseElementoDAO != null) {
                    genericDAO = (GenericDAO) claseElementoDAO.newInstance();
                } else {
                    genericDAO = new GenericDAO();
                }
                if (idElemento == null) {
                    try {
                        elemento = (Serializable) claseElemento.newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(FrmGenericElementoBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        elemento = genericDAO.findById(claseElemento, idElemento);
                    } catch (DAOException ex) {
                        Logger.getLogger(FrmGenericElementoBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                camposClaseElemento.clear();
                Field[] camposClase = claseElemento.getDeclaredFields(); 
                for (Field campoClase : camposClase) {
                    if(campoClase.isAnnotationPresent(Id.class)){
                        campoIdElemento = campoClase;
                        campoIdElemento.setAccessible(true);
                    }
                }
                for (TblAccesoPantallasCampos campoPantalla : configuracionElemento) {
                    if (campoPantalla.getVisibleDetalle()) {
                        componente = new CampoComponente();
                        componente.setBndCargarCatalogo(false);
                        componente.setConfiguracionCampo(campoPantalla);
                        for (Field campoClase : camposClase) {
                            if ((campoClase.isAnnotationPresent(Column.class) && ((Column) campoClase.getAnnotation(Column.class)).name().equalsIgnoreCase(campoPantalla.getNColumna().trim()))
                                    || (campoClase.isAnnotationPresent(JoinColumn.class) && ((JoinColumn) campoClase.getAnnotation(JoinColumn.class)).name().equalsIgnoreCase(campoPantalla.getNColumna().trim()))) {
                                componente.setCampoClase(campoClase);
                                break;
                            }
                        }
                        if (campoPantalla.getLlaveForaneas()) {
                            System.out.println("entrara a buscar catalogo");
                            String nombreClase = campoPantalla.getCatalogo().replaceAll("_", "");
                            System.out.println("nombreClase: " + nombreClase);
                            Class claseCatalogo = null;
                            Map<String, ClassMetadata> metadataPojos = genericDAO.getMetaDataPojos();
                            ClassMetadata metadataClassPojo;
                            for (String clasePojo : metadataPojos.keySet()) {
                                metadataClassPojo = metadataPojos.get(clasePojo);
                                if (metadataClassPojo.getMappedClass().getSimpleName().equalsIgnoreCase(nombreClase)) {
                                    claseCatalogo = metadataClassPojo.getMappedClass();
                                }
                            }
                            if (claseCatalogo != null) {
                                List<?> elementosCatalogo = genericDAO.findAll(claseCatalogo);
                                componente.setComponentesCatalogoCombo(elementosCatalogo);
                            }
                        }
                    }
                    camposClaseElemento.add(componente);
                }
            } catch (DAOException | DataBaseException | InstantiationException | IllegalAccessException ex) {
                System.out.println("error:" + ex.getMessage());
            }
        }else{
            System.out.println("no hay clase del catalogo en cuestion");
        }
    }

    public void resetDialog(String nombreFrmDialogo) {
        RequestContext.getCurrentInstance().reset(nombreFrmDialogo);
    }

    public void guardarElemento() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = null;
        try {
            genericDAO.saveOrUpdate(elemento);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Terminado", "Elemento guardado correctamente");
        } catch (DAOException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
        }
//            finally {
//                if (genericDAO != null) {
//                    genericDAO.closeDAO();
//                }
//            }               
        context.addMessage(null, message);
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

    public String getNombreAccionFrm() {
        return nombreAccionFrm;
    }

    public void setNombreAccionFrm(String nombreAccionFrm) {
        this.nombreAccionFrm = nombreAccionFrm;
    }

    public Class<?> getClaseElemento() {
        return claseElemento;
    }

    public void setClaseElemento(Class<?> claseElemento) {
        this.claseElemento = claseElemento;
    }

    public Class<?> getClaseElementoDAO() {
        return claseElementoDAO;
    }

    public void setClaseElementoDAO(Class<?> claseElementoDAO) {
        this.claseElementoDAO = claseElementoDAO;
    }

    public List<CampoComponente> getCamposClaseElemento() {
        return camposClaseElemento;
    }

    public void setCamposClaseElemento(List<CampoComponente> camposClaseElemento) {
        this.camposClaseElemento = camposClaseElemento;
    }

    public Integer getIdTablaElemento() {
        return idTablaElemento;
    }

    public void setIdTablaElemento(Integer idTablaElemento) {
        this.idTablaElemento = idTablaElemento;
    }

    public List<TblAccesoPantallasCampos> getConfiguracionElemento() {
        return configuracionElemento;
    }

    public void setConfiguracionElemento(List<TblAccesoPantallasCampos> configuracionElemento) {
        this.configuracionElemento = configuracionElemento;
    }

    public void nuevoElemento() {
        try {
            elemento = (Serializable) claseElemento.newInstance();
            permissionToWrite = true;
            idElemento = null;
            init();
            nombreAccionFrm = menuOrigen.getTitulo() + " - Agregar registro";
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(FrmGenericElementoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public Serializable getElemento() {
        return elemento;
    }

    public void setElemento(Serializable elemento) {
        this.elemento = elemento;
    }

    public TblAccesoPantallas getMenuOrigen() {
        return menuOrigen;
    }

    public void setMenuOrigen(TblAccesoPantallas menuOrigen) {
        this.menuOrigen = menuOrigen;
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

    @PreDestroy
    public void cerrarConexion() {
        if (genericDAO != null) {
            genericDAO.closeDAO();
            genericDAO = null;
        }
    }
}
