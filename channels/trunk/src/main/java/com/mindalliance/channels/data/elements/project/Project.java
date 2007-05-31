// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.reference.Pattern;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.user.UserImpl;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A project.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Project extends AbstractElement {

    private TypeSet missions;
    private Set<Model> models = new TreeSet<Model>();
    private List<Participation> participations = new ArrayList<Participation>();
    private List<InScope> inScopes = new ArrayList<InScope>();

    /**
     * Default constructor.
     */
    public Project() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid for the project
     */
    public Project( GUID guid ) {
        super( guid );
    }

    /**
     * Return the missions.
     */
    public TypeSet getMissions() {
        return missions;
    }

    /**
     * Set the missions.
     * @param missions the missions to set
     */
    public void setMissions( TypeSet missions ) {
        this.missions = missions;
    }

    /**
     * Return the models.
     */
    @CollectionType( type = Model.class )
    public Set<Model> getModels() {
        return models;
    }

    /**
     * Set the models.
     * @param models the models to set
     */
    public void setModels( Set<Model> models ) {
        for ( Model m : this.models )
            m.setProject( null );
        this.models = new TreeSet<Model>( models );
        for ( Model m : models )
            m.setProject( this );
    }

    /**
     * Add a model.
     * @param model the model
     */
    public void addModel( Model model ) {
        models.add( model );
        model.setProject( this );
    }

    /**
     * Remove a model.
     * @param model the model
     */
    public void removeModel( Model model ) {
        models.remove( model );
        model.setProject( null );
    }

    /**
     * Test if an organization participates in this project.
     * @param organization the organization
     */
    public boolean includes( final Organization organization ) {
        if ( inScopes == null || inScopes.isEmpty() ) {
            // empty or null means no restriction
            return true;

        } else {
            return CollectionUtils.exists( inScopes, new Predicate() {

                public boolean evaluate( Object object ) {
                    InScope inScope = (InScope) object;
                    return inScope.includes( organization );
                }
            } );
        }
    }

    /**
     * Return the organization criterias.
     */
    @CollectionType( type = InScope.class )
    public List<InScope> getInScopes() {
        return inScopes;
    }

    /**
     * Set the organization criterias.
     * @param inScopes the inScopes to set
     */
    public void setInScopes( List<InScope> inScopes ) {
        this.inScopes = inScopes;
    }

    /**
     * Add an organization criteria.
     * @param inScope the criteria
     */
    public void addInScope( InScope inScope ) {
        inScopes.add( inScope );
    }

    /**
     * Remove an organization criteria.
     * @param inScope the criteria
     */
    public void removeInScope( InScope inScope ) {
        inScopes.remove( inScope );
    }

    /**
     * Return whether the user matches at least one of the
     * participation criteria.
     *
     * @param authenticatedUser the user
     */
    public boolean hasParticipant( final User authenticatedUser ) {
        if ( participations == null || participations.isEmpty() ) {
            // empty or null means no restriction
            return true;

        } else {
            return CollectionUtils.exists( participations, new Predicate() {

                public boolean evaluate( Object object ) {
                    Participation participation = (Participation) object;
                    return participation.allows( authenticatedUser );
                }
            } );
        }
    }

    /**
     * Return the role-based admission criterias.
     */
    @CollectionType( type = Participation.class )
    public List<Participation> getParticipations() {
        return participations;
    }

    /**
     * Set the role-based admission criterias.
     * @param participations the participations to set
     */
    public void setParticipations( List<Participation> participations ) {
        this.participations = participations;
    }

    /**
     * Add a role-based admission criteria.
     * @param participation the participation
     */
    public void addParticipation( Participation participation ) {
        participations.add( participation );
    }

    /**
     * Remove a role-based admission criteria.
     * @param participation the participation
     */
    public void removeParticipation( Participation participation ) {
        participations.remove( participation );
    }

    /**
     * Generic criteria for allowing participation in a project
     * based on a user's roles.
     */
    public class Participation {

        private Pattern<Role> rolePattern;

        /**
         * Default constructor.
         */
        public Participation() {
        }

        /**
         * Whether a role matches the pattern for participation in the
         * project.
         *
         * @param role the role
         */
        public boolean allows( Role role ) {
            return rolePattern.matches( role );
        }

        /**
         * Whether any of the roles matches the pattern for
         * participation in the project for a user.
         *
         * @param user the user.
         */
        public boolean allows( User user ) {
            // TODO remove cycle dependency on UserImpl
            Person person = ( (UserImpl) user ).getPerson();
            List<Role> roles = person.getRoles();
            return CollectionUtils.exists( roles, new Predicate() {

                public boolean evaluate( Object object ) {
                    Role role = (Role) object;
                    return allows( role );
                }
            } );
        }

        /**
         * Return the role pattern.
         */
        public Pattern<Role> getRolePattern() {
            return rolePattern;
        }

        /**
         * Set the role pattern.
         * @param rolePattern the rolePattern to set
         */
        public void setRolePattern( Pattern<Role> rolePattern ) {
            this.rolePattern = rolePattern;
        }
    }

    /**
     * Generic criteria for allowing users in a project.
     */
    public class InScope {

        private Pattern<Organization> organizationPattern;

        /**
         * Default constructor.
         */
        public InScope() {
        }

        /**
         * Test if an organization is matched by this pattern.
         * @param organization the organization
         */
        public boolean includes( Organization organization ) {
            if ( organizationPattern.matches( organization ) )
                return true;

            else {
                return CollectionUtils.exists( organization.getParents(),
                        new Predicate() {

                            public boolean evaluate( Object object ) {
                                Organization org = (Organization) object;
                                return organizationPattern.matches( org );
                            }
                        } );
            }
        }

        /**
         * Whether the organization falls within the scope of the project.
         * @param organization the organization
         */
        public boolean isInScope( Organization organization ) {
            return organizationPattern.matches( organization );
        }

        /**
         * Return the organizationPattern.
         */
        public Pattern<Organization> getOrganizationPattern() {
            return organizationPattern;
        }

        /**
         * Set the organization pattern.
         * @param organizationPattern the organizationPattern to set
         */
        public void setOrganizationPattern(
                Pattern<Organization> organizationPattern ) {
            this.organizationPattern = organizationPattern;
        }
    }
}
