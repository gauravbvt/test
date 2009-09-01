package com.mindalliance.channels.surveys;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.UserIssue;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

/**
 * An issue specification.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 7:23:58 AM
 */
public class IssueSpec implements Serializable {
    /**
     * The kind of detection.
     */
    private String kind;
    /**
     * The description of the issue.
     */
    private String description;
    /**
     * The id of the model object the issue is about.
     */
    private long aboutId;
    /**
     * Id of issue if a user issue.
     */
    private Long userIssueId;
    /**
     * Detector label.
     */
    private String detectorLabel;

    public IssueSpec() {
    }

    public IssueSpec( Issue issue ) {
        if ( issue instanceof UserIssue ) {
            userIssueId = issue.getId();
        }
        kind = issue.getKind();
        description = issue.getDescription();
        aboutId = issue.getAbout().getId();
        detectorLabel = issue.getDetectorLabel();
    }

    public String getKind() {
        return kind;
    }

    public void setKind( String kind ) {
        this.kind = kind;
    }

    public String getDetectorLabel() {
        return detectorLabel;
    }

    public void setDetectorLabel( String detectorLabel ) {
        this.detectorLabel = detectorLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public long getAboutId() {
        return aboutId;
    }

    public void setAboutId( long aboutId ) {
        this.aboutId = aboutId;
    }

    public Long getUserIssueId() {
        return userIssueId;
    }

    public void setUserIssueId( Long userIssueId ) {
        this.userIssueId = userIssueId;
    }

    /**
     * Whether this issue spec matches a given issue.
     *
     * @param issue an issue
     * @return a boolean
     */
    public boolean matches( Issue issue ) {
        if ( issue instanceof UserIssue ) {
            return userIssueId != null && userIssueId == issue.getId();
        } else {
            return issue.getKind().equals( kind )
                    && issue.getAbout().getId() == aboutId
                    && issue.getDescription().equals( description )
                    && issue.getDetectorLabel().equals( detectorLabel );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( userIssueId != null ) {
            sb.append( "user issue," );
            sb.append( userIssueId );
            sb.append( ',' );
        } else {
            sb.append( "detected issue," );
        }
        sb.append( kind );
        sb.append( ',' );
        sb.append( aboutId );
        sb.append( ',' );
        try {
            sb.append( URLEncoder.encode( description, "UTF-8" ) );
            sb.append( ',' );
            sb.append( URLEncoder.encode( detectorLabel, "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( "Failed to encode issue description" );
        }
        return sb.toString();
    }

    public static IssueSpec fromString( String s ) {
        IssueSpec issueSpec = new IssueSpec();
        StringTokenizer tokens = new StringTokenizer( s, "," );
        if ( tokens.nextToken().equals( "user issue" ) ) {
            issueSpec.setUserIssueId( Long.parseLong( tokens.nextToken() ) );
        }
        issueSpec.setKind( tokens.nextToken() );
        issueSpec.setAboutId( Long.parseLong( tokens.nextToken() ) );
        try {
            issueSpec.setDescription( URLDecoder.decode( tokens.nextToken(), "UTF-8" ) );
            issueSpec.setDetectorLabel( URLDecoder.decode( tokens.nextToken(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( "Failed to decode issue description" );
        }
        return issueSpec;
    }
}
