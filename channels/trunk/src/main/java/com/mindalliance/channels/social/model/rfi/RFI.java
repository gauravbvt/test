package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:50 PM
 */
@Entity
public class RFI extends AbstractPersistentPlanObject implements Messageable {


    public static final String DEADLINE = "deadline";
    public static final String NAG = "nag";
    public static final String TODO = "todo";


    public static final RFI UNKNOWN = new RFI( Channels.UNKNOWN_RFI_ID );


    @ManyToOne
    private RFISurvey rfiSurvey;

    /**
     * username of user being questioned.
     */
    private String surveyedUsername;

    // Employment = organization and role ids, and title
    private String title;
    private Long organizationId;
    private Long roleId;

    private Date deadline;
    private Date nagged;
    private boolean declined = false;

    private String reasonDeclined = "";

    private boolean naggingRequested = false;

    private String notifications;

    @OneToMany( mappedBy = "rfi", cascade = CascadeType.ALL )
    private List<RFIForward> forwards = new ArrayList<RFIForward>();

    @OneToMany( mappedBy = "rfi", cascade = CascadeType.ALL )
    private List<AnswerSet> answerSets = new ArrayList<AnswerSet>();

    public RFI() {
    }

    public RFI( long id ) {
        this.id = id;
    }

    public RFI( String username, String planUri, int planVersion ) {
        super( planUri, planVersion, username );
    }

    public RFI( String username, String planUri, int planVersion, String surveyedUsername, Employment employment ) {
        this( username, planUri, planVersion );
        this.surveyedUsername = surveyedUsername;
        title = employment.getTitle();
        organizationId = employment.getOrganization().getId();
        roleId = employment.getRole().getId();
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public String getSurveyedUsername() {
        return surveyedUsername;
    }

    public void setSurveyedUsername( String surveyedUsername ) {
        this.surveyedUsername = surveyedUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId( Long organizationId ) {
        this.organizationId = organizationId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId( Long roleId ) {
        this.roleId = roleId;
    }

    public RFISurvey getRfiSurvey() {
        return rfiSurvey;
    }

    public void setRfiSurvey( RFISurvey rfiSurvey ) {
        this.rfiSurvey = rfiSurvey;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined( boolean declined ) {
        this.declined = declined;
    }

    public String getReasonDeclined() {
        return reasonDeclined == null ? "" : reasonDeclined;
    }

    public void setReasonDeclined( String reasonDeclined ) {
        this.reasonDeclined = reasonDeclined;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline( Date deadline ) {
        this.deadline = deadline;
    }

    public boolean isNaggingRequested() {
        return naggingRequested;
    }

    public void setNaggingRequested( boolean naggingRequested ) {
        this.naggingRequested = naggingRequested;
    }

    public Date getNagged() {
        return nagged;
    }

    public void setNagged( Date nagged ) {
        this.nagged = nagged;
        setNaggingRequested( false );
        addNotification( NAG );
    }

    public void nag() {
        setNaggingRequested( true );
        removeNotification( NAG );
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications( String notifications ) {
        this.notifications = notifications;
    }

    public List<RFIForward> getForwards() {
        return forwards;
    }

    public void setForwards( List<RFIForward> forwards ) {
        this.forwards = forwards == null ? new ArrayList<RFIForward>() : forwards;
    }

    public void addForwarding( RFIForward forwarding ) {
        getForwards().add( forwarding );
    }

    public List<AnswerSet> getAnswerSets() {
        return answerSets;
    }

    public void setAnswerSets( List<AnswerSet> answerSets ) {
        this.answerSets = answerSets;
    }

    public boolean isLate( QueryService queryService, Analyst analyst ) {
        return !isDeclined()
                && getDeadline() != null
                && new Date().after( getDeadline() )
                && getRfiSurvey().isOngoing( queryService, analyst );
    }

    public boolean isActive( QueryService queryService, Analyst analyst ) {
        return !isDeclined()
                && isOngoing( queryService, analyst );
    }

    public boolean isOngoing( QueryService queryService, Analyst analyst ) {
        RFISurvey survey = getRfiSurvey();
        return survey.isOngoing( queryService, analyst );
    }

    public long getTimeLeft() {
        Date deadline = getDeadline();
        if ( deadline == null ) {
            return Long.MAX_VALUE;
        } else {
            return deadline.getTime() - new Date().getTime();
        }
    }

    public String getShortTimeLeft() {
        Date deadline = getDeadline();
        if ( deadline == null ) {
            return null;
        } else {
            long delta = deadline.getTime() - new Date().getTime();
            boolean past = delta < 0;
            String interval = ChannelsUtils.getShortTimeIntervalString( Math.abs( delta ) );
            return past ? interval + " ago" : "In " + interval;
        }
    }

    public String getLongTimeLeft() {
        Date deadline = getDeadline();
        if ( deadline == null ) {
            return null;
        } else {
            long delta = deadline.getTime() - new Date().getTime();
            boolean past = delta < 0;
            String interval = ChannelsUtils.getLongTimeIntervalString( Math.abs( delta ) );
            return past ? interval + " ago" : "In " + interval;
        }
    }

    @Override
    public String getName() {
        return getRfiSurvey().getName();
    }


    public String getLabel( QueryService queryService ) {
        return getRfiSurvey().getLabel( queryService );
    }

    public void nagged() {
        naggingRequested = false;
        nagged = new Date();
    }

    public boolean isNotificationSent( String notification ) {
        return notifications != null && allNotifications().contains( notification );
    }

    private List<String> allNotifications() {
        return notifications == null
                ? new ArrayList<String>()
                : Arrays.asList( notifications.split( "," ) );
    }

    public void addNotification( String notification ) {
        List<String> list = allNotifications();
        if ( !list.contains( notification ) ) {
            list.add( notification );
        }
        notifications = StringUtils.join( list, "," );
    }

    public void removeNotification( String notification ) {
        List<String> list = allNotifications();
        list.remove( notification );
        notifications = StringUtils.join( list, "," );
    }


    /// MESSAGEABLE ///


    @Override
    public String getToUsername( String topic ) {
        if ( topic.equals( NAG )
                || topic.equals( DEADLINE ) )
            return getSurveyedUsername();
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }


    @Override
    public String getFromUsername( String topic ) {
        if ( topic.equals( NAG )
                || topic.equals( DEADLINE ) ) return null;
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( NAG ) || topic.equals( DEADLINE ) )
            return getNagContent( format, planService, surveysDAO );
        else if ( topic.equals( TODO ) )
            return getTodoContent( format, planService, surveysDAO );
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }


    @Override
    public String getSubject(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( NAG ) || topic.equals( DEADLINE ) )
            return getNagSubject( format, planService );
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    private String getNagSubject( Format format, PlanService planService ) {
        // ignore format
        return getLabel( planService )
                + "is due on "
                + Messageable.DATE_FORMAT.format( getDeadline() );
    }

    private String getNagContent(
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        StringBuilder sb = new StringBuilder();
        boolean overdue = getDeadline() != null && new Date().after( getDeadline() );
        sb.append( "This is a reminder to complete " )
                .append( getLabel( planService ) )
                .append( "." );
        if ( overdue ) {
            sb.append( " The survey was due on " )
                    .append( Messageable.DATE_FORMAT.format( getDeadline() ) )
                    .append( "." );
        } else {
            sb.append( " The survey is due on " )
                    .append( Messageable.DATE_FORMAT.format( getDeadline() ) )
                    .append( "." );
        }
        sb.append( "\n\n" );
        sb.append( "We want to hear from you! You have so far answered " )
                .append( surveysDAO.getPercentRequiredQuestionsAnswered( this ) )
                .append( "% of all required questions. Completing this survey would be greatly appreciated!\n\n" )
                .append( "Regards,\n\n" )
                .append( "The planners of " )
                .append( planService.getPlan().getName() );
        return sb.toString();
    }

    private String getTodoContent( Format format, PlanService planService, SurveysDAO surveysDAO ) {
        // Ignore format
        StringBuilder sb = new StringBuilder();
        sb.append( getLabel( planService ) );
        if ( getDeadline() != null ) {
            Date now = new Date();
            sb.append( now.after( getDeadline() ) ? " was" : " is" )
                    .append( " sent to you on " )
                    .append( DATE_FORMAT.format( getDeadline() ) )
                    .append( " and" );
        }
        int percentComplete = surveysDAO.getPercentCompletion( this );
        sb.append( " is " )
                .append( percentComplete )
                .append( "% complete.\n\n" );
        sb.append( "Your participation was requested by " )
                .append( planService.getUserDao().getFullName( getUsername() ) )
                .append( ".\n\n" );
        int requiredQuestionsCount = surveysDAO.getRequiredQuestionCount( this );
        int optionalQuestionsCount = surveysDAO.getOptionalQuestionCount( this );
        sb.append( "You answered " )
                .append( surveysDAO.getRequiredAnswersCount( this ) )
                .append( " out of " )
                .append( requiredQuestionsCount )
                .append( " required " )
                .append( requiredQuestionsCount > 1 ? "questions" : "question" )
                .append( " and " )
                .append( surveysDAO.getOptionalAnswersCount( this ) )
                .append( " out of " )
                .append( optionalQuestionsCount )
                .append( " optional " )
                .append( optionalQuestionsCount > 1 ? "questions" : "question" )
                .append( ".\n\n" );
        int surveyCompletionCount = surveysDAO.findAllCompletedRFIs( planService.getPlan(), getRfiSurvey() ).size();
        sb.append( surveyCompletionCount )
                .append( " other " )
                .append( surveyCompletionCount > 1 ? "participants" : "participant" )
                .append( " completed this survey.\n" );
        return sb.toString();
    }

    public int compareUrgencyTo( RFI other, SurveysDAO surveysDAO ) {
        if ( getDeadline() == null && other.getDeadline() == null ) {
            int percent = surveysDAO.getPercentCompletion( this );
            int otherPercent = surveysDAO.getPercentCompletion( other );
            return
                    percent < otherPercent
                            ? -1
                            : percent > otherPercent
                            ? 1
                            : getCreated().compareTo( other.getCreated() );
        } else {
            return other.getDeadline() == null
                    ? -1
                    : getDeadline() == null
                    ? 1
                    : getDeadline().compareTo( other.getDeadline() );
        }
    }
}
