/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.ArrayList;
import java.util.List;

import org.mywms.model.Role;

/**
 * Transfers the data of a Client.
 * 
 * @author Olaf Krause
 * @version $Revision: 479 $ provided by $Author: okrause $
 */
public class UserTO
    extends BasicTO
{
    private static final long serialVersionUID = 1L;

    public String firstname;
    public String lastname;
    public String email;
    public String phone;
    public String password;

    public String name;

    public String locale;
    
    public int lock;

    public String[] roles;

    /**
     * Creates a new UserTO, using the data of the given User entity.
     * 
     * @param user the origin of the data to be transfered
     */
    public UserTO(org.mywms.model.User user) {
        super(user);

        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        // this.password = user.getPassword();
        this.name = user.getName();
        this.locale = user.getLocale();
        this.lock = user.getLock();

        List<Role> roleList = user.getRoles();
        int n = roleList.size();
        this.roles = new String[n];
        for (int i = 0; i < n; i++) {
            this.roles[i] = roleList.get(i).getName();
        }
    }

    /**
     * @see org.mywms.facade.BasicTO#merge(org.mywms.model.BasicEntity)
     * @param user the user to be merged
     * @param allRoles a list of all roles, the user can be assigned to
     * @throws VersionException
     */
    public void merge(
        org.mywms.model.User user,
        List<org.mywms.model.Role> allRoles) throws VersionException
    {
        super.merge(user);

        user.setFirstname(this.firstname);
        user.setLastname(this.lastname);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        if (this.password != null && this.password.length() > 0) {
            user.setPassword(this.password);
        }
        user.setName(this.name);
        user.setLocale(this.locale);
        user.setLock(this.lock);

        List<org.mywms.model.Role> newRoles =
            new ArrayList<org.mywms.model.Role>();
        for (org.mywms.model.Role allRole: allRoles) {
            for (String role: roles) {
                if (allRole.getName().equals(role)) {
                    newRoles.add(allRole);
                }
            }
        }
        user.setRoles(newRoles);
    }
}
