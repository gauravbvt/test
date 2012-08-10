// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.services.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Address;
import com.mindalliance.playbook.model.AddressMedium;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.FacebookMedium;
import com.mindalliance.playbook.model.IMMedium;
import com.mindalliance.playbook.model.LinkedInMedium;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.PhoneMedium;
import com.mindalliance.playbook.model.SkypeMedium;
import com.mindalliance.playbook.model.TwitterMedium;
import com.mindalliance.playbook.services.ContactMerger;
import com.mindalliance.playbook.services.SocialHub;
import com.mindalliance.playbook.services.SocialProvider;
import org.apache.commons.collections.map.ListOrderedMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.WorkEntry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.gdata.contact.ContactOperations;
import org.springframework.social.google.api.gdata.contact.Email;
import org.springframework.social.google.api.gdata.contact.Phone;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.linkedin.api.ImAccount;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
import org.springframework.social.linkedin.api.PhoneNumber;
import org.springframework.social.linkedin.api.Position;
import org.springframework.social.linkedin.api.TwitterAccount;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Application-level connection services to social providers.
 */
@Service
public class SocialHubImpl implements InitializingBean, SocialHub, SignInAdapter {

    private static final Logger LOG = LoggerFactory.getLogger( SocialHubImpl.class );
    
    private Map<String,SocialProvider> providerMap = new ListOrderedMap();
    private SocialProvider[] providers;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ContactMerger contactMerger;

    @Autowired
    private ConnectionFactoryRegistry registry;

    @Autowired
    private JdbcUsersConnectionRepository repository;
    
    @Autowired
    private TokenBasedRememberMeServices rememberMeServices;

    @Value( "${facebook.id}" )
    private String facebookId = "";

    @Value( "${linkedIn.id}" )
    private String linkedInId = "";

    @Value( "${twitter.id}" )
    private String twitterId = "";

    @Value( "${google.id}" )
    private String googleId;

    @Value( "${facebook.secret}" )
    private String facebookSecret = "";

    @Value( "${linkedIn.secret}" )
    private String linkedInSecret = "";

    @Value( "${twitter.secret}" )
    private String twitterSecret = "";

    @Value( "${google.secret}" )
    private String googleSecret;

    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private SessionFactory sessionFactory;
       
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public void afterPropertiesSet() {
        setProviders(
            new TwitterProvider( twitterId, twitterSecret ),
            new FacebookProvider( facebookId, facebookSecret ),
            new LinkedInProvider( linkedInId, linkedInSecret ),
            new GoogleProvider( googleId, googleSecret )
        );
        
        for ( SocialProvider provider : providerMap.values() )
            provider.registerFactory( registry );
        

        repository.setConnectionSignUp(
            new ConnectionSignUp() {
                @Override
                public String execute( Connection<?> connection ) {
                    return signUp( connection ).getUserKey();
                }
            } );
    }
    
    @Override
    public Account signUp( Connection<?> connection ) {
        try {
            ConnectionKey key = connection.getKey();
            String providerId = key.getProviderId();
            String providerUserId = key.getProviderUserId();

            Account account = accountDao.getCurrentAccount();
            if ( account == null )
                account = accountDao.findByUserId( providerId, providerUserId );

            if ( account == null ) {
                Contact contact = newContact( connection );
                contact.setMain( true );
                account = new Account( providerId, providerUserId, contact );
                account.setConfirmed( true );
                accountDao.save( account );
                LOG.debug( "Signed up {}", account );

            } else {
                Contact me = account.getOwner();
                me.merge( newContact( connection ) );
                contactDao.save( me );
                LOG.debug( "Added {} connection to {}", key, account );
            }
            
            return account;
            
        } catch ( RuntimeException e ) {
            LOG.error( "Exception", e );
            throw e;
        }
    }

    @Override
    public String signIn( String userId, Connection<?> connection, NativeWebRequest request ) {

        try {
            Account account = accountDao.findByUserKey( userId );
            UserDetails details = accountDao.getDetails( account );
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                details,
                null,
                details.getAuthorities() );
            SecurityContextHolder.getContext().setAuthentication( authentication );

            LOG.debug( "Signed in {}", account );

            DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getAttribute(
                "SPRING_SECURITY_SAVED_REQUEST", RequestAttributes.SCOPE_SESSION );
            request.removeAttribute( "SPRING_SECURITY_SAVED_REQUEST", RequestAttributes.SCOPE_SESSION );

            rememberMeServices.setAlwaysRemember( true );
            rememberMeServices.onLoginSuccess(
                (HttpServletRequest) request.getNativeRequest(),
                (HttpServletResponse) request.getNativeResponse(),
                authentication );

            // Do a sync on sign-in
            scheduleSync( account, connection );

            return savedRequest == null ? "/" : savedRequest.getServletPath();

        } catch ( RuntimeException e ) {
            LOG.error( "Exception", e );
            throw e;
        }
    }

    //  TODO move this around...
    private void scheduleSync( final Account account ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                syncContacts( account );
            }
        } );
    }

    //  TODO move this around...
    private void scheduleSync( final Account account, final Connection<?> connection ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                syncContacts( account, connection );
            }
        } );
    }
    public void setProviders( SocialProvider... providers ) {
        this.providers = providers;
        for ( SocialProvider socialProvider : providers )
            providerMap.put( socialProvider.getProviderId(), socialProvider );
    }

    @Override
    public void syncContacts( Account account ) {
        String accountName = account.toString();
        LOG.debug( "Starting sync of {}", accountName );
        accountDao.setCurrentAccount( account );

        Session session = sessionFactory.openSession();
        try {
            TransactionSynchronizationManager.bindResource( sessionFactory, new SessionHolder( session ) );

            MultiValueMap<String,Connection<?>> connections =
                repository.createConnectionRepository( account.getUserKey() ).findAllConnections();

            // Process provider in the order defined
            for ( SocialProvider provider : providers )
                for ( Connection<?> connection : connections.get( provider.getProviderId() ) ) {
                    LOG.debug( "Syncing contacts for {}", connection.getKey() );
                    provider.mergeContacts( contactMerger, connection );
                }

        } finally {
            TransactionSynchronizationManager.unbindResource( sessionFactory );
            session.close();
        }
        LOG.debug( "Done with sync for {}", accountName );
    }

    @Override
    public void syncContacts( Account account, Connection<?> connection ) {
        String accountName = account.toString();
        ConnectionKey key = connection.getKey();
        LOG.debug( "Starting sync for {} ({})", accountName, key );
        accountDao.setCurrentAccount( account );

        Session session = sessionFactory.openSession();
        try {
            TransactionSynchronizationManager.bindResource( sessionFactory, new SessionHolder( session ) );
            providerMap.get( key.getProviderId() ).mergeContacts( contactMerger, connection );

        } finally {
            TransactionSynchronizationManager.unbindResource( sessionFactory );
            session.close();
        }
        LOG.debug( "Done with sync for {} ({})", accountName, key );
    }

    @Override
    public boolean isFacebookEnabled() {
        SocialProvider provider = providerMap.get( FacebookProvider.ID );
        return provider != null && provider.isEnabled();
    }

    @Override
    public boolean isLinkedInEnabled() {
        SocialProvider provider = providerMap.get( LinkedInProvider.ID );
        return provider != null && provider.isEnabled();
    }

    @Override
    public boolean isTwitterEnabled() {
        SocialProvider provider = providerMap.get( TwitterProvider.ID );
        return provider != null && provider.isEnabled();
    }

    @Override
    public boolean isGoogleEnabled() {
        SocialProvider provider = providerMap.get( GoogleProvider.ID );
        return provider != null && provider.isEnabled();
    }

    private Contact newContact( Connection<?> connection ) {

        ConnectionKey key = connection.getKey();
        String providerId = key.getProviderId();

        if ( providerMap.containsKey( providerId ) )
            return providerMap.get( providerId ).newContact( connection, key.getProviderUserId() );

        else {
            UserProfile profile = connection.fetchUserProfile();

            Contact contact = new Contact( new EmailMedium( null, profile.getEmail() ) );
            contact.setGivenName( profile.getFirstName() );
            contact.setFamilyName( profile.getLastName() );
            contact.setPhoto( new ImageAdapter( connection.getImageUrl() ).getBytes() );

            return contact;
        }
    }

    //=======================================
    public static class ImageAdapter {
        
        
        private BufferedImage image;

        public ImageAdapter( String url ) {
            try {
                image = ImageIO.read( new URL( url ) );

            } catch ( MalformedURLException ignored ) {
                LOG.error( "Invalid URL: {}", url );

            } catch ( IOException ignored ) {
                LOG.debug( "Unable to read image: {}", url );
            }
        }

        public ImageAdapter( byte[] bytes ) {
            try {
                image = ImageIO.read( new ByteArrayInputStream( bytes ) );
                
            } catch ( IOException e ) {
                LOG.error( "Invalid image bytes", e );
            }
        }
        
        public byte[] getBytes() {
            if ( image != null )
                try {
                    // TODO crop and scale to ratio. Enlarge when image is too small...
    
                    BufferedImage scaledInstance = getScaledInstance( 
                        image, 80, 80, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
    
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write( scaledInstance, "jpg", out );
                    return out.toByteArray();
                    
                } catch ( IOException e ) {
                    LOG.debug( "Unable to convert image", e );
                }

            return null;
        }

        /**
         * Convenience method that returns a scaled instance of the provided {@code BufferedImage}.
         * <p/>
         * (Code stolen from <a href="http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html">here</a>)
         *
         *
         * @param img the original image to be scaled
         * @param targetWidth the desired width of the scaled instance, in pixels
         * @param targetHeight the desired height of the scaled instance, in pixels
         * @param hint one of the rendering hints that corresponds to {@code RenderingHints.KEY_INTERPOLATION} (e.g. {@code
         * RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR}, {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR}, {@code
         * RenderingHints.VALUE_INTERPOLATION_BICUBIC})
         * @return a scaled version of the original {@code BufferedImage}
         */
        private static BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight, Object hint ) {
            int type = img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : 
                                                                      BufferedImage.TYPE_INT_ARGB;
            BufferedImage ret = img;
            int w = img.getWidth();
            int h = img.getHeight();

            // If image ratio is taller than target's, adjust target width so as not to lose any parts
            if ( h * targetWidth > w * targetHeight )
                targetWidth = w * targetHeight / h ;

            else if ( h * targetWidth < w * targetHeight ) {
                // Image ratio is longer than target, crop sides so height is full
                int w2 = h * targetWidth / targetHeight ;
                
                BufferedImage tmp = new BufferedImage( w2, h, type );
                Graphics2D g2 = tmp.createGraphics();
                g2.drawImage( ret, ( w2 - w ) / 2, 0, w, h, null );
                g2.dispose();

                ret = tmp;
            }
            
            if ( h < targetHeight ) {
                // Image is smaller than target, scale up

                BufferedImage tmp = new BufferedImage( targetWidth, targetHeight, type );
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, hint );
                g2.drawImage( ret, 0, 0, targetWidth, targetHeight, null );
                g2.dispose();
                
                return tmp;
            }
            
            while ( w > targetWidth || h > targetHeight ) {
                w /= 2;
                if ( w < targetWidth )
                    w = targetWidth;

                h /= 2;
                if ( h < targetHeight )
                    h = targetHeight;
    
                BufferedImage tmp = new BufferedImage( w, h, type );
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, hint );
                g2.drawImage( ret, 0, 0, w, h, null );
                g2.dispose();
    
                ret = tmp;
            }
               
            return ret;
        }
    }

    //=======================================
    public static class GoogleProvider implements SocialProvider {

        public static final String ID = "google";

        private String id;
        private String secret;

        public GoogleProvider( String id, String secret ) {
            this.id = id;
            this.secret = secret;
        }

        @Override
        public String getProviderId() {
            return ID;
        }

        @Override
        public void registerFactory( ConnectionFactoryRegistry registry ) {
            if ( isEnabled() )
                registry.addConnectionFactory(
                    new GoogleConnectionFactory( id, secret ) );
        }

        @Override
        public void mergeContacts( ContactMerger merger, Connection<?> connection ) {
            ContactOperations operations = ((Google) connection.getApi()).contactOperations();
            for ( org.springframework.social.google.api.gdata.contact.Contact profile : operations.getContactList() ) {
                Contact contact = newContact( profile );
                if ( contact != null ) 
                    merger.merge( contact );
            }
        }

        private static Contact newContact( org.springframework.social.google.api.gdata.contact.Contact profile ) {
            List<Email> emails = profile.getEmails();
            List<Phone> phones = profile.getPhones();

            boolean isUrl = profile.getId().startsWith( "http" );
            if ( emails.isEmpty() && phones.isEmpty() && isUrl )
                return null;

            Medium medium = !isUrl ?            new EmailMedium( null, profile.getId() )
                          : !emails.isEmpty() ? new EmailMedium( emails.get( 0 ).getLabel(), 
                                                                 emails.get( 0 ).getAddress() )
                                              : new PhoneMedium( phones.get( 0 ).getLabel(), 
                                                                 phones.get( 0 ).getNumber() );
            Contact contact = new Contact( medium );

            contact.setPrefixes( profile.getNamePrefix() );
            contact.setGivenName( profile.getFirstName() );
            contact.setAdditionalNames( profile.getMiddleName() );
            contact.setFamilyName( profile.getLastName() );
            contact.setSuffixes( profile.getNameSuffix() );

            for ( Email email : emails ) {
                EmailMedium using = new EmailMedium( email.getLabel(), email.getAddress() );
                using.setPreferred( email.isPrimary() );
                contact.addMedium( using );
            }

            for ( Phone phone : phones ) {
                PhoneMedium phoneMedium = new PhoneMedium( phone.getLabel(), phone.getNumber() );
                phoneMedium.setPreferred( phone.isPrimary() );
                contact.addMedium( phoneMedium );
            }
            
            contact.setPhoto( new ImageAdapter( profile.getPictureUrl() ).getBytes() );

            return contact;
        }

        @Override
        public Contact newContact( Connection<?> connection, String userId ) {
            ContactOperations contactOperations = ((Google) connection.getApi()).contactOperations();
            org.springframework.social.google.api.gdata.contact.Contact contact = contactOperations.getContact(
                "https://www.google.com/m8/feeds/contacts/default/full" );
            return newContact( contact );
        }

        @Override
        public boolean isEnabled() {
            return id != null && !id.isEmpty();
        }
    }
    
    //=======================================
    public static class LinkedInProvider implements SocialProvider {

        public static final String ID = "linkedin";
        
        private String id;
        private String secret;

        public LinkedInProvider( String id, String secret ) {
            this.id = id;
            this.secret = secret;
        }

        @Override
        public String getProviderId() {
            return ID;
        }

        @Override
        public void registerFactory( ConnectionFactoryRegistry registry ) {
            if ( isEnabled() )
                registry.addConnectionFactory(
                    new LinkedInConnectionFactory( id, secret ) );
        }

        @Override
        public void mergeContacts( ContactMerger merger, Connection<?> connection ) {
            LinkedIn api = (LinkedIn) connection.getApi();
            for ( LinkedInProfile profile : api.connectionOperations().getConnections() )
                if ( !"private".equals( profile.getLastName() ) )
                    merger.merge( newContact( api, profile ) );
        }

        @Override
        public Contact newContact( Connection<?> connection, String userId ) {
            return newContact( (LinkedIn) connection.getApi(), userId );
        }


        private static Contact newContact( LinkedIn api, LinkedInProfile profile ) {
            try {
                return newContact( profile, api.profileOperations().getProfileFullById( profile.getId() ) );

            } catch ( RuntimeException e ) {
                LOG.warn( "Exception getting full profile of " + profile.getFirstName() + ' ' + profile.getLastName(), e );
                return newContact( profile, null );
            }
        }

        private static Contact newContact( LinkedIn api, String userId ) {
            LinkedInProfile profile = api.profileOperations().getProfileById( userId );

            try {
                return newContact( profile, api.profileOperations().getProfileFullById( userId ) );

            } catch ( RuntimeException e ) {
                LOG.warn( "Exception getting full profile of " + profile.getFirstName() + ' ' + profile.getLastName(), e );

                return newContact( profile, null );
            }
        }

        private static Contact newContact( LinkedInProfile myProfile, LinkedInProfileFull fullProfile ) {

            // http://www.linkedin.com/profile?viewProfile=&key=1951574&authToken=PyCo&authType=name&trk=api*a172251
            // *s180487*
            String url = myProfile.getSiteStandardProfileRequest().getUrl();
            int i = url.indexOf( "&key=" );

            LinkedInMedium medium = new LinkedInMedium(
                myProfile.getId(),
                myProfile.getFirstName() + ' ' + myProfile.getLastName(),
                "https://www.linkedin.com/msgToConns?displayCreate=&connId=" + url.substring( i + 5, url.indexOf( "&", i + 5 ) )
            );
            Contact contact = new Contact( medium );

            contact.setGivenName( myProfile.getFirstName() );
            contact.setFamilyName( myProfile.getLastName() );

            String profilePictureUrl = myProfile.getProfilePictureUrl();
            if ( profilePictureUrl != null )
                contact.setPhoto( new ImageAdapter( profilePictureUrl ).getBytes() );

            if ( fullProfile != null ) {
                List<Position> positions = fullProfile.getPositions();
                if ( positions != null )
                    for ( Position position : positions )
                        if ( position.getIsCurrent() ) {
                            contact.setOrganization( position.getCompany().getName() );
                            contact.setTitle( position.getTitle() );
                        }

                List<ImAccount> imAccounts = fullProfile.getImAccounts();
                if ( imAccounts != null )
                    for ( ImAccount imAccount : imAccounts )
                        contact.addMedium(
                            "skype".equals( imAccount.getImAccountType() ) ?
                            new SkypeMedium( imAccount.getImAccountName() ) :
                            new IMMedium( imAccount.getImAccountType(), imAccount.getImAccountName() ) );

                List<TwitterAccount> twitterAccounts = fullProfile.getTwitterAccounts();
                if ( twitterAccounts != null )
                    for ( TwitterAccount twitterAccount : twitterAccounts )
                        contact.addMedium( new TwitterMedium( twitterAccount.getProviderAccountName() ) );

                List<PhoneNumber> phoneNumbers = fullProfile.getPhoneNumbers();
                if ( phoneNumbers != null )
                    for ( PhoneNumber phoneNumber : phoneNumbers )
                        contact.addMedium( new PhoneMedium( phoneNumber.getPhoneType(), phoneNumber.getPhoneNumber() ) );

                String mainAddress = fullProfile.getMainAddress();
                if ( mainAddress != null && !mainAddress.isEmpty() )
                    contact.addMedium( new AddressMedium( null, new Address( mainAddress ) ) );
            }

            return contact;
        }

        @Override
        public boolean isEnabled() {
            return id != null && !id.isEmpty();
        }
    }

    //=======================================
    public static class FacebookProvider implements SocialProvider {

        public static final String ID = "facebook";

        private String id;
        private String secret;

        public FacebookProvider( String id, String secret ) {
            this.id = id;
            this.secret = secret;
        }

        @Override
        public String getProviderId() {
            return ID;
        }

        @Override
        public void registerFactory( ConnectionFactoryRegistry registry ) {
            if ( isEnabled() )
                registry.addConnectionFactory(
                    new FacebookConnectionFactory( id, secret ) );
        }

        @Override
        public void mergeContacts( ContactMerger merger, Connection<?> connection ) {
            Facebook api = (Facebook) connection.getApi();
            for ( Reference friend : api.friendOperations().getFriends() )
                merger.merge( newContact( api, friend.getId() ) );
        }


        private static Contact newContact( Facebook facebook, String userId ) {

            FacebookProfile myProfile = facebook.userOperations().getUserProfile( userId );

            Contact contact = new Contact( new FacebookMedium( userId, myProfile.getName() ) );

            contact.setGivenName( myProfile.getFirstName() );
            contact.setAdditionalNames( myProfile.getMiddleName() );
            contact.setFamilyName( myProfile.getLastName() );
            contact.setPhoto( new ImageAdapter( facebook.userOperations().getUserProfileImage( userId ) ).getBytes() );

            String email = myProfile.getEmail();
            if ( email != null )
                contact.addMedium( new EmailMedium( null, email ) );

            List<WorkEntry> work = myProfile.getWork();
            if ( work != null )
                for ( WorkEntry entry : work )
                    if ( "0000-00".equals( entry.getEndDate() ) )
                        contact.setOrganization( entry.getEmployer().getName() );

            return contact;
        }

        @Override
        public Contact newContact( Connection<?> connection, String userId ) {
            return newContact( (Facebook) connection.getApi(), userId );
        }

        @Override
        public boolean isEnabled() {
            return id != null && !id.isEmpty();
        }
    }

    //=======================================
    public static class TwitterProvider implements SocialProvider {

        public static final String ID = "twitter";

        private String id;
        private String secret;

        public TwitterProvider( String id, String secret ) {
            this.id = id;
            this.secret = secret;
        }

        @Override
        public String getProviderId() {
            return ID;
        }

        @Override
        public void registerFactory( ConnectionFactoryRegistry registry ) {
            if ( isEnabled() )
                registry.addConnectionFactory( new TwitterConnectionFactory( id, secret ) );
        }

        @Override
        public void mergeContacts( ContactMerger merger, Connection<?> connection ) {
            Twitter api = (Twitter) connection.getApi();
            for ( Long friendId : api.friendOperations().getFollowerIds() )
                merger.merge( newContact( api, friendId ) );
        }

        @Override
        public Contact newContact( Connection<?> connection, String userId ) {
            return newContact( (Twitter) connection.getApi(), Long.valueOf( userId ) );
        }

        @Override
        public boolean isEnabled() {
            return id != null && !id.isEmpty();
        }

        private static Contact newContact( Twitter twitter, long userId ) {
            TwitterProfile profile = twitter.userOperations().getUserProfile( userId );
            String screenName = profile.getScreenName();

            Contact contact = new Contact( new TwitterMedium( screenName ) );

            String name = profile.getName();
            StringTokenizer t = new StringTokenizer( name, ", " );
            int count = t.countTokens();
            switch ( count ) {
            case 1:
                contact.setFamilyName( t.nextToken() );
                break;
            case 2:
                contact.setGivenName( t.nextToken() );
                contact.setFamilyName( t.nextToken() );
                break;

            case 0:
                break;

            default:
                contact.setGivenName( t.nextToken() );
                StringBuilder b = new StringBuilder();
                for ( int i = 2; i < count; i++ ) {
                    if ( i > 2 )
                        b.append( ' ' );
                    b.append( t.nextToken() );
                }
                contact.setAdditionalNames( b.toString() );
                contact.setFamilyName( t.nextToken() );
                break;
            }

            String imageUrl = profile.getProfileImageUrl();
            if ( imageUrl != null )
                contact.setPhoto( new ImageAdapter( imageUrl ).getBytes() );
            return contact;
        }
    }
    
}
