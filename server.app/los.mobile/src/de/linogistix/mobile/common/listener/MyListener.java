/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.listener;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author artur
 */
public class MyListener implements PhaseListener {
	
	private static final long serialVersionUID = 1L;

	public void beforePhase(PhaseEvent e) {
        System.out.println("MyListener works xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        if (e.getPhaseId() == PhaseId.RESTORE_VIEW) {
            if (e.getFacesContext().getExternalContext().getSession(false) == null) {
                e.getFacesContext().getApplication().getNavigationHandler().handleNavigation(
                        e.getFacesContext(), "", "sessionExpired");
            }
        }
    }

    public void afterPhase(PhaseEvent arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
