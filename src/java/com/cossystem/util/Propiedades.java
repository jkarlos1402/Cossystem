/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author JC
 */
public class Propiedades {

    public static Properties obtienePropiedades() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        String contextPathResources = servletContext.getRealPath("");
        Properties propiedadesIniciales = new Properties();
        propiedadesIniciales.load(new FileInputStream(contextPathResources + File.separator + "WEB-INF" + File.separator + "initConfig.properties"));
        Properties propiedades = new Properties();
        propiedades.load(new FileInputStream(propiedadesIniciales.getProperty("configPath") + File.separator + "config.properties"));
        return propiedades;
    }
}
