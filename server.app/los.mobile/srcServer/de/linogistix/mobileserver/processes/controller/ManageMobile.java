/*
 * Copyright (c) 2010-2013 LinogistiX GmbH
 *
 * www.linogistix.com
 *
 * Project: myWMS-LOS
*/
package de.linogistix.mobileserver.processes.controller;

import java.util.Comparator;
import java.util.List;

import javax.ejb.Local;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.mobileserver.processes.picking.PickingMobilePos;

@Local
public interface ManageMobile {
	public enum OnPickCompleteBehaviour { Print_Label, Scan_Label, Scan_Target_Location};

	public List<MobileFunction> getFunctions();

	public int getMenuPageSize();

	public Comparator<PickingMobilePos> getPickingComparator();

	public String getPickingSelectionText(LOSPickingOrder pickingOrder);

	public OnPickCompleteBehaviour getOnPickCompleteBehaviour();
}
