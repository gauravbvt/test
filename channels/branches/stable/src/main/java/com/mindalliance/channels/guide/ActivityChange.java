package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @XStreamImplicit( itemFieldName = "qualifier" )
    private List<ChangeQualifier> qualifiers;

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

    public List<ChangeQualifier> getQualifiers() {
        return qualifiers == null ? new ArrayList<ChangeQualifier>() : qualifiers;
    }

    public void setQualifiers( List<ChangeQualifier> qualifiers ) {
        this.qualifiers = qualifiers;
    }
}
