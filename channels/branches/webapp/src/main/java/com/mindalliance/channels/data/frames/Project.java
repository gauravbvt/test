// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.frames;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.CategorySet;
import com.mindalliance.channels.data.definitions.Organization;
import com.mindalliance.channels.data.definitions.TypedObject;
import com.mindalliance.channels.data.models.Scenario;
import com.mindalliance.channels.data.profiles.Person;
import com.mindalliance.channels.data.profiles.Role;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.CollectionType;

/**
 * A project.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - missions 1 CategorySet
 * @composed - - * Scenario
 * @composed - - * RolePattern
 * @composed - - * OrganizationPattern
 */
public class Project extends TypedObject {

    private CategorySet missions;
    private Set<Scenario> scenarios = new TreeSet<Scenario>();
    private List<RolePattern> rolePatterns = new ArrayList<RolePattern>();
    private List<OrganizationPattern> organizationPatterns =
                new ArrayList<OrganizationPattern>();

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
    public CategorySet getMissions() {
        return missions;
    }

    /**
     * Set the missions.
     * @param missions the missions to set
     */
    public void setMissions( CategorySet missions ) {
        this.missions = missions;
    }

    /**
     * Return the scenarios.
     */
    @CollectionType( type = Scenario.class )
    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * Set the scenarios.
     * @param scenarios the scenarios to set
     */
    public void setScenarios( Set<Scenario> scenarios ) {
        this.scenarios = new TreeSet<Scenario>( scenarios );
    }

    /**
     * Add a scenario.
     * @param scenario the scenario
     */
    public void addScenario( Scenario scenario ) {
        scenarios.add( scenario );
    }

    /**
     * Remove a scenario.
     * @param scenario the scenario
     */
    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
    }

    /**
     * Test if an organization participates in this project.
     * @param organization the organization
     */
    public boolean includes( final Organization organization ) {
        if ( organizationPatterns == null || organizationPatterns.isEmpty() ) {
            // empty or null means no restriction
            return true;

        } else {
            return CollectionUtils.exists(
                    organizationPatterns, new Predicate() {

                        public boolean evaluate( Object object ) {
                            OrganizationPattern pattern =
                                (OrganizationPattern) object;
                            return pattern.includes( organization );
                        }
                    }
            );
        }
    }

    /**
     * Return the organization criterias.
     */
    @PropertyOptions( ignore = true )
    @CollectionType( type = OrganizationPattern.class )
    public List<OrganizationPattern> getOrganizationPatterns() {
        return organizationPatterns;
    }

    /**
     * Set the organization criterias.
     * @param organizationPatterns the organizationPatterns to set
     */
    public void setOrganizationPatterns(
            List<OrganizationPattern> organizationPatterns ) {
        this.organizationPatterns = organizationPatterns;
    }

    /**
     * Add an organization criteria.
     * @param categories the criteria
     */
    public void addOrganizationPattern( OrganizationPattern categories ) {
        organizationPatterns.add( categories );
    }

    /**
     * Remove an organization criteria.
     * @param categories the criteria
     */
    public void removeOrganizationPattern( OrganizationPattern categories ) {
        organizationPatterns.remove( categories );
    }

    /**
     * Return whether the user matches at least one of the
     * participation criteria.
     *
     * @param authenticatedUser the user
     */
    public boolean hasParticipant( final User authenticatedUser ) {
        // TODO check for organizations
        if ( rolePatterns == null || rolePatterns.isEmpty() ) {
            // empty or null means no restriction
            return true;

        } else {
            return CollectionUtils.exists( rolePatterns, new Predicate() {

                public boolean evaluate( Object object ) {
                    RolePattern rolePattern = (RolePattern) object;
                    return rolePattern.allows( authenticatedUser );
                }
            } );
        }
    }

    /**
     * Return the role-based admission criterias.
     */
    @CollectionType( type = RolePattern.class )
    public List<RolePattern> getRolePatterns() {
        return rolePatterns;
    }

    /**
     * Set the role-based admission criterias.
     * @param rolePatterns the rolePatterns to set
     */
    public void setRolePatterns( List<RolePattern> rolePatterns ) {
        this.rolePatterns = rolePatterns;
    }

    /**
     * Add a role-based admission criteria.
     * @param rolePattern the participation
     */
    public void addRolePattern( RolePattern rolePattern ) {
        rolePatterns.add( rolePattern );
    }

    /**
     * Remove a role-based admission criteria.
     * @param rolePattern the participation
     */
    public void removeRolePattern( RolePattern rolePattern ) {
        rolePatterns.remove( rolePattern );
    }

    /**
     * Generic criteria for allowing participation in a project
     * based on a user's roles.
     */
    public class RolePattern {

        private CategorySet rolePattern = new CategorySet( Taxonomy.Role );

        /**
         * Default constructor.
         */
        public RolePattern() {
        }

        /**
         * Whether a role matches the pattern for participation in the
         * project.
         *
         * @param role the role
         */
        public boolean allows( Role role ) {
            return role.getCategorySet().implies( rolePattern );
        }

        /**
         * Whether any of the roles matches the pattern for
         * participation in the project for a user.
         *
         * @param user the user.
         */
        public boolean allows( User user ) {
            Person person = personForUser( user );
            if ( person != null ) {
                List<Role> roles = person.getRoles();
                return CollectionUtils.exists( roles, new Predicate() {

                    public boolean evaluate( Object object ) {
                        Role role = (Role) object;
                        return allows( role );
                    }
                } );
            } else
                return false;

        }

        /**
         * Return the "project person" for a given system user.
         * @param user the user
         * @return null if the user is not represented in this project
         */
        private Person personForUser( User user ) {
            // TODO
            return null;
        }

        /**
         * Return the role pattern.
         */
        public CategorySet getRolePattern() {
            return rolePattern;
        }

        /**
         * Set the role pattern.
         * @param rolePattern the rolePattern to set
         */
        public void setRolePattern( CategorySet rolePattern ) {
            this.rolePattern = rolePattern;
        }
    }

    /**
     * Generic criteria for allowing users in a project.
     */
    public static class OrganizationPattern {

        private CategorySet categories =
                    new CategorySet( Taxonomy.Organization );

        /**
         * Default constructor.
         */
        public OrganizationPattern() {
        }

        /**
         * Test if an organization is matched by this pattern.
         * @param organization the organization
         */
        public boolean includes( Organization organization ) {
            if ( isInScope( organization ) )
                return true;

            else {
                return CollectionUtils.exists( organization.getParents(),
                        new Predicate() {

                            public boolean evaluate( Object object ) {
                                Organization org = (Organization) object;
                                return isInScope( org );
                            }
                        } );
            }
        }

        /**
         * Whether the organization falls within the scope of the project.
         * @param organization the organization
         */
        public boolean isInScope( Organization organization ) {
            return organization.getCategorySet().implies( categories );
        }

        /**
         * Return the categories.
         */
        public CategorySet getCategories() {
            return categories;
        }

        /**
         * Set the organization pattern.
         * @param categories the categories to set
         */
        public void setCategories( CategorySet categories ) {
            this.categories = categories;
        }
    }
}
