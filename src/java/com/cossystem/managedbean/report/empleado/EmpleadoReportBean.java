/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.managedbean.report.empleado;

import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
import com.crystaldecisions.sdk.occa.report.reportsource.IReportSource;
import java.io.File;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author JC
 */
@ManagedBean
@ViewScoped
public class EmpleadoReportBean implements Serializable {

    private IReportSource reporte;
    private String nombreReporte = "";

    public EmpleadoReportBean() {
        try {
            Object reportSource = null;
            String absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            File webRoot = new File(absoluteWebPath);
            System.out.println(webRoot.getAbsolutePath());
            String rutaReporte = webRoot.getAbsolutePath() + File.separator + "reports" + File.separator + "rpt_tbl_Empleados_DiarioActividadDet.rpt";
            System.out.println("ruta: " + rutaReporte);
            ReportClientDocument oReportClientDocument = new ReportClientDocument();
            System.out.println("pase 1");
            oReportClientDocument.open(rutaReporte, 1);
            System.out.println("pase 3");
            reportSource = oReportClientDocument.getReportSource();
            System.out.println("ya paso");
            oReportClientDocument.close();
            reporte = (IReportSource) reportSource;
        } catch (ReportSDKException ex) {
            ex.printStackTrace();
        }
    }

    public IReportSource getReporte() {

        return reporte;
    }

    public void setReporte(IReportSource reporte) {
        this.reporte = reporte;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

}
