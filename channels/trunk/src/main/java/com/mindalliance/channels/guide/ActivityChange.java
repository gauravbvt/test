package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/25/12
 * Time: 1:38 PM
 */
public class ActivityChange implements Serializable {

    private Long subjectId;
    private String subjectPath;
    @XStreamAlias( "type" )
    private String changeType;
    private String property;
    private String updateTargetPath;

    public String getUpdateTargetPath() {
        return updateTargetPath;
    }

    public void setUpdateTargetPath( String updateTargetPath ) {
        this.updateTargetPath = updateTargetPath;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId( Long subjectId ) {
        this.subjectId = subjectId;
    }

    public String getSubjectPath() {
        return subjectPath;
    }

    public void setSubjectPath( String subjectPath ) {
        this.subjectPath = subjectPath;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType( String changeType ) {
        this.changeType = changeType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty( String property ) {
        this.property = property;
    }
}
