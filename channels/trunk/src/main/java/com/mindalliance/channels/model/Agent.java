// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.util.GUID;

/**
 * A member of an organization who may possess and need information
 * in order to fulfill role-based responsibilities.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Agent extends AbstractNamedObject {

    /**
     * Your typical genders...
     */
    public enum Gender { male, female }

    private int age;
    private Gender gender;
    private String clearance;

    private List<Job> jobs = new ArrayList<Job>();
    private Availability availability;

    private ContactInfo contactInfo;
    private SortedSet<InformationNeed> needsToKnow =
                new TreeSet<InformationNeed>();
    private SortedSet<InformationAsset> knows = new TreeSet<InformationAsset>();
    private List<ContactInfo> access = new ArrayList<ContactInfo>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Agent( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of access.
     */
    public List<ContactInfo> getAccess() {
        return this.access;
    }

    /**
     * Set the value of access.
     * @param access The new value of access
     */
    public void setAccess( List<ContactInfo> access ) {
        this.access = access;
    }

    /**
     * Return the value of age.
     */
    public int getAge() {
        return this.age;
    }

    /**
     * Set the value of age.
     * @param age The new value of age
     */
    public void setAge( int age ) {
        this.age = age;
    }

    /**
     * Return the value of availability.
     */
    public Availability getAvailability() {
        return this.availability;
    }

    /**
     * Set the value of availability.
     * @param availability The new value of availability
     */
    public void setAvailability( Availability availability ) {
        this.availability = availability;
    }

    /**
     * Return the value of clearance.
     */
    public String getClearance() {
        return this.clearance;
    }

    /**
     * Set the value of clearance.
     * @param clearance The new value of clearance
     */
    public void setClearance( String clearance ) {
        this.clearance = clearance;
    }

    /**
     * Return the value of contactInfo.
     */
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }

    /**
     * Set the value of contactInfo.
     * @param contactInfo The new value of contactInfo
     */
    public void setContactInfo( ContactInfo contactInfo ) {
        this.contactInfo = contactInfo;
    }

    /**
     * Return the value of gender.
     */
    public Gender getGender() {
        return this.gender;
    }

    /**
     * Set the value of gender.
     * @param gender The new value of gender
     */
    public void setGender( Gender gender ) {
        this.gender = gender;
    }

    /**
     * Return the value of jobs.
     */
    public List<Job> getJobs() {
        return this.jobs;
    }

    /**
     * Set the value of jobs.
     * @param jobs The new value of jobs
     */
    public void setJobs( List<Job> jobs ) {
        this.jobs = jobs;
    }

    /**
     * Return the value of knows.
     */
    public SortedSet<InformationAsset> getKnows() {
        return this.knows;
    }

    /**
     * Set the value of knows.
     * @param knows The new value of knows
     */
    public void setKnows( SortedSet<InformationAsset> knows ) {
        this.knows = knows;
    }

    /**
     * Return the value of needsToKnow.
     */
    public SortedSet<InformationNeed> getNeedsToKnow() {
        return this.needsToKnow;
    }

    /**
     * Set the value of needsToKnow.
     * @param needsToKnow The new value of needsToKnow
     */
    public void setNeedsToKnow( SortedSet<InformationNeed> needsToKnow ) {
        this.needsToKnow = needsToKnow;
    }

    //===================================
    /**
     * A particular job of an agent.
     */
    public static class Job {

        private Organization organization;
        private List<String> titles;
        private List<Role> roles;
        private String kind;
        private List<Agent> managers;
        private List<Responsibility> responsibilities;
        private Availability availability;
        private TimePeriod timing;

        /**
         * Default constructor.
         */
        public Job() {
        }

        /**
         * Return the value of availability.
         */
        public Availability getAvailability() {
            return this.availability;
        }

        /**
         * Set the value of availability.
         * @param availability The new value of availability
         */
        public void setAvailability( Availability availability ) {
            this.availability = availability;
        }

        /**
         * Return the value of kind.
         */
        public String getKind() {
            return this.kind;
        }

        /**
         * Set the value of kind.
         * @param kind The new value of kind
         */
        public void setKind( String kind ) {
            this.kind = kind;
        }

        /**
         * Return the value of managers.
         */
        public List<Agent> getManagers() {
            return this.managers;
        }

        /**
         * Set the value of managers.
         * @param managers The new value of managers
         */
        public void setManagers( List<Agent> managers ) {
            this.managers = managers;
        }

        /**
         * Return the value of organization.
         */
        public Organization getOrganization() {
            return this.organization;
        }

        /**
         * Set the value of organization.
         * @param organization The new value of organization
         */
        public void setOrganization( Organization organization ) {
            this.organization = organization;
        }

        /**
         * Return the value of responsibilities.
         */
        public List<Responsibility> getResponsibilities() {
            return this.responsibilities;
        }

        /**
         * Set the value of responsibilities.
         * @param responsibilities The new value of responsibilities
         */
        public void setResponsibilities(
                List<Responsibility> responsibilities ) {

            this.responsibilities = responsibilities;
        }

        /**
         * Return the value of roles.
         */
        public List<Role> getRoles() {
            return this.roles;
        }

        /**
         * Set the value of roles.
         * @param roles The new value of roles
         */
        public void setRoles( List<Role> roles ) {
            this.roles = roles;
        }

        /**
         * Return the value of timing.
         */
        public TimePeriod getTiming() {
            return this.timing;
        }

        /**
         * Set the value of timing.
         * @param timing The new value of timing
         */
        public void setTiming( TimePeriod timing ) {
            this.timing = timing;
        }

        /**
         * Return the value of titles.
         */
        public List<String> getTitles() {
            return this.titles;
        }

        /**
         * Set the value of titles.
         * @param titles The new value of titles
         */
        public void setTitles( List<String> titles ) {
            this.titles = titles;
        }
    }

    //===================================
    /**
     * A role in a particular job.
     */
    public static class Role {

        private String kind;
        private List<Agent> managers;
        private List<Agent> alternates;
        private List<Responsibility> responsibilities;
        private Availability availability;

        /**
         * Default constructor.
         */
        public Role() {
        }

        /**
         * Return the value of alternates.
         */
        public List<Agent> getAlternates() {
            return this.alternates;
        }

        /**
         * Set the value of alternates.
         * @param alternates The new value of alternates
         */
        public void setAlternates( List<Agent> alternates ) {
            this.alternates = alternates;
        }

        /**
         * Return the value of availability.
         */
        public Availability getAvailability() {
            return this.availability;
        }

        /**
         * Set the value of availability.
         * @param availability The new value of availability
         */
        public void setAvailability( Availability availability ) {
            this.availability = availability;
        }

        /**
         * Return the value of kind.
         */
        public String getKind() {
            return this.kind;
        }

        /**
         * Set the value of kind.
         * @param kind The new value of kind
         */
        public void setKind( String kind ) {
            this.kind = kind;
        }

        /**
         * Return the value of managers.
         */
        public List<Agent> getManagers() {
            return this.managers;
        }

        /**
         * Set the value of managers.
         * @param managers The new value of managers
         */
        public void setManagers( List<Agent> managers ) {
            this.managers = managers;
        }

        /**
         * Return the value of responsibilities.
         */
        public List<Responsibility> getResponsibilities() {
            return this.responsibilities;
        }

        /**
         * Set the value of responsibilities.
         * @param responsibilities The new value of responsibilities
         */
        public void setResponsibilities(
                List<Responsibility> responsibilities ) {

            this.responsibilities = responsibilities;
        }
    }
}
