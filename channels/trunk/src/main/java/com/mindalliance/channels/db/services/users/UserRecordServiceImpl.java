package com.mindalliance.channels.db.services.users;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.DuplicateKeyException;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.db.data.users.QUserRecord;
import com.mindalliance.channels.db.data.users.UserAccess;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.repositories.UserRecordRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/13
 * Time: 12:56 PM
 */
public class UserRecordServiceImpl
        extends AbstractDataService<UserRecord>
        implements UserRecordService {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserRecordServiceImpl.class );

    @Autowired
    private UserRecordRepository repository;

    public UserRecordServiceImpl() {
    }

    @Override
    public UserRecord load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public void save( UserRecord userRecord ) {
        repository.save( userRecord );
    }

    private void delete( UserRecord userRecord ) {
        repository.delete( userRecord );
    }

    @Override
    public ChannelsUser createUser( String username, String name ) throws DuplicateKeyException {
        return createUser( username, name, "" );
    }

    @Override
    public ChannelsUser createUser( String username, String name, String email ) throws DuplicateKeyException {
        if ( getUserWithIdentity( name ) != null || (!email.isEmpty() && getUserWithIdentity( email ) != null ) )
            throw new DuplicateKeyException();
        else
            return new ChannelsUser( createUserRecord( username, name, "", name, email ) );
    }

    @Override
    public UserRecord createUserRecord( String username,
                                        String name,
                                        String password,
                                        String fullName,
                                        String email ) throws DuplicateKeyException {
        if ( getUserWithIdentity( name ) != null || (!email.isEmpty() && getUserWithIdentity( email ) != null ) )
            throw new DuplicateKeyException();
        UserRecord userRecord = new UserRecord(
                username,
                name,
                fullName,
                email );
        userRecord.setPassword( password );
        save( userRecord );
        return userRecord;
    }

    @Override
    public boolean updateUserRecord( UserRecord userRecord, UserRecord update ) {
        if ( userRecord.getUsername().equals( update.getCreatorUsername() ) )
            return false;
        if ( update.getEmail() != null
                && !update.getEmail().isEmpty()
                && !userRecord.getEmail().equals( update.getEmail() ) ) {
            if ( getUserWithIdentity( update.getEmail() ) != null ) {
                return false; // conflicting email address
            }
        }
        String newPassword = update.getPassword();
        if ( newPassword != null && !newPassword.isEmpty() )
            userRecord.setDigestedPassword( update.getPassword() );
        userRecord.setEmail( update.getEmail() );
        userRecord.setFullName( update.getFullName() );
        userRecord.setPhoto( update.getPhoto() );
        userRecord.clearAccess();
        for ( UserAccess userAccess : update.getAccessList() ) {
            userRecord.addUserAccess( new UserAccess( userAccess ) );
        }
        save( userRecord );
        return true;
    }

    @Override
    public void deleteUser( String username,
                            ChannelsUser user,
                            PlanManager planManager ) {
        // Delete user contact info
        removeAllChannels( user.getUserRecord() );
        delete( user.getUserRecord() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public ChannelsUser getUserWithIdentity( String identifier ) {
        if ( identifier == null ) return null;
        List<UserRecord> userRecords = (List<UserRecord>) CollectionUtils.select(
                getAllUserRecordsOf( identifier ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((UserRecord)object).isEnabled();
                    }
                }
        );
        // Default admin auto-created if needed
        if ( userRecords.isEmpty() ) {
            if ( identifier.equals( ChannelsUser.DEFAULT_ADMIN_USERNAME ) ) {
                UserRecord adminUserInfo = new UserRecord(
                        ChannelsUser.DEFAULT_ADMIN_USERNAME,
                        ChannelsUser.DEFAULT_ADMIN_USERNAME,
                        "Administrator",
                        "" );
                adminUserInfo.setPassword( ChannelsUser.DEFAULT_ADMIN_PASSWORD );
                adminUserInfo.makeAdmin( true );
                save( adminUserInfo );
                return new ChannelsUser( adminUserInfo );
            } else {
                return null;
            }
        } else {
            return new ChannelsUser( userRecords.get( 0 ) );
        }
    }

    private  List<UserRecord> getAllUserRecordsOf( String identifier ) {
        if ( identifier == null ) return new ArrayList<UserRecord>(  );
        QUserRecord qUserRecord = QUserRecord.userRecord;
        return toList(
                repository.findAll(
                        qUserRecord.classLabel.eq( UserRecord.class.getSimpleName() )
                                .and( qUserRecord.name.eq( identifier )
                                        .or( qUserRecord.email.eq( identifier ) ) )
                )
        );
    }


        @Override
    public UserRecord getUserRecord( String username ) {
        if ( username == null ) return null;
        QUserRecord qUserRecord = QUserRecord.userRecord;
        return repository.findOne(
                qUserRecord.classLabel.eq( UserRecord.class.getSimpleName() )
                        .and( qUserRecord.name.eq( username ) )
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ChannelsUser> findAllUsersWithFullName( final String name, String uri ) {
        return (List<ChannelsUser>) CollectionUtils.select(
                getUsers( uri ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ChannelsUser) object ).getFullName().equalsIgnoreCase( name );
                    }
                }
        );
    }

    @Override
    public List<ChannelsUser> getAllEnabledUsers() {
        List<ChannelsUser> result = new ArrayList<ChannelsUser>();
        List<UserRecord> userRecords = toList( repository.findAll() );
        Collections.sort( userRecords, new Comparator<UserRecord>() {
            @Override
            public int compare( UserRecord o1, UserRecord o2 ) {
                return o1.getUsername().compareTo( o2.getUsername() );
            }
        } );
        for ( UserRecord userRecord : userRecords ) {
            if ( !userRecord.isDisabled() )
            result.add( new ChannelsUser( userRecord ) );
        }
        return result;
    }

    @Override
    public List<ChannelsUser> getPlanners( String uri ) {
        Collection<ChannelsUser> userList = getAllEnabledUsers();
        List<ChannelsUser> result = new ArrayList<ChannelsUser>( userList.size() );
        for ( ChannelsUser user : userList )
            if ( user.isPlannerOrAdmin( uri ) )
                result.add( user );

        return result;
    }

    @Override
    public List<ChannelsUser> getCommunityPlanners( String communityUri ) {
        Collection<ChannelsUser> userList = getAllEnabledUsers();
        List<ChannelsUser> result = new ArrayList<ChannelsUser>( userList.size() );
        for ( ChannelsUser user : userList )
            if ( user.isCommunityPlanner( communityUri ) )
                result.add( user );
        return result;
    }

    @Override
    public boolean isPlanner( final String username, String planUri ) {
        return CollectionUtils.exists(
                getPlanners( planUri ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ChannelsUser) object ).getUsername().equals( username );
                    }
                } );
    }

    @Override
    public String getFullName( String username ) {
        ChannelsUser user = getUserWithIdentity( username );
        return user == null ? username : user.getFullName();
    }

    @Override
    public UserRecord getOrMakeUserFromEmail( String email, QueryService queryService ) {
        UserRecord userRecord;
        ChannelsUser userFromEmail = getUserWithIdentity( email );
        if ( userFromEmail != null ) {
            userRecord = userFromEmail.getUserRecord();
        } else {
            if ( !ChannelsUtils.isValidEmailAddress( email ) ) return null;
            String newUsername = makeNewUsernameFromEmail( email );
            String password = makeNewPassword();
            try {
                ChannelsUser newUser = createUser( newUsername, email );
                userRecord = newUser.getUserRecord();
                userRecord.setPassword( password );
                userRecord.setGeneratedPassword( password );
            } catch ( DuplicateKeyException e ) {
                LOG.warn( "Failed to create new user " + email, e );
                return null;
            }
        }
        // Ensure user is authorized for plan in at least USER role
        String planUri = queryService.getPlan().getUri();
        if ( !userRecord.isParticipant( planUri ) ) {
            userRecord.makeParticipantOf( planUri );
        }
        save( userRecord );
        return userRecord;
    }

    private String makeNewUsernameFromEmail( String email ) {
        int index = email.indexOf( '@' );
        String candidate = email.substring( 0, index );
        String username = candidate;
        boolean success = false;
        int i = 2;
        do {
            if ( getUserWithIdentity( username ) == null ) {
                success = true;
            } else {
                username = candidate + i;
                i++;
            }
        } while ( !success );
        return username;
    }

    @Override
    public boolean isParticipant( final String username, String planUri ) {
        return getUsernames( planUri ).contains( username );
    }


    @Override
    public List<String> getUsernames( String uri ) {
        Collection<ChannelsUser> userList = getAllEnabledUsers();
        List<String> result = new ArrayList<String>( userList.size() );
        for ( ChannelsUser user : userList )
            if ( user.isParticipant( uri ) )
                result.add( user.getUsername() );
        return result;
    }

    @Override
    public List<String> getUsernames() {
        List<String> result = new ArrayList<String>();
        for ( ChannelsUser user : getAllEnabledUsers() ) {
            result.add( user.getUsername() );
        }
        Collections.sort( result );
        return result;
    }

    @Override
    public List<UserRecord> getAllUserRecords() {
        return toList( repository.findAll() );
    }

    @Override
    public List<ChannelsUser> getUsers( String uri ) {
        Collection<ChannelsUser> collection = getAllEnabledUsers();
        List<ChannelsUser> result = new ArrayList<ChannelsUser>( collection.size() );
        for ( ChannelsUser user : collection )
            if ( user.isParticipant( uri ) )
                result.add( user );
        return result;
    }

    @Override
    public boolean changePassword( ChannelsUser user, PlanManager planManager, MailSender mailSender ) {
        boolean success = false;
        String newPassword = makeNewPassword();
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo( user.getEmail() );
        String fromAddress = planManager.getDefaultSupportCommunity();
        email.setFrom( fromAddress );
        email.setReplyTo( fromAddress );
        String subject = "New password";
        email.setSubject( subject );
        String text = "Dear "
                + user.getFullName()
                + " ("
                + user.getUsername()
                + "),\n\n"
                + "Your new Channels password is\n\n"
                + "--------\n"
                + newPassword
                + "\n--------"
                + "\n\nFor further assistance, please contact us at "
                + fromAddress
                + ".";
        email.setText( text );
        LOG.info( fromAddress
                + " emailing \"" + subject + "\" to "
                + user.getEmail() );
        try {
            mailSender.send( email );
            user.getUserRecord().setPassword( newPassword );
            save( user.getUserRecord() );
            success = true;
        } catch ( Exception exc ) {
            LOG.warn( fromAddress
                    + " failed to email server error ", exc );
        }
        return success;
    }


    private String makeNewPassword() {
        StringBuilder sb = new StringBuilder();
        int min = 'a';
        int max = 'z';
        Random random = new Random();
        for ( int i = 0; i < 8; i++ ) {
            int c = random.nextInt( max - min ) + min;
            sb.append( (char) c );
        }
        return sb.toString();
    }

    @Override
    public UserDetails loadUserByUsername( String identifier ) throws UsernameNotFoundException, DataAccessException {
        ChannelsUser user = getUserWithIdentity( identifier );
        if ( user == null ) throw new UsernameNotFoundException( "User " + identifier + " not found" );
        else return user;
    }

    //////////////

    @Override
    public UserRecord authorizeCommunityPlanner( String username,
                                                 ChannelsUser authorizedUser,
                                                 CommunityService communityService ) {
        ChannelsUser authorizingUser = getUserWithIdentity( username );
        String uri = communityService.getPlanCommunity().getUri();
        if ( authorizingUser != null && authorizedUser != null
                && authorizingUser.isPlannerOrAdmin( uri ) //allows admins to authorize community planners
                && !authorizedUser.isCommunityPlanner( uri ) ) {
            UserRecord userRecord = authorizedUser.getUserRecord();
            userRecord.makePlannerOf( communityService.getPlanCommunity().getUri() );
            save( userRecord );
            communityService.clearCache();
            return userRecord;
        } else {
            return null;
        }
    }


    @Override
    public boolean resignAsCommunityPlanner( String username, ChannelsUser planner, CommunityService communityService ) {
        ChannelsUser user = getUserWithIdentity( username );
        String uri = communityService.getPlanCommunity().getUri();
        if ( ( user != null
                && ( user.isPlannerOrAdmin( uri ) )
                && getCommunityPlanners( uri ).size() > 1 )
                && planner.isCommunityPlanner(  uri ) ) {
            UserRecord userRecord = planner.getUserRecord();
            userRecord.makeParticipantOf( uri );
            save( userRecord );
            communityService.clearCache();
            return true;
        }
        return false;
    }

    @Override
    public void addFounder( ChannelsUser founder, PlanCommunity planCommunity ) {
        List<ChannelsUser> planners = getCommunityPlanners( planCommunity.getUri() );
        assert planners.isEmpty(); // Make sure founder is first planner
        UserRecord userRecord = founder.getUserRecord();
        userRecord.makePlannerOf( planCommunity.getUri() );
        save( userRecord );
    }


    ///////// MESSAGEABLE

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Channel> findChannels( UserRecord userRecord, CommunityService communityService ) {
        List<Channel> channels = new ArrayList<Channel>();
        for ( ContactInfo contactInfo : userRecord.getContactInfoList() ) {
            try {
                TransmissionMedium medium = TransmissionMedium.getUNKNOWN();
                if ( communityService != null ) {
                    // check if medium still valid
                    medium = communityService.find(
                            TransmissionMedium.class,
                            contactInfo.getTransmissionMediumId() );
                }
                channels.add( new Channel( medium, contactInfo.getAddress() ) );
            } catch ( NotFoundException e ) {
                // ignore
            }
        }
        return channels;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void setAddress( UserRecord userRecord, Channel channel, String address ) {
        ContactInfo contactInfo = new ContactInfo( channel );
        userRecord.removeContactInfo( contactInfo );
        contactInfo.setAddress( address );
        userRecord.addContactInfo( contactInfo );
        save( userRecord );
    }

    @Override
    public void addChannel( String username, UserRecord user, Channel channel ) {
        ContactInfo contactInfo = new ContactInfo( channel );
        user.addContactInfo( contactInfo );
        save( user );
    }

    @Override
    public void removeChannel( UserRecord userRecord, Channel channel ) {
        ContactInfo contactInfo = new ContactInfo( channel );
        userRecord.removeContactInfo( contactInfo );
        save( userRecord );
    }

    @Override
    public void removeAllChannels( UserRecord userRecord ) {
        for ( Channel channel : findChannels( userRecord, null ) ) {
            // null = don't do validation of channel media
            userRecord.removeContactInfo( new ContactInfo( channel ) );
        }
        save( userRecord );
    }


}
