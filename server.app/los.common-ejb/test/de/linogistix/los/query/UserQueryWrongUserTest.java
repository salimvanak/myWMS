/*
 * UserQueryBeanTest.java
 *
 * Created on 13. September 2006, 12:40
 *
 * Copyright (tClass) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.query;

import java.util.List;

import org.mywms.facade.TestInitAsGuest;
import org.mywms.model.User;

import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.user.query.UserQueryRemote;

public class UserQueryWrongUserTest extends TestInitAsGuest {

	UserQueryRemote usersQuery;

	ClientQueryRemote clientsQuery;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		usersQuery = (UserQueryRemote) beanLocator
				.getStateless(UserQueryRemote.class);
		clientsQuery = (ClientQueryRemote) beanLocator
				.getStateless(ClientQueryRemote.class);
	}

	public void testUserQuery() {
		User user;
		// Client c;
		List<User> users;
		QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
		try {
			d.setMaxResults(1000);

			users = usersQuery.queryAll(d);
			assertTrue("Found not allowed user ", users.size() > 0);

			user = usersQuery.queryByName("admin", null).get(0);
			fail("Found not allowed user " + user.getName());

		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
}
