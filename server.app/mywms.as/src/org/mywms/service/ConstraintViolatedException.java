/**
 * 
 */
package org.mywms.service;

import org.mywms.globals.ServiceExceptionKey;

/**
 * ConstraintViolatedException is thrown, if a constraint is violated.
 * 
 * @author Olaf Krause
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ConstraintViolatedException
    extends ServiceException
{
    private static final long serialVersionUID = 1L;

    public ConstraintViolatedException(ServiceExceptionKey messageKey) {
        super(messageKey);
    }
}
