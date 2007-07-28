// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.support.GUID;

/**
 * A team aggregates roles possibly across organizations and acts as a unit.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @composed - - * Mission
 * @composed - - * Membership
 */
public class Team extends Actor {

    private List<Membership> memberships;
    private List<Mission> missions;

    /**
     * Default constructor.
     */
    public Team() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param taxonomy the taxonomy
     */
    public Team( GUID guid, Taxonomy taxonomy ) {
        super( guid, taxonomy );
    }

    /**
     * Overriden from Actor.
     * @see Actor#getRoles()
     */
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        for ( Membership membership : memberships ) {
            roles.add( membership.getRole() );
        }
        return roles;
    }

    /**
     * Return the memberships of this team.
     */
    public List<Membership> getMemberships() {
        return memberships;
    }

    /**
     * Set the memberships of this team.
     * @param memberships the memberships
     */
    public void setMemberships( List<Membership> memberships ) {
        this.memberships = memberships;
    }

    /**
     * Add a membership.
     * @param membership the membership
     */
    public void addMembership( Membership membership ) {
        memberships.add( membership );
    }

    /**
     * Remove a membership.
     * @param membership the membership
     */
    public void removeMembership( Membership membership ) {
        memberships.remove( membership );
    }

    /**
     * Return the value of missions.
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * Set the value of missions.
     * @param missions The new value of missions
     */
    public void setMissions( List<Mission> missions ) {
        this.missions = missions;
    }

    /**
     * Add a mission.
     * @param mission the mission
     */
    public void addMission( Mission mission ) {
        missions.add( mission );
    }

    /**
     * Remove a mission.
     * @param mission the mission
     */
    public void removeMission( Mission mission ) {
        missions.remove( mission );
    }

    /**
     * Description for member of the team.
     * @navassoc - - 1 Role
     * @opt attributes
     */
    public class Membership {

        private Role role;
        private int count;
        private boolean pointOfContact;

        /**
         * Default constructor.
         */
        public Membership() {
        }

        /**
         * Return the count, ie. how many participants of the given
         * role are in this group.
         */
        public int getCount() {
            return count;
        }

        /**
         * Set the count.
         * @param count the count to set
         */
        public void setCount( int count ) {
            this.count = count;
        }

        /**
         * Return if this membership is a point of contact for
         * the team.
         */
        public boolean isPointOfContact() {
            return pointOfContact;
        }

        /**
         * Specify if this membership is a point of contact for
         * the team.
         * @param pointOfContact the point of contact status
         */
        public void setPointOfContact( boolean pointOfContact ) {
            this.pointOfContact = pointOfContact;
        }

        /**
         * Return the role.
         */
        public Role getRole() {
            return role;
        }

        /**
         * Set the role.
         * @param role the role
         */
        public void setRole( Role role ) {
            this.role = role;
        }
    }
}
