package com.mindalliance.channels.db.data.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.db.data.AbstractModelObjectReferencingDocument;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A questionnaire applied to a plan element.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 11:27 AM
 */
@Document(collection = "surveys")
public class RFISurvey extends AbstractModelObjectReferencingDocument implements Messageable {

    public static final RFISurvey UNKNOWN = new RFISurvey( Channels.UNKNOWN_RFI_SURVEY_ID );
    private static final String DELETED = "(DELETED)";
    public static final String STATUS = "status";
    private String questionnaireUid;
    private boolean closed = false;
    private boolean canBeForwarded = true;
    private Date deadline;
    // Questionnaire's about - a model object's label.
    private String about;
    private String name;
    private boolean issueRemediation;

    public RFISurvey() {
    }

    public RFISurvey( PlanCommunity planCommunity, String username ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username );
    }

    public RFISurvey( Long id ) {
        this.uid = Long.toString( id ); // only to be used for unknown RFISurvey
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public String getQuestionnaireUid() {
        return questionnaireUid;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        questionnaireUid = questionnaire.getUid();
        setAbout( questionnaire.getAbout() );
        setName( questionnaire.getName() );
        setIssueRemediation( questionnaire.isIssueRemediation() );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isIssueRemediation() {
        return issueRemediation;
    }

    public void setIssueRemediation( boolean issueRemediation ) {
        this.issueRemediation = issueRemediation;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout( String about ) {
        this.about = StringUtils.abbreviate( about, 2000 );
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        this.closed = closed;
    }

    public boolean isCanBeForwarded() {
        return canBeForwarded;
    }

    public void setCanBeForwarded( boolean canBeForwarded ) {
        this.canBeForwarded = canBeForwarded;
    }

    public boolean isOngoing( CommunityService communityService ) {
        return !isClosed() && !isObsolete( communityService );
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline( Date deadline ) {
        this.deadline = deadline;
    }

    public String getStatusLabel( CommunityService communityService ) {
        return isClosed()
                ? "Closed"
                : isObsolete( communityService )
                ? "Obsolete"
                : "Ongoing";

    }

    public String getSurveyLabel( CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        sb
                .append( isClosed() ? "Closed survey \"" : "Survey \"" )
                .append( getName() )
                .append( "\"" );
        if ( !isIssueRemediation() ) {
            sb.append( ", about " )
                    .append( getMoLabel() );
            if ( !( getModelObject( communityService ) instanceof CollaborationModel ) )
                sb.append( " in collaboration model " )
                        .append( communityService.getPlan().getName() );
        }
        return sb.toString();
    }

    private String getModelObjectName( CommunityService communityService ) {
        ModelObject mo = getModelObject( communityService );
        return mo == null
                ? DELETED
                : mo.getName();
    }

    public boolean isObsolete( CommunityService communityService ) {
        Questionnaire questionnaire = communityService.getModelService()
                .getSurveysDAO().findQuestionnaire( questionnaireUid );
        return !questionnaire.isActive()
                || ( getAboutRef() != null && getModelObject( communityService ) == null )
                || questionnaire.isObsolete( communityService, communityService.getAnalyst() );
    }

    @Override
    public String getName() {
        return name;
    }

    /// Messageable


    private String getToUsername( String topic ) {
        return topic.equals( STATUS )
                ? UserRecord.PLANNERS
                : null;
    }

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        usernames.add( getToUsername( topic ) );
        return usernames;
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getContent(
            String topic,
            Messageable.Format format,
            CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        if ( topic.equals( STATUS ) ) {
            return getStatusContent( communityService, modelService.getSurveysDAO() );
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    private String getStatusContent( CommunityService communityService, SurveysDAO surveysDAO ) {
        StringBuilder sb = new StringBuilder();
        sb.append( getSurveyLabel( communityService ) )
                .append( ".\n" )
                .append( "Launched on " )
                .append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( getCreated() ) )
                .append( " by " )
                .append( communityService.getUserRecordService().getFullName( getUsername() ) )
                .append( ".\n\n" );
        List<RFI> completedRFIs = surveysDAO.findAllCompletedRFIs( communityService, this );
        List<RFI> incompleteRFIs = surveysDAO.findAllIncompleteRFIs( communityService, this );
        List<RFI> declinedRFIs = surveysDAO.findAllDeclinedRFIs( communityService, this );
        List<RFIForward> forwards = surveysDAO.findAllRFIForwards( communityService, this );
        addCompletedRFIContent( "completed", completedRFIs, sb, communityService );
        addIncompleteRFIContent( "incomplete", incompleteRFIs, sb, communityService, surveysDAO );
        addForwardContent( "forwarded", forwards, sb, communityService );
        addDeclinedContent( "declined", declinedRFIs, sb, communityService );
        sb.append( "\n" );
        return sb.toString();
    }

    private void addCompletedRFIContent(
            String title,
            List<RFI> rfisList,
            StringBuilder sb,
            CommunityService communityService ) {
        sb.append( rfisList.size() ).append( " " ).append( title ).append( ":\n" );
        for ( RFI rfi : rfisList ) {
            sb.append( "\t" )
                    .append( communityService.getUserRecordService().getFullName( rfi.getSurveyedUsername() ) )
                    .append( "\n" );
        }
    }

    private void addIncompleteRFIContent(
            String title,
            List<RFI> rfisList,
            StringBuilder sb,
            CommunityService communityService,
            SurveysDAO surveysDAO ) {
        Date now = new Date();
        sb.append( rfisList.size() ).append( " " ).append( title ).append( ":\n" );
        for ( RFI rfi : rfisList ) {
            sb.append( "\t" );
            sb.append( communityService.getUserRecordService().getFullName( rfi.getSurveyedUsername() ) ).append( "(" );
            int percent = surveysDAO.getPercentCompletion( rfi );
            sb.append( percent ).append( "%" );
            Date dueDate = rfi.getDeadline();
            if ( dueDate != null && now.after( dueDate ) ) {
                sb.append( ", OVERDUE" );
            }
            sb.append( ")\n" );
        }
    }

    private void addDeclinedContent(
            String title,
            List<RFI> rfisList,
            StringBuilder sb,
            CommunityService communityService ) {
        sb.append( rfisList.size() ).append( " " ).append( title ).append( ":\n" );
        for ( RFI rfi : rfisList ) {
            sb.append( "\t" )
                    .append( communityService.getUserRecordService().getFullName( rfi.getSurveyedUsername() ) )
                    .append( " (" )
                    .append( rfi.getReasonDeclined().isEmpty() ? "No reason given." : rfi.getReasonDeclined() )
                    .append( ")\n" );
        }
    }

    private void addForwardContent(
            String title,
            List<RFIForward> forwards,
            StringBuilder sb,
            CommunityService communityService ) {
        sb.append( forwards.size() ).append( " " ).append( title ).append( ":\n" );
        for ( RFIForward forward : forwards ) {
            sb.append( "\t" )
                    .append( "from " )
                    .append( communityService.getUserRecordService().getFullName( forward.getUsername() ) )
                    .append( " to " )
                    .append( communityService.getUserRecordService().getFullName( forward.getForwardToEmail() ) )
                    .append( "\n" );
        }
    }

    @Override
    public String getSubject(
            String topic,
            Messageable.Format format,
            CommunityService communityService ) {
        return null;
    }

    @Override
    public String getLabel() {
        return "Survey";
    }
}
