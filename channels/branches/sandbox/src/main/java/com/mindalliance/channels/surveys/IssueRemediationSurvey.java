package com.mindalliance.channels.surveys;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.UserIssue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Issue remediation survey.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/15/11
 * Time: 10:03 AM
 */
public class IssueRemediationSurvey extends Survey {

    /** Class logger. */
    public static final Logger LOG = LoggerFactory.getLogger( IssueRemediationSurvey.class );
    /**
     * Spec of the issue the survey is about.
     */
    private IssueSpec issueSpec;

    public IssueRemediationSurvey() {

    }

    public IssueRemediationSurvey( Issue issue ) {
        super( issue );
        issueSpec = new IssueSpec( issue );
    }


    @Override
    public String getSurveyType() {
        return "Issue remediation";
    }

    @Override
    public String getInvitationTemplate() {
        return "issueRemediationInvitation.vm";
    }

    @Override
    public String getSurveyTemplate() {
        return "issueRemediationSurvey.vm";
    }

    @Override
    public boolean matches( Type type, Identifiable identifiable ) {
        return type == Survey.Type.Remediation
                && identifiable instanceof Issue
                && issueSpec.matches( (Issue) identifiable );
    }

   protected List<String> getDefaultContacts( Analyst analyst ) {
       Issue issue = getIssue( analyst );
        List<String> contacts = new ArrayList<String>();
        if ( issue.isDetected() ) {
            contacts.addAll( ( (DetectedIssue) issue ).getDefaultRemediators() );
        } else {
            // By default all planners
            contacts.addAll( analyst.getQueryService().findAllPlanners() );
        }
        // contacts.remove( survey.getUserName() );
        return contacts;
    }


    @Override
    protected void setIdentifiableSpecs( String specs ) {
        issueSpec = issueSpecsFromString( specs );
    }

    @Override
    public Identifiable findIdentifiable( Analyst analyst ) throws NotFoundException {
        ModelObject mo = analyst.getQueryService().find( ModelObject.class, issueSpec.getAboutId() );
        Issue issue = (Issue) CollectionUtils.find(
                // exclude property-specific, exclude waived
                analyst.listIssues( mo, false, false ), new Predicate() {
                    @Override
                    public boolean evaluate( Object obj ) {
                        return issueSpec.matches( (Issue) obj );
                    }
                } );
        if ( issue != null )
            return issue;
        else
            throw new NotFoundException();
    }

    protected Map<String, Object> getSurveyContext( SurveyService surveyService, Plan plan ) {
        Map<String, Object> context = super.getSurveyContext( surveyService, plan );
        Issue issue = getIssue( surveyService.getAnalyst() );
        if ( issue != null ) {
            context.put( "about", getAboutText( issue ) );
            context.put( "segment", getSegmentText( issue ) );
            context.put( "issue", getIssueDescriptionText( issue ) );
            context.put( "options", getRemediationOptions( issue ) );
        }
        return context;
    }

    public Identifiable getAbout( Analyst analyst ) {
        Issue issue = getIssue( analyst );
        return issue != null ? issue.getAbout() : null;
    }

    protected List<String> getRemediationOptions( Issue issue ) {
        try {
            List<String> options = new ArrayList<String>();
            BufferedReader reader = new BufferedReader( new StringReader( issue.getRemediation() ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                options.add( optionize( line ) );
            }
            return options;
        } catch ( IOException e ) {
            LOG.error( "Failed to get remediation options", e );
            throw new RuntimeException( e );
        }
    }

    public Map<String, Object> getInvitationContext(
            SurveyService surveyService, User user, User issuer, Plan plan ) {
        Map<String, Object> context = super.getInvitationContext( surveyService, user, issuer, plan );
        Issue issue = getIssue( surveyService.getAnalyst() );
        context.put( "issue", getIssueDescriptionText( issue ) );
        context.put( "segment", getSegmentText( issue ) );
        context.put( "about", getAboutPlainText( issue ) );
        return context;
    }

    /**
     * Get short title for the survey.
     *
     * @return a string
     */
    public String getTitle() {
        String title;
        if ( isAboutDetectedIssue() )
            title = issueSpec.getDetectorLabel();
        else
            title = StringUtils.abbreviate(
                    issueSpec.getDescription(),
                    Survey.MAX_TITLE_LENGTH );
        return title;
    }

    @Override
    protected String getIdentifiableSpecs() {
        return issueSpec.toString();
    }


    private String optionize( String line ) {
        String option = StringUtils.trim( line );
        if ( option.startsWith( "or" ) ) {
            option = option.substring( 2 );
        }
        option = StringUtils.trim( option );
        if ( option.endsWith( "." ) ) {
            option = option.substring( 0, option.length() - 1 );
        }
        option = StringUtils.capitalize( option );
        return option;
    }

    private Issue getIssue( Analyst analyst ) {
        try {
            return (Issue) findIdentifiable( analyst );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    private String getIssueDescriptionText( Issue issue ) {
        return issue.getDescription();
    }

    /**
     * Get html text for the model object the issue surveyd is about.
     *
     * @param issue an issue
     * @return a string
     */
    private String getAboutText( Issue issue ) {
        ModelObject about = issue.getAbout();
        String type = about.getKindLabel();
        return type + " \"<strong>" + about.getLabel() + "</strong>\"";
    }

    /**
     * Get plain text for the model object the issue surveyd is about.
     *
     * @param issue an issue
     * @return a string
     */
    private String getAboutPlainText( Issue issue ) {
        ModelObject about = issue.getAbout();
        String type = about.getKindLabel();
        return type + " \"" + about.getLabel() + "\"";
    }

    private String getSegmentText( Issue issue ) {
        ModelObject mo = issue.getAbout();
        if ( mo instanceof SegmentObject ) {
            return ( (SegmentObject) mo ).getSegment().getName();
        } else {
            return null;
        }
    }


    private boolean isAboutDetectedIssue() {
        Long userIssueId = issueSpec.getUserIssueId();
        return userIssueId == null;
    }

    private IssueSpec issueSpecsFromString( String s ) {
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

    }
}
