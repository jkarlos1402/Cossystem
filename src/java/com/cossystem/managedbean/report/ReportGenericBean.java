package com.cossystem.managedbean.report;

import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
import com.crystaldecisions.sdk.occa.report.reportsource.IReportSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@ManagedBean
@RequestScoped
public class ReportGenericBean implements Serializable {

    private IReportSource reporte;
    private String nombreReporte = "";

    public ReportGenericBean() {

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

    public IReportSource obtieneReporte(String nombreReporte) {
        System.out.println("nombre reporte: " + nombreReporte);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map parametros = externalContext.getRequestParameterMap();
        FacesMessage message = null;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession httpSession = (HttpSession) context.getExternalContext().getSession(false);
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String contextPathResources = servletContext.getRealPath("");
            IReportSource reportSource;
            Properties propiedadesIniciales = new Properties();
            propiedadesIniciales.load(new FileInputStream(contextPathResources + File.separator + "WEB-INF" + File.separator + "initConfig.properties"));
            Properties propiedades = new Properties();
            propiedades.load(new FileInputStream(propiedadesIniciales.getProperty("configPath") + File.separator + "config.properties"));
            String rutaReporte = propiedades.getProperty("reportPath") + File.separator + nombreReporte;
            System.out.println("ruta reporte: "+rutaReporte+" : "+httpSession.getAttribute(rutaReporte));
            ReportClientDocument clientDoc = httpSession.getAttribute(rutaReporte) == null ? null : (ReportClientDocument) httpSession.getAttribute(rutaReporte);
            if (clientDoc != null) {
                System.out.println("clientDoc no es nulo");
                try {
                    clientDoc.getStatusVariables();
                } catch (ReportSDKException ex) {
                    clientDoc = null;
                }
            }else{
                System.out.println("clientDoc es nulo "+clientDoc);
            }
            if (clientDoc == null) {
                clientDoc = new ReportClientDocument();
                clientDoc.setReportAppServer(ReportClientDocument.inprocConnectionString);

                // Open report
                clientDoc.open(rutaReporte, OpenReportOptions._openAsReadOnly);

                // ****** BEGIN SET RUNTIME DATABASE CREDENTIALS ****************  
                {
                    String connectString = propiedades.getProperty("connectString");
                    String driverName = propiedades.getProperty("driverName");
                    String JNDIName = "";
                    String userName = propiedades.getProperty("usuario");
                    String password = propiedades.getProperty("password");

                    // Switch all tables on the main report and sub reports
                    CRJavaHelper.changeDataSource(clientDoc, userName, password, connectString, driverName, JNDIName);

                    // logon to database
                    CRJavaHelper.logonDataSource(clientDoc, userName, password);

                }
                // ****** END SET RUNTIME DATABASE CREDENTIALS **************** 		

                // ****** BEGIN CONNECT PARAMETERS SNIPPET ****************		
                // This option is not applicable for the report you have chosen
                // ****** END CONNECT PARAMETERS SNIPPET ****************	
                // Store the report document in session
                httpSession.setAttribute(rutaReporte, clientDoc);

            } else {

            }
            if (parametros != null) {
                for (Object param : parametros.keySet()) {
                    if (param.toString().contains("parametro-")) {
                        System.out.println("parametro recibido: " + param + " valor: " + parametros.get(param));
                        String nombreParametro = param.toString().replace("parametro-", "");
                        String valorParametro = (String) parametros.get(param);
                        CRJavaHelper.addDiscreteParameterValue(clientDoc, "", nombreParametro, valorParametro);
                    }
                }

            }
            // ****** BEGIN CONNECT CRYSTALREPORTPAGEVIEWER SNIPPET ****************  
            {
                String reportSourceSessionKey = rutaReporte + "ReportSource";
                reportSource = (IReportSource) httpSession.getAttribute(reportSourceSessionKey);
                if (reportSource == null) {

                    reportSource = clientDoc.getReportSource();
                    httpSession.setAttribute(reportSourceSessionKey, reportSource);
                }
                reporte = reportSource;
            }

        } catch (ReportSDKException | IOException | NullPointerException ex) {            
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage());
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return reporte;
    }
}
