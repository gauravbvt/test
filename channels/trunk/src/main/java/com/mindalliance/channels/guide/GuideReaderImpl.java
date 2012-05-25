package com.mindalliance.channels.guide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Guide reader implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/25/12
 * Time: 11:10 AM
 */
public class GuideReaderImpl implements GuideReader, InitializingBean {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( GuideReaderImpl.class );


    @Autowired
    private XStreamMarshaller xStreamMarshaller;

    private Resource guideResource;

    private List<Class> supportedClasses;

    private String serverUrl;

    public void setSupportedClasses( List<Class> supportedClasses ) {
        this.supportedClasses = supportedClasses;
    }

    public void setGuideResource( Resource guideResource ) {
        this.guideResource = guideResource;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Class[] classes = new Class[ supportedClasses.size() ];
        for( int i= 0; i < supportedClasses.size(); i++ ) {
           classes[i] = supportedClasses.get( i );
        }
        xStreamMarshaller.getXStream().alias( "guide", Guide.class );
        xStreamMarshaller.getXStream().processAnnotations( classes );
    }

    @Override
    public Guide getGuide() {
        try {
            File doc = guideResource.getFile();
            FileReader reader = new FileReader( doc );
            return (Guide)xStreamMarshaller.unmarshal( new StreamSource( reader ) );
        } catch ( IOException e ) {
            LOG.error( "Failed to read guide", e );
            throw new RuntimeException( "Failed to read guide", e );
        }
    }
}
