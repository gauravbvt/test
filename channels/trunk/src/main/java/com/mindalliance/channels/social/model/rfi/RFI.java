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


    public static final String NEW = "new";
    public static final String DECLINED = "declined";
    public static final String DEADLINE = "deadline";
    public static final String NAG = "nag";


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
        return reasonDeclined;
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
        if ( topic.equals( NAG ) )
            return getSurveyedUsername();
        else
            throw new RuntimeException();
    }


    @Override
    public String getFromUsername( String topic ) {
        if ( topic.equals( NAG ) ) return null;
        else
            throw new RuntimeException();
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( NAG ) )
            return getNagContent( format, planService, surveysDAO );
        else
            throw new RuntimeException( "invalid content" );
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( NAG ) )
            return getNagSubject( format, planService );
        else
            throw new RuntimeException( "invalid content" );
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

    private String getNagSubject( Format format, PlanService planService ) {
        // ignore format
        return getLabel( planService )
                + "is due on "
                + Messageable.DATE_FORMAT.format( getDeadline() );
    }

}
