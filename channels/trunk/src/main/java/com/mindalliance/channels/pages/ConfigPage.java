// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** ... */
public class ConfigPage extends WebPage {

    private static final Logger LOGGER = LoggerFactory.getLogger( ConfigPage.class );

    private Config config;

    @SpringBean
    private ChannelsUser user;

    public ConfigPage() {
        try {
            config = new Config( loadProperties() );

            add(
                new Label( "loggedUser", user.getFullName() ),
                new Form<Config>( "config", new CompoundPropertyModel<Config>( config ) )
                    .add(
                        new TextField<String>( "home" ),
                        new TextField<String>( "dotPath" ),
                        new TextField<String>( "mail.host" ),
                        new TextField<String>( "mail.user" ),
                        new TextField<String>( "mail.password" ),
                        new TextField<String>( "mail.port" ),
                        new TextField<String>( "mail.protocol" )
                    )

            );



        } catch ( IOException e ) {
            LOGGER.error( "Unable to read or find channels.properties", e );
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unable to read configuration. Server was not configured properly ( "
                    + e.getMessage() + " )" );
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        InputStream stream = findProperties();
        try {
            properties.load( stream );
        } finally {
            if ( stream != null )
                stream.close();
        }

        return properties;
    }

    private static InputStream findProperties() throws IOException {
        File file = new File( System.getProperty( "user.home" ), "channels.properties" );
        if ( file.exists() )
            return new FileSystemResource( file ).getInputStream();

        return null;
    }

    /**
     * A summary of external configuration.
     */
    private static class Config {

        private static final String HOME = "channels.home";
        private static final String DOT_PATH = "graphRenderer.dotPath";

        private static final String DEFAULT_DOT_PATH = "/usr/bin";

        private String home;
        private String dotPath;

        private final MailConfig mail;
        private final GeoConfig geo;
//        private final SurveyConfig survey;

        private Config( Properties properties ) {
            home = properties.getProperty( HOME );
            dotPath = properties.getProperty( DOT_PATH, DEFAULT_DOT_PATH );
            mail = new MailConfig( properties );
            geo = new GeoConfig( properties );
//            survey = new SurveyConfig( properties );
        }

        public void saveTo( Properties properties ) {
            if ( home != null )
                properties.setProperty( HOME, home );
            if ( !DEFAULT_DOT_PATH.equals( dotPath ) )
                properties.setProperty( DOT_PATH, dotPath );

            mail.saveTo( properties );
            geo.saveTo( properties );
//            survey.saveTo( properties );
        }

        public MailConfig getMail() {
            return mail;
        }

        public GeoConfig getGeo() {
            return geo;
        }

/*
        public SurveyConfig getSurvey() {
            return survey;
        }
*/

        public String getHome() {
            return home;
        }

        public void setHome( String home ) {
            this.home = home;
        }

        public String getDotPath() {
            return dotPath;
        }

        public void setDotPath( String dotPath ) {
            this.dotPath = dotPath;
        }

        private static final class MailConfig {

            private static final String HOST = "mailSender.host";
            private static final String PORT = "mailSender.port";
            private static final String PROTOCOL = "mailSender.protocol";
            private static final String USERNAME = "mailSender.username";
            private static final String PASSWORD = "mailSender.password";

            private static final String DEFAULT_PROTOCOL = "smtp";

            private static final int DEFAULT_PORT = 25;

            private String host;
            private int port;
            private String protocol;
            private String user;
            private String password;

            private MailConfig( Properties properties ) {
                host = properties.getProperty( HOST );
                port = Integer.parseInt(
                    properties.getProperty( PORT, Integer.toString( DEFAULT_PORT ) ) );
                protocol = properties.getProperty( PROTOCOL, DEFAULT_PROTOCOL );
                user = properties.getProperty( USERNAME );
                password = properties.getProperty( PASSWORD );
            }

            public void saveTo( Properties properties ) {
                if ( host != null )
                    host = properties.getProperty( HOST );
                if ( port != DEFAULT_PORT )
                    port = Integer.parseInt( properties.getProperty( PORT, "25" ) );
                if ( !DEFAULT_PROTOCOL.equalsIgnoreCase( protocol ) )
                    protocol = properties.getProperty( PROTOCOL, DEFAULT_PROTOCOL );
                if ( user != null )
                    user = properties.getProperty( USERNAME );
                if ( password != null )
                    password = properties.getProperty( PASSWORD );
            }

            public String getHost() {
                return host;
            }

            public void setHost( String host ) {
                this.host = host;
            }

            public int getPort() {
                return port;
            }

            public void setPort( int port ) {
                this.port = port;
            }

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol( String protocol ) {
                this.protocol = protocol;
            }

            public String getUser() {
                return user;
            }

            public void setUser( String user ) {
                this.user = user;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword( String password ) {
                this.password = password;
            }
        }

        private static final class GeoConfig {

            private static final String KEY = "geoService.actual.googleMapsAPIKey";
            private static final String SERVER = "geoService.actual.geonamesServer";
            private static final String USER_NAME = "geoService.actual.geonamesUserName";
            private static final String TOKEN = "geoService.actual.geonamesToken";

            private String key;
            private String server;
            private String user;
            private String token;

            private GeoConfig( Properties properties ) {
                key = properties.getProperty( KEY );
                server = properties.getProperty( SERVER );
                user = properties.getProperty( USER_NAME );
                token = properties.getProperty( TOKEN );
            }

            public void saveTo( Properties properties ) {
                if ( key != null )
                    properties.setProperty( KEY, key );
                if ( server != null )
                    properties.setProperty( SERVER, server );
                if ( user != null )
                    properties.setProperty( USER_NAME, user );
                if ( token != null )
                    properties.setProperty( TOKEN, token );
            }

            public String getKey() {
                return key;
            }

            public void setKey( String key ) {
                this.key = key;
            }

            public String getServer() {
                return server;
            }

            public void setServer( String server ) {
                this.server = server;
            }

            public String getUser() {
                return user;
            }

            public void setUser( String user ) {
                this.user = user;
            }

            public String getToken() {
                return token;
            }

            public void setToken( String token ) {
                this.token = token;
            }
        }

    }
}
