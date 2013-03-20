package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.social.model.AbstractModelObjectReferencingPPO;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A survey about a model object and based on a questionnaire.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 10:50 AM
 */
@Entity
public class RFISurvey extends AbstractModelObjectReferencingPPO implements Messageable {

    public static final RFISurvey UNKNOWN = new RFISurvey( Channels.UNKNOWN_RFI_SURVEY_ID );
    private static final String DELETED = "(DELETED)";
    public static final String STATUS = "status";
    @ManyToOne
    private Questionnaire questionnaire;
    private boolean closed = false;
    private boolean canBeForwarded = true;
    private Date deadline;
    // Questionnaire's about - a model object's label.
    @Column( length = 2000 )
    private String about;
    @OneToMany(mappedBy = "rfiSurvey", cascade = CascadeType.ALL)
    private List<RFI> rfis;

    public RFISurvey() {
    }

    public RFISurvey( PlanCommunity planCommunity, String username ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
    }

    public RFISurvey( Long id ) {
        this.id = id;
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        this.questionnaire = questionnaire;
        setAbout( questionnaire.getAbout() );
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

    public List<RFI> getRfis() {
        return rfis;
    }

    public void setRfis( List<RFI> rfis ) {
        this.rfis = rfis;
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
                .append( isClosed() ? "Closed survey on " : "Survey on " )
                .append( getQuestionnaire().getName() );
        if ( !getQuestionnaire().isIssueRemediation() ) {
            sb.append( ", about " )
                    .append( getMoLabel() )
                    .append( " in plan " )
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
        return !getQuestionnaire().isActive()
                || ( getAboutRef() != null && getModelObject( communityService ) == null )
                || getQuestionnaire().isObsolete( communityService, communityService.getAnalyst() );
    }

    @Override
    public String getName() {
        return getQuestionnaire().getName();
    }

    /// Messageable


    private String getToUsername( String topic ) {
        return topic.equals( STATUS )
                ? ChannelsUserInfo.PLANNERS
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
            Format format,
            CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        if ( topic.equals( STATUS ) ) {
            return getStatusContent( communityService, planService.getSurveysDAO() );
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
                .append( communityService.getUserDao().getFullName( getUsername() ) )
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
                    .append( communityService.getUserDao().getFullName( rfi.getSurveyedUsername() ) )
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
            sb.append( communityService.getUserDao().getFullName( rfi.getSurveyedUsername() ) ).append( "(" );
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
                    .append( communityService.getUserDao().getFullName( rfi.getSurveyedUsername() ) )
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
                    .append( communityService.getUserDao().getFullName( forward.getUsername() ) )
                    .append( " to " )
                    .append( communityService.getUserDao().getFullName( forward.getForwardToEmail() ) )
                    .append( "\n" );
        }
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            CommunityService communityService ) {
        return null;
    }

    @Override
    public String getLabel() {
        return "Survey";
    }
}
