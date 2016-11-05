/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.empleado;

import com.cossystem.core.pojos.TblEmpleados;
import com.cossystem.core.pojos.TblEmpleadosFotos;
import com.cossystem.core.pojos.TblEmpresa;
import com.cossystem.managedbean.FrmGenericElementoBean;
import com.cossystem.managedbean.PrincipalBean;
import com.cossystem.util.Propiedades;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@ViewScoped
public class FrmEmpleadoBean extends FrmGenericElementoBean implements Serializable {

    @ManagedProperty(value = "#{principalBean}")
    private PrincipalBean principalBean;

    private TblEmpleados empleado;

    public FrmEmpleadoBean() {
        super();
    }

    @Override
    public void init() {
        super.init();
        if (elemento != null) {
            Field[] camposElemento = elemento.getClass().getDeclaredFields();
            if (principalBean != null) {
                usuarioSesion = principalBean.getUsuarioSesion();
                empresa = usuarioSesion.getIdEmpresa();
            }
            if (idElemento == null) {
                try {
                    for (Field campo : camposElemento) {
                        if (campo.getType().getSimpleName().equals(TblEmpresa.class.getSimpleName())) {
                            campo.setAccessible(true);
                            campo.set(elemento, empresa);
                            break;
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(FrmEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            empleado = (TblEmpleados) elemento;
        }
        System.out.println("permision to write: "+permissionToWrite);
    }

    public void subirFoto(FileUploadEvent event) {
        FacesMessage message = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Properties propiedades = Propiedades.obtienePropiedades();
            String rutaFotos = (String) propiedades.get("fotosPath");
            String nombreCarpetaFotos = campoIdElemento.get(elemento).toString();
            File dirFotos = new File(rutaFotos + File.separator + nombreCarpetaFotos);
            if (!dirFotos.isDirectory()) {
                dirFotos.mkdirs();
            }
            String ext = getExtension(event.getFile().getFileName());
            String nombreFoto = "foto-" + campoIdElemento.get(elemento).toString() + "-" + Calendar.getInstance().getTime().getTime() + "." + ext;
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
        } catch (IllegalArgumentException | IllegalAccessException ex) {
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

    public void selectPrincipal(int index) {
        if (empleado.getTblEmpleadosFotosList() != null && empleado.getTblEmpleadosFotosList().size() > index) {
            for (int i = 0; i < empleado.getTblEmpleadosFotosList().size(); i++) {
                if (i != index) {
                    empleado.getTblEmpleadosFotosList().get(i).setPrincipal(false);
                }
            }
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

    public PrincipalBean getPrincipalBean() {
        return principalBean;
    }

    public void setPrincipalBean(PrincipalBean principalBean) {
        this.principalBean = principalBean;
    }

    public TblEmpleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(TblEmpleados empleado) {
        this.empleado = empleado;
    }

}
