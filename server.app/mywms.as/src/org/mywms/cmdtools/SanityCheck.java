/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.cmdtools;

import org.mywms.ejb.BeanLocator;

/**
 * The SanityCheck calls the apropriate method on the application
 * server.
 * 
 * @see org.mywms.facade.SanityCheck
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class SanityCheck {

    /**
     * This method connects to the application server and calls the
     * sanity check.
     * 
     * @param args the arguments forwarded from the command line; not in
     *            use here
     */
    public static void main(String[] args) {
        System.out.println("# launching sanity check");
        System.out.println("# creating bean locator");
        BeanLocator beanLocator = new BeanLocator();
        System.out.println("# locate remote bean on application server");
        org.mywms.facade.SanityCheck sanityCheck =
            beanLocator.getStateless(org.mywms.facade.SanityCheck.class);
        System.out.println("# do the check");
        String response = sanityCheck.check();
        System.out.println("# sanity check returned successfully:");
        System.out.println(response);
        System.out.println("# terminating sanity check");
    }

}
