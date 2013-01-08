package com.mindalliance.channels.core.community.participation.issues;

import com.mindalliance.channels.core.model.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A participation issue.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 9:13 AM
 */
public class ParticipationIssue implements Serializable {

    private Identifiable about;
    private String kind;
    private String description;
    private List<String> remediationOptions = new ArrayList<String>(  );

    public ParticipationIssue( Identifiable about, ParticipationIssueDetector detector ) {
        this.about = about;
        this.kind = detector.getKind();
    }

    public Identifiable getAbout() {
        return about;
    }

    public String getKind() {
        return kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public List<String> getRemediationOptions() {
        return remediationOptions;
    }

    public void setRemediationOptions( List<String> remediationOptions ) {
        this.remediationOptions = remediationOptions;
    }

    public void addRemediationOption( String option ) {
        remediationOptions.add( option );
    }

    ///////////////

    public String toString() {
        return getKind() + " about " + getAbout().getName();
    }

    public boolean equals( Object object ) {
        if ( object instanceof ParticipationIssue ) {
            ParticipationIssue other = (ParticipationIssue)object;
          return about.equals( other.getAbout() )
                  && kind.equals( other.getKind() );
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + about.hashCode();
        hash = hash * 31 + kind.hashCode();
        return hash;
    }
}
