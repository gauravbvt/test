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
        add( "the fast fox jumped over the big dog", "the fast fox leapted over the big dog", Proximity.HIGH );
        add( "the fast fox jumped over the big dog", "the slow fox jumped over the small dog", Proximity.HIGH );
        add( "the fast fox jumped over the big dog", "the agile carnivore leaped over the huge animal", Proximity.HIGH );
        add( "the fast fox jumped over the big dog", "the fast fox jumped under the big dog", Proximity.HIGH );
        add( "the fast fox jumped over the big dog", "the slow carnivore crawled under the tiny animal", Proximity.MEDIUM );
        add( "the quick fox jumped over the lazy dog", "tea for two at the Ritz", Proximity.LOW );
        add( "I flew to Europe on Delta Airlines", "an American Airlines plane crashed on take off", Proximity.LOW );
        add( "terrorism, John Doe", "John Doe committed a violent crime", Proximity.VERY_HIGH );
        add( "autopsy report of plague", "account of death by contagious disease", Proximity.HIGH );
        add( "avian influenza virus usually refers to influenza A viruses found chiefly in birds, but infections can occur in humans.",
                "avian influenza, sometimes avian flu, and commonly bird flu refers to influenza caused by viruses adapted to birds.",
                Proximity.VERY_HIGH );
        add( "manage outage", "manage building evacuation", Proximity.HIGH );
        add( "manage outage", "conduct building evacuation", Proximity.MEDIUM );
        add( "manage outage", "managing chicken eggs", Proximity.MEDIUM );
        add( "manage outage", "management of job completion delay", Proximity.MEDIUM );
        add( "manage outage", "overseeing data transfer and operations", Proximity.MEDIUM );
        add( "pandemic flu, epidemic, quarantine", "disease, public health", Proximity.HIGH );
        add( "bomb set off in a train station", "explosive detonated on public transportation", Proximity.VERY_HIGH );

    }

    private void add( String text, String otherText, Proximity proximity ) {
        Object[] item = {text, otherText, proximity};
        data.add( item );
    }

    public void testSemanticMatching() throws Exception {
        WordnetSemanticMatcher matcher = new WordnetSemanticMatcher();
        matcher.setWordnetDict( new FileSystemResource( "src/main/webapp/WEB-INF/data/wordnet-2/dict" ) );
        matcher.setSimIndex( new FileSystemResource( "src/main/webapp/WEB-INF/data/jwsl/wn_index" ) );
        // for ( int i = 0; i < 2; i++ )
        for ( Object[] item : data ) {
            Proximity score = matcher.semanticProximity( (String) item[0], (String) item[1] );
            // assertTrue( score.getOrdinal() == ( (Proximity) item[2] ).getOrdinal() );
        }
    }
}
