/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.listener;

import com.cossystem.core.dao.GenericDAO;
import com.cossystem.core.exception.DAOException;
import com.cossystem.core.exception.DataBaseException;
import com.cossystem.core.pojos.catalogos.TblConfiguracionCossAdmin;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Web application lifecycle listener.
 *
 * @author JC
 */
public class MonitoreoAtributosSesion implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if ("session_user".equals(event.getName())) {
            GenericDAO genericDAO = null;
            try {
                genericDAO = new GenericDAO();
                List<TblConfiguracionCossAdmin> configuracionTablas = genericDAO.findAll(TblConfiguracionCossAdmin.class);
                event.getSession().setAttribute("configuracionCosAdmin", configuracionTablas);
            } catch (DataBaseException | DAOException ex) {
                Logger.getLogger(MonitoreoSesion.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (genericDAO != null) {
                    genericDAO.closeDAO();
                }
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        //to do
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
