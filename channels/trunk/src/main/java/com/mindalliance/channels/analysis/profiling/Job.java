package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.util.SemMatch;

import java.io.Serializable;
import java.util.List;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 8:26:05 PM
 */
public class Job implements Serializable {

    private Actor actor;
    private Role role;
    private Organization organization;
    // TODO -- should be a Jurisdiction, not a String
    private Jurisdiction jurisdiction;
    List<String> channels;

    public Job() {}

    public Job( Part part ) {
        actor = part.getActor();
        role = part.getRole();
        organization = part.getOrganization();
        jurisdiction = part.getJurisdiction();
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Role getRole() {
        return role;
    }

    public void setRole( Role role ) {
        this.role = role;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Jurisdiction jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels( List<String> channels ) {
        this.channels = channels;
    }

    public String getChannelsString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = channels.iterator();
        while (iter.hasNext()) {
            String channel = iter.next();
            sb.append(channel);
            if (iter.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }

    public boolean equals( Object obj ) {
        if (! (obj instanceof Job) ) return false;
        Job job = (Job)obj;
        return actor == job.getActor()
                && role == job.getRole()
                && organization == job.getOrganization()
                && SemMatch.sameLocation(jurisdiction, job.getJurisdiction());
    }

    public int hashCode() {
        int hash = 1;
        if (actor != null) hash = hash * 31 + actor.hashCode();
        if (role != null) hash = hash * 31 + role.hashCode();
        if (organization != null) hash = hash * 31 + organization.hashCode();
        if (jurisdiction != null) hash = hash * 31 + jurisdiction.hashCode();
        return hash;
    }

    public void addChannel(String ch) {
        boolean contained = false;
        for (String channel : channels) {
            if (SemMatch.same(channel, ch)) contained = true;
        }
        if (!contained) channels.add(ch);
    }
}
