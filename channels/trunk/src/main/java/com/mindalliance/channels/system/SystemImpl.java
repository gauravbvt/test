// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.acegisecurity.Authentication;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.project.ProjectImpl;
import com.mindalliance.channels.util.AbstractJavaBean;

import static com.mindalliance.channels.system.UserTypes.AdminType;
import static com.mindalliance.channels.system.UserTypes.UserType;

/**
 * Basic implementation of the system object.
 * This requires both implementations of Project and User to
 * implement JavaBean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @composed - - * SystemObject
 * @composed - - * Alert
 */
public class SystemImpl extends AbstractJavaBean
        implements System, UserDetailsService {

    private static final String NAME_PROPERTY = "name";

    private Map<User,UserTypes> userRight = new HashMap<User,UserTypes>();
    private Map<String,User> usernames = new HashMap<String,User>();
    private Map<String,Project> projects = new HashMap<String,Project>();
    private Set<Organization> organizations = new TreeSet<Organization>();
    private Set<Alert> alerts = new HashSet<Alert>();

    /**
     * Listen and approve project name changes to ensure uniqueness.
     */
    private VetoableChangeListener projectNameListener =
        new VetoableChangeListener() {
            public void vetoableChange( PropertyChangeEvent evt )
                throws PropertyVetoException {

                Map<String, Project> map = SystemImpl.this.projects;
                if ( map.containsKey( evt.getNewValue() ) )
                    throw new PropertyVetoException(
                            MessageFormat.format(
                                    "Project named {0} already exists",
                                    evt.getNewValue() ),
                            evt );
                else {
                    // Reindex
                    Project project = map.get( evt.getOldValue() );
                    map.remove( evt.getOldValue() );
                    map.put( (String) evt.getNewValue(), project );
                }
            }
        };

    /**
     * Listen and approve username changes to ensure uniqueness.
     */
    private VetoableChangeListener usernameListener =
            new VetoableChangeListener() {
                public void vetoableChange( PropertyChangeEvent evt )
                    throws PropertyVetoException {

                    Map<String, User> map = SystemImpl.this.usernames;
                    if ( map.containsKey( evt.getNewValue() ) )
                        throw new PropertyVetoException(
                                MessageFormat.format(
                                        "User named {0} already exists",
                                        evt.getNewValue() ),
                                evt );
                    else {
                        // Reindex
                        User user = map.get( evt.getOldValue() );
                        map.remove( evt.getOldValue() );
                        map.put( (String) evt.getNewValue(), user );
                    }
                }
            };

    /**
     * Default bean constructor.
     */
    public SystemImpl() {
        super();
    }

    /**
     * Return the value of projects.
     * <b>Note:</b> This is filtered by the current user's permissions.
     */
    @Secured( { "ROLE_USER" } )
    public Set<Project> getProjects() {
        User user = null ;
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null ) {
            Authentication authentication = context.getAuthentication();
            if ( authentication != null )
                user = (User) authentication.getPrincipal();
        }

        return getProjects( user );
    }

    /**
     * Get the projects for which a given user is participating.
     * @param user the given user. If null, no projects are returned.
     * @return the appropriate projects
     */
    @Secured( { "ROLE_USER" } )
    public Set<Project> getProjects( User user ) {
        SortedSet<Project> result = new TreeSet<Project>();
        for ( Project project : this.projects.values() )
            if ( project.isParticipant( user ) || isAdministrator( user ) )
                result.add( project );

        return Collections.unmodifiableSortedSet( result );
    }

    /**
     * Set the value of projects.
     * @param projects The new value of projects
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setProjects( Set<Project> projects ) {
        for ( Project p : this.projects.values() )
            ( (ProjectImpl) p ).removeVetoableChangeListener(
                    NAME_PROPERTY, this.projectNameListener );

        this.projects = new HashMap<String,Project>();
        for ( Project p : projects ) {
            this.projects.put( p.getName(), p );
            ( (ProjectImpl) p ).addVetoableChangeListener(
                    NAME_PROPERTY, this.projectNameListener );
        }
    }

    /**
     * Add a new project.
     * If adding a project with the same name as an existing
     * project, the newer project will replace the old one.
     * @param project the new project.
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addProject( Project project ) {
        // TODO revise this...

        JavaBean old = (JavaBean) this.projects.get( project.getName() );
        if ( old != null )
            old.removeVetoableChangeListener(
                    NAME_PROPERTY, this.projectNameListener );

        this.projects.put( project.getName(), project );

        ( (JavaBean) project ).addVetoableChangeListener(
                NAME_PROPERTY, this.projectNameListener );
    }

    /**
     * Remove a project.
     * @param project the project to get rid of.
     */
    @Secured( { "ROLE_ADMIN" } )
    public void removeProject( Project project ) {
        ( (JavaBean) project ).removeVetoableChangeListener(
                NAME_PROPERTY, this.projectNameListener );
        this.projects.remove( project.getName() );
    }

    /**
     * Return the value of administrators.
     */
    @Secured( { "ROLE_USER" } )
    public Set<User> getAdministrators() {
        SortedSet<User> result = new TreeSet<User>();
        for (  Entry<User,UserTypes> entry : this.userRight.entrySet() ) {
            if ( entry.getValue().equals( AdminType ) )
                result.add( entry.getKey() );
        }

        return Collections.unmodifiableSortedSet( result );
    }

    /**
     * Set the value of administrators.
     * If using this, make sure it is called <i>after</i> a
     * call to setUser()...
     *
     * @param administrators The new value of administrators
     * @throws UserExistsException if new usernames are not distinct.
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setAdministrators( Set<User> administrators )
        throws UserExistsException {

        if ( administrators == null )
            throw new NullPointerException();

        // Make sure all admins are also userRight
        for ( User user : administrators ) {
            addAdministrator( user );
        }
    }

    /**
     * Give administrative rights to a user. Implies an addUser().
     * @param user the user to promote.
     * @throws UserExistsException if a user by the same username
     * already exists
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addAdministrator( User user ) throws UserExistsException {
        addUser( user, AdminType );
    }

    /**
     * Revoke administrative rights from a user.
     * @param user the user to demote.
     */
    @Secured( { "ROLE_ADMIN" } )
    public void removeAdministrator( User user ) {
        this.userRight.put( user, UserType );
    }

    /**
     * Check if given user is an administrator of the system.
     * @param user the given user
     * @return true if an administrator.
     */
    @Secured( { "ROLE_USER" } )
    public boolean isAdministrator( User user ) {
        return AdminType.equals( this.userRight.get( user ) );
    }

    /**
     * Return the value of userRight.
     */
    @Secured( { "ROLE_USER" } )
    public Set<User> getUsers() {
        return Collections.unmodifiableSortedSet(
                new TreeSet<User>(
                        this.userRight.keySet() ) );
    }

    /**
     * Set the value of users.
     * This call resets existing users (and administrators).
     * @param users The new users.
     * @throws UserExistsException if usernames in list are not distinct
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setUsers( Set<User> users ) throws UserExistsException {
        if ( users == null )
            throw new NullPointerException();

        // TODO Fix resetting the admins?

        for ( User user : this.usernames.values() )
            ( (JavaBean) user ).removeVetoableChangeListener(
                    "username", this.usernameListener );

        this.userRight.clear();
        this.usernames.clear();
        for ( User user : users )
            addUser( user );
    }

    /**
     * Add a user to the system.
     * Note: once added, userRight cannot be removed, just disabled.
     * @param user the new user.
     * @throws UserExistsException on duplicate username
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addUser( User user ) throws UserExistsException {
        addUser( user, UserType );
    }

    /**
     * Add a user of the given type.
     * @param user the user
     * @param type the type
     * @throws UserExistsException if a user by that name already exists
     */
    private void addUser( User user, UserTypes type )
        throws UserExistsException {

        if ( this.usernames.containsKey( user.getUsername() ) )
            throw new UserExistsException(
                    MessageFormat.format(
                            "User {0} already exists",
                            user.getUsername() ) );

        this.usernames.put( user.getUsername(), user );
        this.userRight.put( user, type );
        ( (JavaBean) user ).addVetoableChangeListener(
                "username", this.usernameListener );
    }

    /**
     * Test if a given user is managed by the system.
     * @param user the given user
     * @return true if the user is known to the system.
     */
    @Secured( { "ROLE_USER" } )
    public boolean isUser( User user ) {
        return this.userRight.containsKey( user );
    }

    /**
     * Get the Acegi user details given a short user name.
     * @param username the user name
     */
    public UserDetails loadUserByUsername( String username ) {

        User user = this.usernames.get( username );
        if ( user == null )
            throw new UsernameNotFoundException(
                    MessageFormat.format(
                            "User {0} not found", username ) );
        return user;
    }

    /**
     * Find a project of given name.
     * @param name the given name
     * @return null if not found.
     */
    @Secured( { "ROLE_USER" } )
    public Project getProject( String name ) {
        return this.projects.get( name );
    }

    /**
     * Return the value of organizations.
     */
    @Secured( { "ROLE_USER" } )
    public Set<Organization> getOrganizations() {
        return Collections.unmodifiableSet( this.organizations );
    }

    /**
     * Set the value of organizations.
     * @param organizations The new value of organizations
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setOrganizations( Set<Organization> organizations ) {
        this.organizations = new TreeSet<Organization>( organizations );
    }

    /**
     * Add an organization to the system.
     * @param organization the organization to add
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addOrganization( Organization organization ) {
        this.organizations.add( organization );
    }

    /**
     * Remove an organization from the system.
     * @param organization the organization to remove
     */
    @Secured( { "ROLE_ADMIN" } )
    public void removeOrganization( Organization organization ) {
        this.organizations.remove( organization );
    }

    /**
     * Return the value of alerts.
     */
    @Secured( { "ROLE_USER" } )
    public Set<Alert> getAlerts() {
        return this.alerts;
    }

    /**
     * Set the value of alerts.
     * @param alerts The new value of alerts
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setAlerts( Set<Alert> alerts ) {
        this.alerts = alerts;
    }

    /**
     * Add an alert.
     * @param alert the new alert
     */
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SERVER" } )
    public void addAlert( Alert alert ) {
        this.alerts.add( alert );
    }

    /**
     * Remove an alert.
     * @param alert the alert
     */
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SERVER" } )
    public void deleteAlert( Alert alert ) {
        this.alerts.remove( alert );
    }
}
