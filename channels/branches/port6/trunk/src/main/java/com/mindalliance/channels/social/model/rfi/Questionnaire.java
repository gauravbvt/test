package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.Channels;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:40 PM
 */
@Entity
public class Questionnaire extends AbstractPersistentChannelsObject {


    public enum Status {
        /**
         * RFI surveys can be launched with the questionnaire.
         */
        ACTIVE,
        /**
         * RFI surveys can no longer be launched with the questionnaire.
         */
        INACTIVE
    }

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Questionnaire.class );


    public static final Questionnaire UNKNOWN = new Questionnaire( Channels.UNKNOWN_QUESTIONNAIRE_ID );

    /**
     * Label of the class the questionnaire is about.
     */
    private String about;

    @Column( length = 2000 )
    private String name = "unnamed";

    @OneToMany( mappedBy = "questionnaire", cascade = CascadeType.ALL )
    @OrderBy( "index" )
    private List<Question> questions = new ArrayList<Question>();

    @OneToMany( mappedBy = "questionnaire", cascade = CascadeType.ALL )
    private List<RFISurvey> surveys = new ArrayList<RFISurvey>();

    private Status status = Status.INACTIVE;

    private String issueKind;

    private String remediatedModelObjectRefString;

    public Questionnaire() {
    }

    public Questionnaire( PlanCommunity planCommunity, String username ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
    }

    public Questionnaire( long id ) {
        this.id = id;
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public String getAbout() {
        return about == null ? ModelObject.CLASS_LABELS.get( 0 ) : about;
    }

    public void setAbout( String typeName ) {
        about = typeName;
    }

    public String getName() {
        return ( name == null || name.isEmpty() ) ? "unnamed" : name;
    }

    public void setName( String name ) {
        this.name = StringUtils.abbreviate( name, 2000 );
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions( List<Question> questions ) {
        this.questions = questions;
    }

    public List<RFISurvey> getSurveys() {
        return surveys;
    }

    public void setSurveys( List<RFISurvey> surveys ) {
        this.surveys = surveys;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public String getIssueKind() {
        return issueKind;
    }

    public void setIssueKind( String issueKind ) {
        this.issueKind = issueKind;
    }

    public String getRemediatedModelObjectRefString() {
        return remediatedModelObjectRefString;
    }

    public void setRemediatedModelObjectRefString( String remediatedModelObjectRefString ) {
        this.remediatedModelObjectRefString = remediatedModelObjectRefString;
    }

    public boolean isIssueRemediation() {
        return issueKind != null && remediatedModelObjectRefString != null;
    }

    public void setIssueRemediated( Issue issue ) {
        issueKind = issue.getKind();
        remediatedModelObjectRefString = new ModelObjectRef( issue.getAbout() ).asString();
    }

    public boolean isObsolete( CommunityService communitysService, Analyst analyst ) {
        boolean obsolete = false;
        if ( isIssueRemediation() ) {
            ModelObject mo = ModelObjectRef.resolveFromString( remediatedModelObjectRefString, communitysService );
            if ( mo == null ) {
                obsolete = true;
            } else {
                // TODO : Apparently, cache bug raises exception
                try {
                    List<? extends Issue> unwaivedIssues = analyst.listUnwaivedIssues( communitysService.getPlanService(), mo, false );
                    obsolete = !CollectionUtils.exists(
                            unwaivedIssues,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return ( (Issue) object ).getKind().equals( issueKind );
                                }
                            } );
                } catch ( Exception e ) {
                    LOG.debug( "Failed to look up issues" );
                }
            }
        }
        return obsolete;
    }

    public String toString() {
        return StringUtils.capitalize( getName() )
                + ": a questionnaire about " + getAbout()
                + " (" + getStatus().name() + ")";
    }

    public boolean isActive() {
        return getStatus().equals( Status.ACTIVE );
    }

    public boolean isAboutRemediation( Issue issue ) {
        return isIssueRemediation()
                && getIssueKind().equals( issue.getKind() )
                && getRemediatedModelObjectRefString().equals( new ModelObjectRef( issue.getAbout() ).asString() );
    }

    public static String makeRemediationAbout( Issue issue ) {
        return issue.getAbout().getClassLabel();
    }

    public static String makeRemediationName( Issue issue ) {
        ModelObject mo = issue.getAbout();
        return "Remediating \""
                + issue.getDetectorLabel()
                + "\"- for "
                + issue.getAbout().getTypeName()
                + " \""
                + issue.getAbout().getLabel()
                + "\""
                // + "[" + mo.getId() + "]"
                + ( mo.isSegmentObject()
                ? ( "- in scenario \"" + ( (SegmentObject) mo ).getSegment().getName() + "\"" )
                : "" );
    }


}
