package com.mindalliance.channels.nlp;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2009
 * Time: 9:58:29 PM
 */
public class TestSemanticProximityMatcher extends TestCase {

    public void testSemanticMatching() throws Exception {
        SemanticProximityMatcher matcher = new SemanticProximityMatcher();
        matcher.setTaggerData( new FileSystemResource( "src/main/webapp/WEB-INF/data/left3words-wsj-0-18.tagger" ) );
        matcher.setWordnetDict( new FileSystemResource( "src/main/webapp/WEB-INF/data/wordnet-2/dict" ) );
        matcher.setSimType( "shef.nlp.wordnet.similarity.JCn" );
        Logger logger = Logger.getLogger( matcher.getClass() );
        Proximity score;
        long msecs;
        for ( int i = 0; i < 2; i++ ) {
            score = matcher.semanticProximity( "", "" );
            assertTrue( score == Proximity.NONE );
            score = matcher.semanticProximity( "", "hello world" );
            assertTrue( score == Proximity.NONE );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "I flew to Europe on Delta Airlines", "an American Airlines plane crashed on take off" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.MEDIUM );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "the quick fox jumped over the lazy dog", "tea for two at the Ritz" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.LOW );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "terrorism, John Doe", "John Doe committed a violent crime" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.MEDIUM );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "pandemic flu, epidemic, quarantine", "disease, public health" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.MEDIUM );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "bomb set off in a train station", "explosive detonated on public transportation" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.MEDIUM );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "autopsy report of plague", "account of death by contagious disease" );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.HIGH );
            msecs = System.currentTimeMillis();
            score = matcher.semanticProximity( "avian influenza virus usually refers to influenza A viruses found chiefly in birds, but infections can occur in humans.", "avian influenza, sometimes avian flu, and commonly bird flu refers to influenza caused by viruses adapted to birds." );
            logger.info( "Elapsed: " + ( System.currentTimeMillis() - msecs ) + " msecs" );
            assertTrue( score == Proximity.VERY_HIGH );
        }
    }
}
