package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.core.model.time.TimeUnit;
import junit.framework.TestCase;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 29, 2009
 * Time: 4:05:50 PM
 */
public class TestDelay extends TestCase {

    public void testAll() {
        Delay delay = new Delay( 0, TimeUnit.Hour );
        assert ( delay.compareTo( new Delay() ) == 0 );
        assert ( delay.compareTo( Delay.parse( "10 minutes" ) ) == -1 );
        assert ( delay.compareTo( Delay.parse( "0 minute" ) ) == 0 );
        assert ( delay.compareTo( Delay.parse( "ten blips" ) ) == 0 );
        delay = new Delay( 10, TimeUnit.Hour );
        assert ( delay.compareTo( Delay.parse( delay.toString() ) ) == 0 );
    }
}
