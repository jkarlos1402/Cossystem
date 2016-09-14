package com.cossystem.managedbean.filtro;

import com.cossystem.core.pojos.accesopantallas.TblAccesoPantallasCampos;
import com.cossystem.core.util.Configuracion;
import com.cossystem.core.util.Filtro;
import com.sun.faces.facelets.tag.IterationStatus;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

public class FiltroEntidad implements Serializable {

    private List<SelectItem> componentesComboCampos = new ArrayList<>();
    private Class claseEntidad;
    List<TblAccesoPantallasCampos> configuracion;
    List<Filtro> filtros;

    public FiltroEntidad() {
        init();
    }

    public void init() {
        filtros = new ArrayList<>();
        filtros.add(new Filtro());
    }

    public List<SelectItem> getComponentesComboCampos() {
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
        configuracion = Configuracion.obtieneConfiguracion(this.claseEntidad);
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

    public void eliminarFiltro(IterationStatus status) {
        filtros.remove(status.getIndex());
    }

    public void selecionarCampo(IterationStatus status) {
        if (claseEntidad != null) {
            Field[] camposClase = claseEntidad.getDeclaredFields();
            Filtro filtro = filtros.get(status.getIndex());
            filtro.setComparador(null);
            for (Field campo : camposClase) {
                if (filtro.getNombreCampoClase().equals(campo.getName())) {
                    filtro.setCampoEntidad(campo);
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

    public void selecionarComparador(IterationStatus status) {
        Filtro filtro = filtros.get(status.getIndex());
        if ("entre".equals(filtro.getComparador())) {
            filtro.setIntervalo(true);
        } else {
            filtro.setIntervalo(false);
        }
    }
}
