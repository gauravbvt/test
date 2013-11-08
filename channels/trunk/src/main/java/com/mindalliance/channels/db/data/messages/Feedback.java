package com.mindalliance.channels.db.data.messages;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Channels;
import org.apache.commons.lang.WordUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:26 PM
 */
@Document( collection = "feedback" )
public class Feedback extends UserStatement {

    public static final Feedback UNKNOWN = new Feedback( Channels.UNKNOWN_FEEDBACK_ID );
    public static final String CHECKLISTS = "Checklists";
    public static final String SURVEYS = "Surveys";
    public static final String TEMPLATES = "Templates";
    public static final String PLANS = "Plans";
    public static final String PARTICIPATION = "Participation";
    public static final String ISSUES = "Issues";
    public static final String FEEDBACK = "Feedback";
    public static final String REQUIREMENTS = "Requirements";
    public static final String CHANNELS = "Channels";


    public enum Type {
        QUESTION,
        PROBLEM,
        SUGGESTION;
        private static final String QUESTION_LABEL = "question";
        private static final String PROBLEM_LABEL = "problem";
        private static final String SUGGESTION_LABEL = "suggestion";

        public String getLabel() {
            switch ( this ) {
                case QUESTION:
                    return QUESTION_LABEL;
                case PROBLEM:
                    return PROBLEM_LABEL;
                case SUGGESTION:
                    return SUGGESTION_LABEL;
                default:
                    return "???";
            }
        }

        public static Type fromLabel( String label ) {
            if ( label.equalsIgnoreCase( QUESTION.getLabel() ) ) return QUESTION;
            else if ( label.equalsIgnoreCase( PROBLEM.getLabel() ) ) return PROBLEM;
            else if ( label.equalsIgnoreCase( SUGGESTION.getLabel() ) ) return SUGGESTION;
            else return null;
        }
    }

    private String fromEmail;
    private Type type;
    private String topic;
    private String context;
    private boolean urgent;
    private Date whenNotified;
    private boolean resolved;
    private Date lastReplied;
    private List<UserMessage> replies;
    private boolean repliesRead;

    public Feedback() {
    }

    public Feedback( long id ) {
        this.uid = Long.toString( id ); // only to be used for unknown feedback
    }

    public Feedback( String username, Type type, PlanCommunity planCommunity ) {
        super( username, planCommunity );
        this.type = type;
    }

     public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getContext() {
        return context;
    }

    public void setContext( String context ) {
        this.context = context;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent( boolean urgent ) {
        this.urgent = urgent;
    }

    public String getTopic() {
        return topic == null ? "" : topic;
    }

    public void setTopic( String topic ) {
        this.topic = topic;
    }

    public Date getWhenNotified() {
        return whenNotified;
    }

    public void setWhenNotified( Date whenNotified ) {
        this.whenNotified = whenNotified;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail( String fromEmail ) {
        this.fromEmail = fromEmail;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved( boolean resolved ) {
        this.resolved = resolved;
    }


    public List<UserMessage> getReplies() {
        return replies == null ? new ArrayList<UserMessage>() : replies;
    }

    public void setReplies( List<UserMessage> replies ) {
        this.replies = replies;
    }

    public void addReply( UserMessage reply ) {
        replies.add( reply );
        setLastReplied( new Date() );
    }

    public boolean isSuggestion() {
        return type == Type.SUGGESTION;
    }

    public boolean isProblem() {
        return type == Type.PROBLEM;
    }

    public boolean isQuestion() {
        return type == Type.QUESTION;
    }

    public boolean isRepliedTo() {
        return !getReplies().isEmpty();
    }

    public String getTypeLabel() {
        return type.name().toLowerCase();
    }

    public Date getLastReplied() {
        return lastReplied;
    }

    public void setLastReplied( Date lastReplied ) {
        this.lastReplied = lastReplied;
    }

    public boolean isRepliesRead() {
        return repliesRead;
    }

    public void setRepliesRead( boolean repliesRead ) {
        this.repliesRead = repliesRead;
    }

    public String getUserFullName( UserRecordService userInfoService ) {
        ChannelsUser user = userInfoService.getUserWithIdentity( getUsername() );
        if ( user != null ) {
            String fullName = user.getFullName();
            return ( fullName == null || fullName.isEmpty() ) ? getUsername() : fullName;
        } else {
            return getUsername();
        }
    }

    // Messageable


    @Override
    public String getToUsername( String topic ) {
        return UserRecord.PLANNERS;
    }

    @Override
    protected String getTextContent( Format format, CommunityService communityService ) {
        // Ignore format
        return "Plan: " + getPlanUri()
                + ":"
                + getPlanVersion()
                + "\nUser: " + communityService.getUserRecordService().getFullName( getUsername() )
                + "\n"
                + new SimpleDateFormat( DATE_FORMAT_STRING ).format( getCreated() )
                + aboutString()
                + "\n----------------------------------------------------------------------------\n\n"
                + getText()
                + "\n\n----------------------------------------------------------------------------\n";
    }

    private String aboutString() {
        String about = getMoRef();
        if ( about == null ) {
            return "";
        } else {
            ModelObjectRef moRef = ModelObjectRef.fromString( about );
            StringBuilder sb = new StringBuilder();
            sb.append( "\nAbout: " );
            sb.append( moRef.getTypeName() );
            sb.append( " \"" );
            sb.append( moRef.getName() );
            sb.append( "\" [" );
            sb.append( moRef.getId() );
            sb.append( "]" );
            String segmentName = moRef.getSegmentName();
            if ( !segmentName.isEmpty() ) {
                sb.append( " in segment \"" );
                sb.append( segmentName );
            }
            String topic = getTopic();
            if ( topic != null && !topic.isEmpty() ) {
                sb.append( " (" );
                sb.append( topic );
                sb.append( ')' );
            }
            return sb.toString();
        }
    }

    protected String getTextSubject( Format format, CommunityService communityService ) {
        // Ignore format
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback" );
        if ( isUrgent() ) sb.append( " [ASAP]" );
        sb.append( " - " );
        sb.append( WordUtils.capitalize( getType().name() ) );
        sb.append( " - " );
        sb.append( getText().replaceAll( "\\s", " " ) );
        return sb.toString();
    }

    @Override
    public String getLabel() {
        return "Feedback";
    }

    @Override
    public String messageContent() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( super.messageContent() );
        if ( context != null && !context.isEmpty() ) {
            sb.append( " - re. ")
                    .append( context );
        }
        return sb.toString();
    }
}
