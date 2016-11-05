/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.pojos.CatUsuarios;
import com.cossystem.util.Propiedades;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author JC
 */
@ManagedBean
@RequestScoped
public class ImagenBean implements Serializable {

    private List<StreamedContent> imagenes = new ArrayList<>();
    private List<String> nombreImagenes = new ArrayList<>();

    public List<StreamedContent> getImagenes() {
        return imagenes;
    }

    public void setImagen(List<StreamedContent> imagenes) {
        this.imagenes = imagenes;
    }

    public StreamedContent getImagen() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map parametros = externalContext.getRequestParameterMap();
        StreamedContent defaultContent = null;
        Integer indexFoto = 0;
        String nomFoto = null;
        try {
            Properties propiedades = Propiedades.obtienePropiedades();
            String nomCarpeta = parametros.containsKey("nomCarpeta") ? (String) parametros.get("nomCarpeta") : null;
            indexFoto = parametros.containsKey("indexFoto") ? Integer.parseInt((String) parametros.get("indexFoto")) : -2;
            nomFoto = parametros.containsKey("nomFoto") ? (String) parametros.get("nomFoto") : null;
            File archivoImagen = null;
            if (nomCarpeta != null && nomFoto != null) {
                archivoImagen = new File(propiedades.getProperty("fotosPath") + File.separator + nomCarpeta + File.separator + nomFoto);
            }
            StreamedContent content = null;
            defaultContent = new DefaultStreamedContent(new FileInputStream(propiedades.getProperty("fotosPath") + File.separator + "nina-icono.png"), "image/jpg");
            if (archivoImagen != null && archivoImagen.isFile()) {
                content = new DefaultStreamedContent(new FileInputStream(propiedades.getProperty("fotosPath") + File.separator + nomCarpeta + File.separator + nomFoto), "image/jpg");
            }
            if (indexFoto >= 0 && imagenes.size() - 1 < indexFoto) {
                imagenes.add(content);
                nombreImagenes.add(nomFoto);
            } else if (content != null) {
                imagenes.set(indexFoto, content);
                nombreImagenes.set(indexFoto, nomFoto);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
        for (int i = 0; i < nombreImagenes.size(); i++) {
            if (nombreImagenes.get(i).equals(nomFoto)) {
                return imagenes.get(i) != null ? imagenes.get(i) : defaultContent;
            }
        }
        return defaultContent;
    }
}
