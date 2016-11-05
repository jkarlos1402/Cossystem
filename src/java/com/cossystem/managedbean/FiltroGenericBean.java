package com.cossystem.managedbean;

import com.cossystem.core.exception.CossException;
import com.cossystem.core.pojos.TblAccesoPantallasCampos;
import com.cossystem.core.util.Configuracion;
import com.cossystem.core.util.Filtro;
import com.sun.faces.facelets.tag.IterationStatus;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.primefaces.context.RequestContext;

public class FiltroGenericBean {

    protected List<SelectItem> componentesComboCampos = new ArrayList<>();
    protected Class claseEntidad;
    protected Class claseEntidadTransaccional;
    protected Integer idClaseEntidad;
    protected List<TblAccesoPantallasCampos> configuracion;
    protected List<TblAccesoPantallasCampos> configuracionTransaccional;
    protected List<Filtro> filtros;
    protected List<Filtro> filtrosTransaccional;

    public FiltroGenericBean() {
        init();
    }

    public void init() {
    }

    public List<SelectItem> getComponentesComboCampos() {
        System.out.println("clase entidad: " + claseEntidad.getSimpleName());
        if (claseEntidad != null && componentesComboCampos.isEmpty()) {
            String nombreTabla = ((Table) claseEntidad.getAnnotation(Table.class)).name();
            Field[] camposEntidad = claseEntidad.getDeclaredFields();
            String nombreCampoClase;
            Field campoClase;
            for (TblAccesoPantallasCampos config : configuracion) {
                if (config.getNTabla().equals(nombreTabla) && config.getVisibleDetalle()) {
                    for (Field field : camposEntidad) {
                        if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class)) {
                            nombreCampoClase = field.isAnnotationPresent(Column.class) ? ((Column) field.getAnnotation(Column.class)).name() : ((JoinColumn) field.getAnnotation(JoinColumn.class)).name();
                            campoClase = field;
                            if (nombreCampoClase.equals(config.getNColumna())) {
                                componentesComboCampos.add(new SelectItem(campoClase.getName(), config.getDescripcion()));
                                break;
                            }
                        }
                    }

                }
            }
        }
        return componentesComboCampos;
    }

    public void setComponentesComboCampos(List<SelectItem> componentesComboCampos) {
        this.componentesComboCampos = componentesComboCampos;
    }

    public Class getClaseEntidad() {
        return claseEntidad;
    }

    public void setClaseEntidad(Class claseEntidad) {
        this.claseEntidad = claseEntidad;
        try {
            configuracion = Configuracion.obtieneConfiguracion(idClaseEntidad);
        } catch (CossException ex) {
            configuracion = null;
        }
        componentesComboCampos.clear();
    }

    public List<TblAccesoPantallasCampos> getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(List<TblAccesoPantallasCampos> configuracion) {
        this.configuracion = configuracion;
    }

    public List<Filtro> getFiltros() {        
        return filtros;
    }

    public void setFiltros(List<Filtro> filtros) {
        this.filtros = filtros;
    }

    public void agregarFiltro(IterationStatus status) {
        if (filtros.get(status.getIndex()).getAgregar() != null && !"".equals(filtros.get(status.getIndex()).getAgregar()) && status.getIndex() == filtros.size() - 1) {
            filtros.add(new Filtro());
        }
    }

    public void eliminarFiltro(IterationStatus status, String nombreForm) {        
        int contadorVisible = 0;
        if (filtros != null && status.getIndex() <= filtros.size() - 1 && filtros.get(status.getIndex()).isVisible()) {
            for (Filtro filtro : filtros) {
                if (filtro.isVisible()) {
                    contadorVisible++;
                }
            }
            if (contadorVisible > 1) {
                filtros.remove(status.getIndex());
            } else {
                filtros.set(status.getIndex(), new Filtro());
            }
        }
        RequestContext.getCurrentInstance().reset(nombreForm);
    }

    public void selecionarCampo(IterationStatus status) {
        if (claseEntidad != null) {
            Field[] camposClase = claseEntidad.getDeclaredFields();
            Filtro filtro = filtros.get(status.getIndex());
            filtro.setComparador(null);
            for (Field campo : camposClase) {
                if (filtro.getNombreCampoClase().equals(campo.getName())) {
                    filtro.setCampoEntidad(campo);
                    String nombreCampoTabla = campo.isAnnotationPresent(Column.class) ? ((Column) campo.getAnnotation(Column.class)).name() : campo.isAnnotationPresent(JoinColumn.class) ? ((JoinColumn) campo.getAnnotation(JoinColumn.class)).name() : null;
                    for (TblAccesoPantallasCampos conf : configuracion) {
                        if (nombreCampoTabla != null && conf.getNColumna().equals(nombreCampoTabla)) {
                            filtro.setConfCampo(conf);
                            filtro.setDescCampo(conf.getDescripcion());
                            break;
                        }
                    }
                    if (String.class.getSimpleName().equals(campo.getType().getSimpleName())) {
                        filtro.setCampoString(true);
                        filtro.setCampoFecha(false);
                        filtro.setCampoNumerico(false);
                        filtro.setCampoCatalogo(false);
                        filtro.setCampoBooleano(false);
                    } else if (Date.class.getSimpleName().equals(campo.getType().getSimpleName())) {
                        filtro.setCampoString(false);
                        filtro.setCampoFecha(true);
                        filtro.setCampoNumerico(false);
                        filtro.setCampoCatalogo(false);
                        filtro.setCampoBooleano(false);
                    } else if (Number.class.isAssignableFrom(campo.getType())) {
                        filtro.setCampoString(false);
                        filtro.setCampoFecha(false);
                        filtro.setCampoNumerico(true);
                        filtro.setCampoCatalogo(false);
                        filtro.setCampoBooleano(false);
                    } else if (Boolean.class.getSimpleName().equals(campo.getType().getSimpleName())) {
                        filtro.setCampoString(false);
                        filtro.setCampoFecha(false);
                        filtro.setCampoNumerico(false);
                        filtro.setCampoCatalogo(false);
                        filtro.setCampoBooleano(true);
                    } else if (campo.isAnnotationPresent(ManyToOne.class)) {
                        filtro.setCampoString(false);
                        filtro.setCampoFecha(false);
                        filtro.setCampoNumerico(false);
                        filtro.setCampoCatalogo(true);
                        filtro.setCampoBooleano(false);
                    }
                    break;
                }
            }
        }
    }

    public Integer getIdClaseEntidad() {
        return idClaseEntidad;
    }

    public void setIdClaseEntidad(Integer idClaseEntidad) {
        this.idClaseEntidad = idClaseEntidad;
    }

    public void selecionarComparador(IterationStatus status) {
        Filtro filtro = filtros.get(status.getIndex());
        if ("entre".equals(filtro.getComparador())) {
            filtro.setIntervalo(true);
        } else {
            filtro.setIntervalo(false);
        }
    }

    public Class getClaseEntidadTransaccional() {
        return claseEntidadTransaccional;
    }

    public void setClaseEntidadTransaccional(Class claseEntidadTransaccional) {
        this.claseEntidadTransaccional = claseEntidadTransaccional;
    }

    public List<TblAccesoPantallasCampos> getConfiguracionTransaccional() {
        return configuracionTransaccional;
    }

    public void setConfiguracionTransaccional(List<TblAccesoPantallasCampos> configuracionTransaccional) {
        this.configuracionTransaccional = configuracionTransaccional;
    }

    public List<Filtro> getFiltrosTransaccional() {
        return filtrosTransaccional;
    }

    public void setFiltrosTransaccional(List<Filtro> filtrosTransaccional) {
        this.filtrosTransaccional = filtrosTransaccional;
    }

    public void cerrarDialogoFiltro(String nombreDialogo) {
        RequestContext.getCurrentInstance().execute("PF('" + nombreDialogo + "').hide()");
    }
}
