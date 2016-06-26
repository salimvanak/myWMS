/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cactustest;

import javax.naming.InitialContext;

import org.apache.cactus.ServletTestCase;
import org.mywms.facade.SanityCheck;
import org.mywms.globals.AreaName;
import org.mywms.model.Area;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.plugin.PluginFactory;
import org.mywms.service.AreaService;
import org.mywms.service.ClearingItemService;
import org.mywms.service.ClientService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.ItemDataService;
import org.mywms.service.LotService;
import org.mywms.service.PluginConfigurationService;
import org.mywms.service.RoleService;
import org.mywms.service.StockUnitService;
import org.mywms.service.UnitLoadService;
import org.mywms.service.UnitLoadTypeService;
import org.mywms.service.UserService;
import org.mywms.service.ZoneService;

/**
 * Tests the apropriate fassade implementation.
 * 
 * @author Markus Jordan
 * @version $Revision: 602 $ provided by $Author: mkrane $
 */
public class CactusTestInit
    extends ServletTestCase
{

    protected InitialContext context;

    private SanityCheck sanityCheck;

    protected ClientService clientService;

    protected UserService userService;

    protected ClearingItemService clearingItemService;

    protected RoleService roleService;

    protected ItemDataService itemDataService;

    protected UnitLoadService unitLoadService;

    protected UnitLoadTypeService unitLoadTypeService;

    protected AreaService areaService;

    protected StockUnitService stockUnitService;

    protected PluginConfigurationService pluginConfigurationService;

    protected ZoneService zoneService;

    protected LotService lotService;

    protected PluginFactory pluginFactory;

    protected Client client1 = null, client2 = null;

    protected User user1 = null, user2 = null, user3 = null;

    protected ItemData item1 = null, item2 = null, item3 = null;

    protected UnitLoadType ulType1 = null, ulType2 = null, ulType3 = null;

    protected Area a1 = null, a2 = null, a3 = null;

    protected UnitLoad ul1 = null, ul2 = null, ul3 = null, ul4 = null;

    public void setUp() throws Exception {
        super.setUp();

        try {
            context = new InitialContext();

            sanityCheck =
                    (SanityCheck) context
                            .lookup("cactustest/SanityCheckBean/remote");
            sanityCheck.check();

            clientService =
                    (ClientService) context
                            .lookup("cactustest/ClientServiceBean/local");

            client1 = clientService.create("FirstTestClient", "t-1-1", "t/1/1");
            assertNotNull(client1);
            client2 = clientService.create("SecondTestClient", "t-2-2", "t/2/2");
            assertNotNull(client2);

            userService =
                    (UserService) context
                            .lookup("cactustest/UserServiceBean/local");

            user1 = userService.create(client1, "mywms");
            user2 = userService.create(client1, "jboss");
            user3 = userService.create(client2, "postgres");

            roleService =
                    (RoleService) context
                            .lookup("cactustest/RoleServiceBean/local");

            clearingItemService =
                    (ClearingItemService) context
                            .lookup("cactustest/ClearingItemServiceBean/local");
            itemDataService =
                    (ItemDataService) context
                            .lookup("cactustest/ItemDataServiceBean/local");
            item1 = itemDataService.create(client1, "I-c1-1");
            item2 = itemDataService.create(client1, "I-c1-2");
            item3 = itemDataService.create(client2, "I-c2-1");

            unitLoadTypeService =
                    (UnitLoadTypeService) context
                            .lookup("cactustest/UnitLoadTypeServiceBean/local");
            ulType1 = unitLoadTypeService.create("Palette");
            ulType2 = unitLoadTypeService.create("Rolli");
            ulType3 = unitLoadTypeService.create("Box");

            areaService =
                    (AreaService) context
                            .lookup("cactustest/AreaServiceBean/local");
            a1 = areaService.create(client1, AreaName.GOODS_IN_STR);
            a2 = areaService.create(client2, AreaName.GOODS_IN_STR);
            a3 = areaService.create(client1, AreaName.PICKING_STR);

            stockUnitService =
                    (StockUnitService) context
                            .lookup("cactustest/StockUnitServiceBean/local");
            
            zoneService =
                    (ZoneService) context
                            .lookup("cactustest/ZoneServiceBean/local");

            pluginConfigurationService =
                    (PluginConfigurationService) context
                            .lookup("cactustest/PluginConfigurationServiceBean/local");

            pluginConfigurationService.setLookupPattern("cactustest/?/local");

            pluginFactory =
                    (PluginFactory) context
                            .lookup("cactustest/PluginFactoryBean/local");

            lotService =
                    (LotService) context
                            .lookup("cactustest/LotServiceBean/local");

        }
        catch (Exception ex) {
            removeTestTopology();
            throw new Exception(ex);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        removeTestTopology();
    }

    private void removeTestTopology() throws ConstraintViolatedException {
        pluginConfigurationService.deleteAll();
        
        stockUnitService.deleteAll();
        unitLoadService.deleteAll();
       
        areaService.deleteAll();
        unitLoadTypeService.deleteAll();
        itemDataService.deleteAll();
        zoneService.deleteAll();
        userService.delete(user1);
        userService.delete(user2);
        userService.delete(user3);
        clientService.delete(client1);
        clientService.delete(client2);
    }
}
