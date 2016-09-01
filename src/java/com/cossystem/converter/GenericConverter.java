package com.cossystem.converter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Id;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 * Convertidor genérico para clases de tipo entidad y que contengan un campo con
 * anotación @Id, el componente debe contener un f:selectItems, de lo contrario
 * siempre regresara nulo
 *
 * @author 
 */
@FacesConverter("genericConverter")
public class GenericConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Field[] properties;
        Method[] metodos;
        Class clase;
        String valorObtenido = "";        
        if (value != null && !"".equals(value)) {
            if (component.getClass().getName().equalsIgnoreCase(PickList.class.getName())) {
                DualListModel<Serializable> model = (DualListModel<Serializable>) ((PickList) component).getValue();
                try {
                    if (!model.getSource().isEmpty()) {
                        clase = Class.forName(model.getSource().get(0).getClass().getName().contains("_$$") ? model.getSource().get(0).getClass().getName().substring(0, model.getSource().get(0).getClass().getName().indexOf("_$$")) : model.getSource().get(0).getClass().getName());
                        metodos = clase.getMethods();
                        Object instanciaTemp = clase.newInstance();
                        properties = clase.getDeclaredFields();
                        for (Field campo : properties) {
                            campo.setAccessible(true);
                            if (campo.isAnnotationPresent(Id.class)) {
                                Class claseCampo = campo.getType();
                                Integer campoNumerico = new Integer(value);
                                campo.set(instanciaTemp, claseCampo.cast(campoNumerico));
                            }
                        }
                        for (Serializable elemento : model.getSource()) {
                            for (Field campo : properties) {
                                campo.setAccessible(true);
                                if (campo.isAnnotationPresent(Id.class)) {
                                    for (Method metodo : metodos) {
                                        if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
                                            valorObtenido = metodo.invoke(elemento).toString();
                                            break;
                                        }
                                    }
                                    if (valorObtenido.equals(value)) {
                                        return elemento;
                                    }
                                }
                            }
                        }
                    }
                    if (!model.getTarget().isEmpty()) {
                        clase = Class.forName(model.getTarget().get(0).getClass().getName().contains("_$$") ? model.getTarget().get(0).getClass().getName().substring(0, model.getTarget().get(0).getClass().getName().indexOf("_$$")) : model.getTarget().get(0).getClass().getName());
                        metodos = clase.getMethods();
                        Object instanciaTemp = clase.newInstance();
                        properties = clase.getDeclaredFields();
                        for (Field campo : properties) {
                            campo.setAccessible(true);
                            if (campo.isAnnotationPresent(Id.class)) {
                                Class claseCampo = campo.getType();
                                Integer campoNumerico = new Integer(value);
                                campo.set(instanciaTemp, claseCampo.cast(campoNumerico));
                            }
                        }
                        for (Serializable elemento : model.getTarget()) {
                            for (Field campo : properties) {
                                campo.setAccessible(true);
                                if (campo.isAnnotationPresent(Id.class)) {
                                    for (Method metodo : metodos) {
                                        if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
                                            valorObtenido = metodo.invoke(elemento).toString();
                                            break;
                                        }
                                    }
                                    if (valorObtenido.equals(value)) {
                                        return elemento;
                                    }
                                }
                            }
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(GenericConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (UIComponent object : component.getChildren()) {
                if (UISelectItems.class.getName().equalsIgnoreCase(object.getClass().getName())) {
                    UISelectItems items = (UISelectItems) object;
                    List<Object> lista = (List<Object>) items.getValue();
                    if (lista != null && !lista.isEmpty()) {
                        try {
                            clase = Class.forName(lista.get(0).getClass().getName().contains("_$$") ? lista.get(0).getClass().getName().substring(0, lista.get(0).getClass().getName().indexOf("_$$")) : lista.get(0).getClass().getName());
                            properties = clase.getDeclaredFields();
                            metodos = clase.getMethods();
                            for (Object elemento : lista) {
                                for (Field campo : properties) {
                                    campo.setAccessible(true);
                                    if (campo.isAnnotationPresent(Id.class)) {
                                        for (Method metodo : metodos) {
                                            if (metodo.getName().equalsIgnoreCase("get" + campo.getName())) {
                                                valorObtenido = metodo.invoke(elemento).toString();
                                                break;
                                            }
                                        }
                                        if (valorObtenido.equals(value)) {
                                            return elemento;
                                        }
                                    }
                                }
                            }
                        } catch (IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException ex) {                            
                            return null;
                        }                        
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        try {
            Class clase = Class.forName(value.getClass().getName().contains("_$$") ? value.getClass().getName().substring(0, value.getClass().getName().indexOf("_$$")) : value.getClass().getName());
            Field[] properties = clase.getDeclaredFields();
            Method[] metodos = clase.getMethods();
            for (Field property : properties) {
                property.setAccessible(true);
                if (property.isAnnotationPresent(Id.class)) {
                    for (Method metodo : metodos) {
                        if (metodo.getName().equalsIgnoreCase("get" + property.getName())) {
                            return metodo.invoke(value).toString();
                        }
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InvocationTargetException ex) {
            Logger.getLogger(GenericConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }
}
