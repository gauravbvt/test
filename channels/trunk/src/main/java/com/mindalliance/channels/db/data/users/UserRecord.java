package com.mindalliance.channels.db.data.users;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User record.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/13
 * Time: 10:27 AM
 */
@Document(collection = "users")
public class UserRecord extends AbstractChannelsDocument implements Messageable {

    public static final String ACCESS_PRIVILEGES_CHANGED = "access privileges changed";

    /**
     * Short hand username for all planners in current plan.
     */
    public static final String PLANNERS = "__planners__";

    /**
     * Short hand username for all users in current plan.
     */
    public static final String USERS = "__users__";

    /**
     * The admin role string.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * The planner role string.
     */
    public static final String ROLE_PLANNER = "ROLE_PLANNER";

    /**
     * The user role string. Implied if user is listed in user list.
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * User notified of change in access privileges?
     */
    private Set<UserAccess> accessChangesToNotify = new HashSet<UserAccess>();


    /**
     * User unique name.
     */
    private String name;
    /**
     * The email.
     */
    private String email = "";

    /**
     * The hash-encoded password.
     */
    private String password;

    /**
     * Clear password generated when automatically creating a user.
     */
    private String generatedPassword;

    /**
     * The fullName.
     */
    private String fullName = "";

    private List<UserAccess> accessList = new ArrayList<UserAccess>();

    private String photo;

    private List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

    public UserRecord() {
    }

    public UserRecord( String creatorUsername, String name ) {
        super( creatorUsername );
        assert name != null;
        this.name = name;
    }

    public UserRecord( String creatorUsername, String name, String fullName, String email ) {
        this( creatorUsername, name );
        this.fullName = fullName;
        setEmail( email == null ? "" : email );
    }

    public UserRecord( String creatorUsername, String name, String fullName, String email, UserAccess.UserRole userRole ) {
        this( creatorUsername, name );
        this.fullName = fullName;
        setEmail( email == null ? "" : email );
        addUserAccess( new UserAccess( userRole ) );
    }

    public UserRecord( String creatorUsername, UserRecord userRecord ) {
        this( creatorUsername,
                userRecord.getUsername(),
                userRecord.getFullName(),
                userRecord.getEmail() );
        password = userRecord.getPassword();
        for ( UserAccess userAccess : userRecord.getAccessList() ) {
            addUserAccess( new UserAccess( userAccess ) );
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Here, username does not refer to the creator of the user record but to the name of the record.
     *
     * @return a string
     */
    public String getUsername() {
        return name;
    }

    public String getCreatorUsername() {
        return super.getUsername();
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail( String email ) {
        if ( ChannelsUtils.isValidEmailAddress( email ) )
            this.email = email;
    }

    public String getFullName() {
        return fullName == null ? "" : fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto( String photo ) {
        this.photo = photo;
    }

    /**
     * Set the password.
     *
     * @param password unencrypted.
     */
    public void setPassword( String password ) {
        if ( password != null
                && !password.trim().isEmpty() ) {
            this.password = digestPassword( password.trim() );
        }
    }

    public void setDigestedPassword( String passwordDigest ) {
        this.password = passwordDigest;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public void setGeneratedPassword( String generatedPassword ) {
        this.generatedPassword = generatedPassword;
    }

    public List<ContactInfo> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList( List<ContactInfo> contactInfoList ) {
        this.contactInfoList = contactInfoList;
    }

    static public String digestPassword( String password ) {
        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(
                "sha",
                true );
        return encoder.encodePassword(
                password,
                null );
    }

    public void addContactInfo( ContactInfo contactInfo ) {
        if ( !contactInfoList.contains( contactInfo ) )
            contactInfoList.add( contactInfo );
    }

    public void removeContactInfo( ContactInfo contactInfo ) {
        contactInfoList.remove( contactInfo );
    }

    public List<UserAccess> getAccessList() {
        return accessList == null
                ? new ArrayList<UserAccess>()
                : accessList;
    }

    @SuppressWarnings("unchecked")
    public List<UserAccess> getAccessChangesToNotify( final String uri ) {
        return (List<UserAccess>) CollectionUtils.select(
                accessChangesToNotify,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isForContext( uri );
                    }
                } );
    }

    @SuppressWarnings("unchecked")
    public void resetAccessChangeToNotify( String uri ) {
        List<UserAccess> userAccesses = getAccessChangesToNotify( uri );
        accessChangesToNotify.removeAll( userAccesses );
    }

    public void addAccessChangeToNotify( UserAccess userAccess ) {
        accessChangesToNotify.add( userAccess );
    }


    public boolean isDisabled() {
        return CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isDisabled();
                    }
                }
        );
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public boolean isAdmin() {
        return !isDisabled()
                && CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isAdmin();
                    }
                }
        );
    }

    public boolean isPlannerOrAdmin( String uri ) {
        return isAdmin() || hasPlannerAccess( uri );
    }

    public boolean isCommunityPlanner( String communityUri ) {
        return hasPlannerAccess( communityUri );
    }


    public boolean isParticipant( String uri ) {
        return isAdmin() || isPlannerOrAdmin( uri ) || hasParticipantAccess( uri );
    }

    public void makeDisabled( boolean val ) {
        if ( val ) {
            if ( !isDisabled() )
                addUserAccess( new UserAccess( UserAccess.UserRole.Disabled ) );
        } else {
            removeUserAccess( new UserAccess( UserAccess.UserRole.Disabled ) );
        }
    }

    public void makeAdmin( boolean val ) {
        if ( !isDisabled() ) {
            if ( val ) {
                addUserAccess( new UserAccess( UserAccess.UserRole.Admin ) );
            } else {
                removeUserAccess( new UserAccess( UserAccess.UserRole.Admin ) );
            }
        }
    }

    public void makePlannerOf( String contextUri ) {
        if ( !isDisabled() && !hasPlannerAccess( contextUri ) ) {
            UserAccess plannerAccess = new UserAccess( contextUri, UserAccess.UserRole.Planner );
            removeUserAccess( new UserAccess( contextUri, UserAccess.UserRole.Participant ) );
            addUserAccess( plannerAccess );
            addAccessChangeToNotify( plannerAccess );
        }
    }

    public void makeParticipantOf( String contextUri ) {
        if ( !isDisabled() ) {
            boolean demoted = removeUserAccess( new UserAccess( contextUri, UserAccess.UserRole.Planner ) );
            UserAccess participantAccess = new UserAccess( contextUri, UserAccess.UserRole.Participant );
            addUserAccess( new UserAccess( contextUri, UserAccess.UserRole.Participant ) );
            if ( demoted )
                addAccessChangeToNotify( participantAccess );
        }
    }

    private boolean removeUserAccess( UserAccess userAccess ) {
        return accessList != null && accessList.remove( userAccess );
    }

    public void addUserAccess( UserAccess userAccess ) {
        if ( accessList == null ) {
            accessList = new ArrayList<UserAccess>();
        }
        if ( userAccess.getContextUri() != null ) {
            clearAccess( userAccess.getContextUri() );
        }
        if ( !accessList.contains( userAccess ) )
            accessList.add( userAccess );
    }

    private boolean hasParticipantAccess( final String uri ) {
        return !isDisabled()
                && CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isParticipantOf( uri );
                    }
                }
        );
    }

    private boolean hasPlannerAccess( final String uri ) {
        return !isDisabled()
                && CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isPlannerOf( uri );
                    }
                }
        );
    }

    public void clearAccess() {
        accessList = new ArrayList<UserAccess>();
    }

    public void clearAccess( String uri ) {
        List<UserAccess> accesses = findAccessesTo( uri );
        for ( UserAccess userAccess : accesses ) {
            removeUserAccess( userAccess );
        }
    }

    @SuppressWarnings("unchecked")
    private List<UserAccess> findAccessesTo( final String uri ) {
        return (List<UserAccess>) CollectionUtils.select(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isForContext( uri );
                    }
                }
        );
    }

    //// MESSAGEABLE

    @Override
    public String getLabel() {
        return "Change to access privileges ";
    }

    @Override
    public String getContent( String topic, Messageable.Format format, CommunityService communityService ) {
        if ( topic.equals( ACCESS_PRIVILEGES_CHANGED ) ) {
            PlanCommunity planCommunity = communityService.getPlanCommunity();
            StringBuilder sb = new StringBuilder();
            sb.append( "Your access privileges have changed in " )
                    .append( planCommunity.isDomainCommunity() ? "plan " : "community " )
                    .append( planCommunity.getName() )
                    .append( ":\n\n" );
            for ( UserAccess userAccessChange : getAccessChangesToNotify( planCommunity.getUri() ) ) {
                sb.append( "to " )
                        .append( userAccessChange.getUserRole().name() )
                        .append( " on " )
                        .append( new SimpleDateFormat( DATE_FORMAT_STRING ).format( userAccessChange.getDate() ) )
                        .append( "\n" );
            }
            return sb.toString();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        if ( topic.equals( ACCESS_PRIVILEGES_CHANGED ) ) {
            List<String> usernames = new ArrayList<String>();
            usernames.add( name );
            return usernames;
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getSubject( String topic, Messageable.Format format, CommunityService communityService ) {
        if ( topic.equals( ACCESS_PRIVILEGES_CHANGED ) ) {
            return "Access privileges changed";
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    public UserAccess getUserAccessForContext( final String uri ) {
        return (UserAccess) CollectionUtils.find(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isForContext( uri );
                    }
                } );
    }

    public void setUserAccessForContext( String uri, UserAccess.UserRole userRole ) {
        clearAccess( uri );
        if ( userRole != null ) {
            addUserAccess( new UserAccess( uri, userRole ) );
        }
    }

    public void setUserRole( UserAccess.UserRole userRole ) {
        assert userRole == UserAccess.UserRole.Admin || userRole == UserAccess.UserRole.Disabled;
        addUserAccess( new UserAccess( userRole ) );
    }

    public boolean isPlanner() {
        return CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isPlanner();
                    }
                } );
    }

    public boolean isParticipant() {
        return CollectionUtils.exists(
                getAccessList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserAccess) object ).isParticipant();
                    }
                } );
    }
}

