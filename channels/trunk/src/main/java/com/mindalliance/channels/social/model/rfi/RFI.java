package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Channels;
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
public class RFI extends AbstractPersistentChannelsObject implements Messageable {


    public static final String DEADLINE = "deadline";
    public static final String NAG = "nag";
    public static final String TODO = "todo";
    public static final String NEW = "new";


    public static final RFI UNKNOWN = new RFI( Channels.UNKNOWN_RFI_ID );


    @ManyToOne
    private RFISurvey rfiSurvey;

    /**
     * username of user being questioned.
     */
    private String surveyedUsername;

    // Employment = organization and role ids, and title
    @Column( length=2000 )
    private String title;
    private Long organizationId;
    private Long roleId;

    private Date deadline;
    private Date nagged;
    private boolean declined = false;

    @Column( length=10000 )
    private String reasonDeclined = "";

    private boolean naggingRequested = false;

    @Column( length=10000 )
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

    public RFI( String username, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
    }

    public RFI( String username, String surveyedUsername, Employment employment, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.surveyedUsername = surveyedUsername;
        setTitle( employment.getTitle() );
        organizationId = employment.getOrganization().getId();
        roleId = employment.getRole().getId();
    }

    public RFI( RFI rfi ) {
        super( rfi.getCommunityUri(), rfi.getPlanUri(), rfi.getPlanVersion(), rfi.getUsername()  );
        setDeadline( rfi.getDeadline() );
        setRfiSurvey( rfi.getRfiSurvey() );
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
        this.title = StringUtils.abbreviate( title, 2000 );
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
        this.reasonDeclined = StringUtils.abbreviate( reasonDeclined, 10000 );
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
        assert notifications.length() <= 10000;
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

    public boolean isLate( CommunityService communityService ) {
        return !isDeclined()
                && getDeadline() != null
                && new Date().after( getDeadline() )
                && getRfiSurvey().isOngoing( communityService );
    }

    public boolean isActive( CommunityService communityService ) {
        return !isDeclined()
                && isOngoing( communityService );
    }

    public boolean isOngoing( CommunityService communityService ) {
        RFISurvey survey = getRfiSurvey();
        return survey.isOngoing( communityService );
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


    public String getRFILabel( CommunityService communityService ) {
        return getRfiSurvey().getSurveyLabel( communityService );
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
                : new ArrayList<String>( Arrays.asList( notifications.split( "," ) ) );
    }

    public void addNotification( String notification ) {
        List<String> list = allNotifications();
        if ( !list.contains( notification ) ) {
            list.add( notification );
        }
        setNotifications( StringUtils.join( list, "," ) );
    }

    public void removeNotification( String notification ) {
        List<String> list = allNotifications();
        list.remove( notification );
        setNotifications( StringUtils.join( list, "," ) );
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


    /// MESSAGEABLE ///


    private String getToUsername( String topic ) {
        if ( topic.equals( NAG )
                || topic.equals( DEADLINE )
                || topic.equals( NEW ) )
            return getSurveyedUsername();
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        usernames.add(  getToUsername(  topic  ) );
        return usernames;
    }


    @Override
    public String getFromUsername( String topic ) {
        if ( topic.equals( NAG )
                || topic.equals( DEADLINE )
                || topic.equals( NEW ) ) return null;
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            CommunityService communityService ) {
        SurveysDAO surveysDAO = communityService.getPlanService().getSurveysDAO();
        if ( topic.equals( NAG ) || topic.equals( DEADLINE ) )
            return getNagContent( format, communityService, surveysDAO );
        else if ( topic.equals( TODO ) )
            return getTodoContent( format, communityService, surveysDAO );
        else if ( topic.equals( NEW ) )
            return getNewRFIContent( format, communityService, surveysDAO );
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        if ( topic.equals( NAG ) || topic.equals( DEADLINE ) )
            return getNagSubject( format, communityService );
        else if ( topic.equals( NEW ) )
            return getNewRFISubject( format, planService, planService.getSurveysDAO() );
        else
            throw new RuntimeException( "Unknown topic " + topic );
    }

    @Override
    public String getLabel() {
        return "Survey";
    }

    private String getNagSubject( Format format, CommunityService communityService ) {
        // ignore format
        return getRFILabel( communityService )
                + "is due on "
                + new SimpleDateFormat( DATE_FORMAT_STRING ).format( getDeadline() );
    }

    private String getNagContent(
            Format format,
            CommunityService communityService,
            SurveysDAO surveysDAO ) {
        StringBuilder sb = new StringBuilder();
        boolean overdue = getDeadline() != null && new Date().after( getDeadline() );
        sb.append( "This is a reminder to complete " )
                .append( getRFILabel( communityService ) )
                .append( "." );
        if ( overdue ) {
            sb.append( " The survey was due on " )
                    .append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( getDeadline() ) )
                    .append( "." );
        } else {
            sb.append( " The survey is due on " )
                    .append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( getDeadline() ) )
                    .append( "." );
        }
        sb.append( "\n\n" );
        sb.append( "We want to hear from you! You have so far answered " )
                .append( surveysDAO.getPercentRequiredQuestionsAnswered( this ) )
                .append( "% of all required questions. Completing this survey would be greatly appreciated!\n\n" )
                .append( "Regards,\n\n" )
                .append( "The planners of " )
                .append( communityService.getPlanCommunity().getName() );
        return sb.toString();
    }

    private String getTodoContent( Format format, CommunityService communityService, SurveysDAO surveysDAO ) {
        // Ignore format
        StringBuilder sb = new StringBuilder();
        sb.append( getRFILabel( communityService ) );
        if ( getDeadline() != null ) {
            Date now = new Date();
            sb.append( now.after( getDeadline() ) ? " was" : " is" )
                    .append( " sent to you on " )
                    .append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( getDeadline() ) )
                    .append( " and" );
        }
        int percentComplete = surveysDAO.getPercentCompletion( this );
        sb.append( " is " )
                .append( percentComplete )
                .append( "% complete.\n\n" );
        sb.append( "Your participation was requested by " )
                .append( communityService.getUserDao().getFullName( getUsername() ) )
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
        int surveyCompletionCount = surveysDAO.findAllCompletedRFIs( communityService, getRfiSurvey() ).size();
        sb.append( surveyCompletionCount )
                .append( " other " )
                .append( surveyCompletionCount > 1 ? "participants" : "participant" )
                .append( " completed this survey.\n" );
        return sb.toString();
    }

    private String getNewRFISubject( Format format, PlanService planService, SurveysDAO surveysDAO ) {
        // ignore format
        return "New survey: " + getName();
    }

    private String getNewRFIContent( Format format, CommunityService communityService, SurveysDAO surveysDAO ) {
        // ignore format
        Plan plan = communityService.getPlan();
        ChannelsUser surveyedUser = communityService.getUserDao().getUserNamed( getSurveyedUsername() );
        StringBuilder sb = new StringBuilder();
        sb.append( plan.getClient() );
        sb.append( " invites you to participate in a survey about the \"" )
                .append( plan.getName() )
                .append( "\" collaboration plan.\n\n" );
        if ( !plan.getDescription().isEmpty() ) {
            sb.append( "About the plan: " )
                    .append( plan.getDescription() )
                    .append( "\n" );
        }
        if ( communityService.getPlanCommunity().getLocale( communityService ) != null ) {
            sb.append( "Targeted location: " )
                    .append( communityService.getPlanCommunity().getLocale( communityService ).getName() )
                    .append( "\n" );
        }
        sb.append( "\n" );
        sb.append( getRFILabel( communityService ) ).append( "\n" );
        sb.append( "can be accessed here: " )
                .append( surveysDAO.makeURL( communityService, this, surveyedUser ) )
                .append( "\n\n" );
        // New account login instructions
        if ( surveyedUser != null ) {
            sb.append( "To login, use your email address as user name" )
                    .append( surveyedUser.getEmail() );
            String newPassword = surveyedUser.getUserInfo().getGeneratedPassword();
            if ( newPassword != null ) {
                sb.append( " and enter password " )
                        .append( newPassword )
                        .append( "\n\n" );
            } else {
                sb.append( "." );
            }
        }
        // forwarding
        List<RFIForward> rfiForwards = surveysDAO.getForwardingsOf( this );
        if ( !rfiForwards.isEmpty() ) {
            sb.append( "This survey was forwarded to you by:" );
            for ( RFIForward rfiForward : rfiForwards ) {
                ChannelsUserInfo forwarder = surveysDAO.getForwarder( rfiForward );
                if ( forwarder != null ) {
                    sb.append("\n")
                            .append( forwarder.getFullName() )
                            .append( " at " )
                            .append( forwarder.getEmail() );
                }
                if ( !rfiForward.getMessage().isEmpty() ) {
                    sb.append( "\nwith this message: \"" )
                            .append( rfiForward.getMessage() )
                            .append( "\"\n" );
                }
            }
        }
        sb.append( "\nThank you!\n" );
        sb.append( plan.getClient() );
        return sb.toString();
    }

}
