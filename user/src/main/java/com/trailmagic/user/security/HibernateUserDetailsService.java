/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.user.security;

import com.trailmagic.user.Group;
import com.trailmagic.user.GroupFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

public class HibernateUserDetailsService implements UserDetailsService {
    private UserFactory m_userFactory;
    private GroupFactory m_groupFactory;

    public void setUserFactory(UserFactory factory) {
        m_userFactory = factory;
    }

    public void setGroupFactory(GroupFactory groupFactory) {
        m_groupFactory = groupFactory;
    }

    /**
     * Locates the user based on the username. In the actual implementation,
     * the search may possibly be case insensitive, or case insensitive
     * depending on how the implementaion instance is configured. In this
     * case, the <code>UserDetails</code> object that comes back may have a
     * username that is of a different case than what was actually requested..
     *
     * @param username the username presented to the {@link
     *        DaoAuthenticationProvider}
     *
     * @return a fully populated user record (never <code>null</code>)
     *
     * @throws UsernameNotFoundException if the user could not be found or the
     *         user has no GrantedAuthority
     * @throws DataAccessException if user could not be found for a
     *         repository-specific reason
     */
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException, DataAccessException {

        User user = m_userFactory.getByScreenName(username);
        if (user == null) {
            throw new UsernameNotFoundException("No such user");
        }
        
        // add a ROLE_<GROUPNAME> for each group the user's in
        List<Group> groups = m_groupFactory.getForUser(user);
        Collection<GrantedAuthority> authorities =
            new ArrayList<GrantedAuthority>();
        for (Group group : groups) {
            authorities
                .add(new GrantedAuthorityImpl("ROLE_"
                                              + group.getName().toUpperCase()));
        }
        // add ROLE_USER for every user
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        
        // TODO: this doesn't really make sense, since I think this
        // should be included by virtue of including the user itself,
        // but that doens't seem to work and I want to move on, so
        // just adding the username directly
        authorities.add(new GrantedAuthorityImpl(user.getScreenName()));
        
        return new ToolkitUserDetails(user,
                                      authorities.toArray(new GrantedAuthority[0]));
    }
}