/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.components.Mission;
import com.mindalliance.channels.util.GUID;

/**
 * A team aggregates roles possibly across organizations and acts as a unit.
 * 
 * @author jf
 */
public class Team extends AbstractActor {

    public class Membership {

        private Role role;
        private Integer count;
        private boolean pointOfContact; // is a point of contact for
                                        // the team

        /**
         * @return the count
         */
        public Integer getCount() {
            return count;
        }

        /**
         * @param count the count to set
         */
        public void setCount( Integer count ) {
            this.count = count;
        }

        /**
         * @return the pointOfContact
         */
        public boolean isPointOfContact() {
            return pointOfContact;
        }

        /**
         * @param pointOfContact the pointOfContact to set
         */
        public void setPointOfContact( boolean pointOfContact ) {
            this.pointOfContact = pointOfContact;
        }

        /**
         * @return the role
         */
        public Role getRole() {
            return role;
        }

        /**
         * @param role the role to set
         */
        public void setRole( Role role ) {
            this.role = role;
        }
    }

    private List<Membership> memberships;
    private List<Mission> missions;

    public Team() {
        super();
    }

    public Team( GUID guid ) {
        super( guid );
    }

    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        for (Membership membership : memberships) {
            roles.add( membership.getRole() );
        }
        return roles;
    }

    /**
     * @return the memberships
     */
    public List<Membership> getMemberships() {
        return memberships;
    }

    /**
     * @param memberships the memberships to set
     */
    public void setMemberships( List<Membership> memberships ) {
        this.memberships = memberships;
    }

    /**
     * @param membership
     */
    public void addMembership( Membership membership ) {
        memberships.add( membership );
    }

    /**
     * @param membership
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
     * Add a mission
     * @param mission
     */
    public void addMission(Mission mission) {
        missions.add( mission );
    }
    /**
     * Remove a mission
     * @param mission
     */
    public void removeMission(Mission mission) {
        missions.remove( mission );
    }

}
