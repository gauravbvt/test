package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.dao.DuplicateKeyException;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/7/12
 * Time: 1:55 PM
 */
public class ChannelsUserDaoImpl extends GenericSqlServiceImpl<ChannelsUserInfo, Long> implements ChannelsUserDao {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelsUserDaoImpl.class );

    @Autowired
    private UserContactInfoService userContactInfoService;


    @Override
    @Transactional
    public ChannelsUser createUser( String username ) throws DuplicateKeyException {
        return createUser( username, "" );
    }

    @Override
    @Transactional
    public ChannelsUser createUser( String username, String email ) throws DuplicateKeyException {
        if ( getUserNamed( username ) != null )
            throw new DuplicateKeyException();
        else
            return null;
          //  return new ChannelsUser( createUserInfo( username, "", username, email ) );
    }

    @Override
    @Transactional
    public ChannelsUserInfo createUserInfo( String username, String password, String fullName, String email ) {
        assert getUserNamed( username ) == null;
        ChannelsUserInfo userInfo = new ChannelsUserInfo(
                username,
                fullName,
                email );
        save( userInfo );
        userInfo.setPassword( password );
        return userInfo;
    }

    @Override
    @Transactional
    public void updateIdentity( ChannelsUserInfo userInfo, ChannelsUserInfo update ) {
        String newPassword = update.getPassword();
        if ( newPassword != null && !newPassword.isEmpty() )
            userInfo.setDigestedPassword( update.getPassword() );
        userInfo.setEmail( update.getEmail() );
        userInfo.setFullName( update.getFullName() );
        userInfo.setPhoto(  update.getPhoto() );
        save( userInfo );
    }

    @Override
    @Transactional
    public void deleteUser( String username,
                            ChannelsUser user,
                            PlanManager planManager ) {
        // Delete user contact info
       // userContactInfoService.removeAllChannels( user.getUserInfo() );
        //delete( user.getUserInfo() );
    }

    @Override
    @Transactional
    public ChannelsUser getUserNamed( String identifier ) {
        if ( identifier == null ) return null;
        List<ChannelsUserInfo> userInfos = findByCriteria(
                Restrictions.disjunction()
                        .add( Restrictions.eq( "username", identifier ) )
                        .add( Restrictions.eq( "email", identifier ) ) );
        // Default admin auto-created if needed
        if ( userInfos.isEmpty() ) {
            if ( identifier.equals( ChannelsUser.DEFAULT_ADMIN_USERNAME ) ) {
                ChannelsUserInfo adminUserInfo = new ChannelsUserInfo(
                        ChannelsUser.DEFAULT_ADMIN_USERNAME,
                        "Administrator",
                        "" );
                adminUserInfo.setPassword( ChannelsUser.DEFAULT_ADMIN_PASSWORD );
                adminUserInfo.setGlobalAccess( ChannelsUserInfo.ROLE_ADMIN );
                save( adminUserInfo );
                return null;
                // return new ChannelsUser( adminUserInfo );
            } else {
                return null;
            }
        } else {
            return null;
           // return new ChannelsUser( userInfos.get( 0 ) );
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<ChannelsUser> findAllUsersWithFullName( final String name, String uri ) {
        return (List<ChannelsUser>) CollectionUtils.select(
                getUsers( uri ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ChannelsUser)object).getFullName().equalsIgnoreCase( name );
                    }
                }
        );
    }

     @Override
    @Transactional( readOnly = true )
    public List<ChannelsUser> getUsers() {
        List<ChannelsUser> result = new ArrayList<ChannelsUser>();
        List<ChannelsUserInfo> userInfos = list();
        Collections.sort( userInfos, new Comparator<ChannelsUserInfo>() {
            @Override
            public int compare( ChannelsUserInfo o1, ChannelsUserInfo o2 ) {
                return o1.getUsername().compareTo( o2.getUsername() );
            }
        } );
        for ( ChannelsUserInfo userInfo : userInfos ) {
           // result.add( new ChannelsUser( userInfo ) );
        }
        return result;
    }

    @Override
    @Transactional( readOnly = true )
    public List<ChannelsUser> getPlanners( String uri ) {
        Collection<ChannelsUser> userList = getUsers();
        List<ChannelsUser> result = new ArrayList<ChannelsUser>( userList.size() );
        for ( ChannelsUser user : userList )
            if ( user.isPlannerOrAdmin( uri ) )
                result.add( user );

        return result;
    }

    @Override
    @Transactional( readOnly = true )
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
        ChannelsUser user = getUserNamed( username );
        return user == null ? username : user.getFullName();
    }

    @Override
    @Transactional
    public ChannelsUserInfo getOrMakeUserFromEmail( String email, QueryService queryService ) {
        ChannelsUserInfo userInfo = null;
        ChannelsUser userFromEmail = getUserNamed( email );
        if ( userFromEmail != null ) {
          //  userInfo = userFromEmail.getUserInfo();
        } else {
            if ( !ChannelsUtils.isValidEmailAddress( email ) ) return null;
            String newUsername = makeNewUsernameFromEmail( email );
            String password = makeNewPassword();
            try {
                ChannelsUser newUser = createUser( newUsername, email );
             //   userInfo = newUser.getUserInfo();
                userInfo.setPassword( password );
                userInfo.setGeneratedPassword( password );
            } catch ( DuplicateKeyException e ) {
                LOG.warn( "Failed to create new user " + email, e );
                return null;
            }
        }
        // Ensure user is authorized for plan in at least USER role
        String planUri = queryService.getPlan().getUri();
        if ( !( userInfo.isPlanner( planUri ) || userInfo.isUser( planUri ) ) ) {
            userInfo.setAuthorities(
                    ChannelsUserInfo.ROLE_USER,
                    planUri,
                    queryService.getPlanManager().getPlans() );
        }
        save( userInfo );
        return userInfo;
    }

    @Override
    public UserContactInfoService getUserContactInfoService() {
        return userContactInfoService;
    }

    private String makeNewUsernameFromEmail( String email ) {
        int index = email.indexOf( '@' );
        String candidate = email.substring( 0, index );
        String username = candidate;
        boolean success = false;
        int i = 2;
        do {
            if ( getUserNamed( username ) == null ) {
                success = true;
            } else {
                username = candidate + i;
                i++;
            }
        } while ( !success );
        return username;
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isParticipant( final String username, String planUri ) {
        return getUsernames( planUri ).contains( username );
    }


    @Override
    @Transactional( readOnly = true )
    public List<String> getUsernames( String uri ) {
        Collection<ChannelsUser> userList = getUsers();
        List<String> result = new ArrayList<String>( userList.size() );
        for ( ChannelsUser user : userList )
            if ( user.isParticipant( uri ) )
                result.add( user.getUsername() );
        return result;
    }

    @Override
    @Transactional( readOnly = true )
    public List<String> getUsernames() {
        List<String> result = new ArrayList<String>();
        for ( ChannelsUser user : getUsers() ) {
            result.add( user.getUsername() );
        }
        Collections.sort( result );
        return result;
    }

    @Override
    @Transactional( readOnly = true )
    public List<ChannelsUser> getUsers( String uri ) {
        Collection<ChannelsUser> collection = getUsers();
        List<ChannelsUser> result = new ArrayList<ChannelsUser>( collection.size() );
        for ( ChannelsUser user : collection )
            if ( user.isParticipant( uri ) )
                result.add( user );
        return result;
    }

    @Override
    @Transactional
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
          //  save( user.getUserInfo() );
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
    @Transactional
    public UserDetails loadUserByUsername( String identifier ) throws UsernameNotFoundException, DataAccessException {
        ChannelsUser user = getUserNamed( identifier );
        if ( user == null ) throw new UsernameNotFoundException( "User " + identifier + " not found" );
        else return user;
    }

}
