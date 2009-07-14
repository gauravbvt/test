package com.mindalliance.channels.nlp;

import junit.framework.TestCase;
import org.springframework.core.io.FileSystemResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2009
 * Time: 9:58:29 PM
 */
public class TestSemanticProximityMatcher extends TestCase {

    private List<Object[]> data = new ArrayList<Object[]>();


    public void setUp() {




        add( "", "", Proximity.NONE );
        add( "", "hello world", Proximity.NONE );
        add( "I flew to Europe on Delta Airlines", "an American Airlines plane crashed on take off", Proximity.LOW );
        add( "the quick fox jumped over the lazy dog", "tea for two at the Ritz", Proximity.NONE );
        add( "terrorism, John Doe", "John Doe committed a violent crime", Proximity.HIGH );
        add( "autopsy report of plague", "account of death by contagious disease", Proximity.MEDIUM );
        add( "avian influenza virus usually refers to influenza A viruses found chiefly in birds, but infections can occur in humans.", "avian influenza, sometimes avian flu, and commonly bird flu refers to influenza caused by viruses adapted to birds.", Proximity.VERY_HIGH );
        add( "manage outage", "managing building evacuation", Proximity.MEDIUM );
        add( "manage outage", "conducting building evacuation", Proximity.LOW );
        add( "manage outage", "managing chicken eggs", Proximity.MEDIUM );
        add( "manage outage", "managing job completion delay", Proximity.LOW );
        add( "manage outage", "overseeing data transfer and operations", Proximity.LOW );
        // unsatisfactory
        add( "pandemic flu, epidemic, quarantine", "disease, public health", Proximity.LOW );
        add( "bomb set off in a train station", "explosive detonated on public transportation", Proximity.LOW );

    }

    private void add( String text, String otherText, Proximity proximity ) {
        Object[] item = {text, otherText, proximity};
        data.add( item );
    }

    public void testSemanticMatching() throws Exception {
        SemanticProximityMatcher matcher = new SemanticProximityMatcher();
        matcher.setTaggerData( new FileSystemResource( "src/main/webapp/WEB-INF/data/left3words-wsj-0-18.tagger" ) );
        matcher.setWordnetDict( new FileSystemResource( "src/main/webapp/WEB-INF/data/wordnet-2/dict" ) );
        matcher.setSimType( "shef.nlp.wordnet.similarity.JCn" );
        for ( Object[] item : data ) {
            Proximity score = matcher.semanticProximity( (String) item[0], (String) item[1] );
            assertTrue( score.getOrdinal() == ( (Proximity) item[2] ).getOrdinal() );
        }
    }
}
