/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.globals.AreaName;
import org.mywms.model.Area;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.AreaService;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.RoleService;
import org.mywms.service.UserService;

/**
 * This implementation can be called by any user. SanityCheck checks for
 * the existence of predefined Entities in the database. If not found,
 * the entities will be created on the fly. Implementations, based on
 * the myWMS framework may add additional checks to their own sanity
 * check. But the SanityCheck provided here is intended to persist in
 * any further application.
 * 
 * @see org.mywms.facade.SanityCheck
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
@Stateless
@PermitAll
public class SanityCheckBean
    implements SanityCheck
{
    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(SanityCheckBean.class.getName());

    @EJB
    private RoleService roleService;

    @EJB
    private UserService userService;

    @EJB
    private ClientService clientService;

    @EJB
    private AreaService areaService;

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

    /**
     * @see org.mywms.facade.SanityCheck#check()
     */
    public String check() {
        StringBuffer strb = new StringBuffer();

        // check existance of system client
        try {
            strb.append("System client:\n");
            Client systemClient;
            systemClient = clientService.getSystemClient();

            if (systemClient != null) {
                strb.append("the system client's name is: ").append(
                    systemClient.getName());
                strb.append("\nOK\n\n");
            }
            else {
                strb.append("Failure in checking System Client").append(
                    "\nFAILURE\n\n");

                strb.append("...aborting sanity check - unrecoverable error");
                return strb.toString();
            }
        }
        catch (Exception ex) {
            strb.append("Failure in checking System Client: ").append(
                ex.getMessage()).append("\nFAILURE\n\n");

            strb.append("...aborting sanity check - unrecoverable error");
            ex.printStackTrace();
            return strb.toString();
        }

        // check existance of roles
        try {
            strb.append("Roles:\n");
            for (org.mywms.globals.Role role: org.mywms.globals.Role.values()) {
                try {
                    roleService.getByName(role.toString());
                }
                catch (EntityNotFoundException ex) {
                    strb.append("creating missing role '").append(
                        role.toString()).append("'\n");

                    roleService.create(role.toString());
                }
            }
            strb.append("OK\n\n");
        }
        catch (Exception ex) {
            strb.append("Failure in checking Roles: ")
                .append(ex.getMessage())
                .append("\nFAILURE\n\n");
            ex.printStackTrace();
        }

        // check existence of at least the admin user
        try {
            strb.append("Admin User:\n");
            org.mywms.model.Role adminRole =
                roleService.getByName(org.mywms.globals.Role.ADMIN.toString());
            try {
                User user = userService.getByUsername("System Admin");
                if (!user.getRoles().contains(adminRole)) {
                    strb.append("adding missing admin role to 'admin'\n");
                    user.getRoles().add(adminRole);
                }
            }
            catch (EntityNotFoundException ex) {
                strb.append("creating missing admin user '")
                    .append("admin")
                    .append("'\n");
                org.mywms.model.User newUser;
                newUser =
                    userService.create(
                        clientService.getSystemClient(),
                        "System Admin");

                newUser.setEmail("root@localhost");
                newUser.setFirstname("-");
                newUser.setLastname("-");
                newUser.setLocale("EN");
                newUser.setPassword("myWMS");
                newUser.setPhone("-");

                newUser.getRoles().add(adminRole);
            }
            strb.append("OK\n\n");
        }
        catch (Exception ex) {
            strb.append("Failure in checking for admin user: ").append(
                ex.getMessage()).append("\nFAILURE\n\n");
            ex.printStackTrace();
        }

        // check existance of at least the guest user
        try {
            strb.append("Guest User:\n");
            try {
                userService.getByUsername("guest");
            }
            catch (EntityNotFoundException ex) {
                strb.append("creating missing guest user '")
                    .append("guest")
                    .append("'\n");
                org.mywms.model.User newUser;
                newUser =
                    userService.create(clientService.getSystemClient(), "guest");

                newUser.setEmail("anonymous@localhost");
                newUser.setFirstname("Jon");
                newUser.setLastname("Doe");
                newUser.setLocale("EN");
                newUser.setPassword("guest");
                newUser.setPhone("-");
                newUser.getRoles().add(
                    roleService.getByName(org.mywms.globals.Role.GUEST_STR));
            }
            strb.append("OK\n\n");
        }
        catch (Exception ex) {
            strb.append("Failure in checking for guest user: ").append(
                ex.getMessage()).append("\nFAILURE\n\n");
            ex.printStackTrace();
        }

        // check existance of the areas
        try {
            strb.append("Areas:\n");
            List<Area> areas =
                areaService.getChronologicalList(clientService.getSystemClient());

            if (areas.size() == 0) {
                strb.append("creating default areas:\n");

                for (AreaName areaName: AreaName.values()) {
                    areaService.create(
                        clientService.getSystemClient(),
                        areaName.toString());
                    strb.append(" - ").append(areaName.toString());
                }
            }
            strb.append("OK\n\n");
        }
        catch (Exception ex) {
            strb.append("Failure in checking for areas: ").append(
                ex.getMessage()).append("\nFAILURE\n\n");
            ex.printStackTrace();
        }

        return strb.toString();
    }
}
