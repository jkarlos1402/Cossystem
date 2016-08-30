/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cossystem.listener;

import com.cossystem.core.dao.GenericDAO;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 *
 * @author JC
 */
public class MonitoreoSesion implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //to do
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        GenericDAO genericDAO = (GenericDAO) se.getSession().getAttribute("genericdao_user");
        if (genericDAO != null) {
            genericDAO.closeDAO();
        }
    }
}
