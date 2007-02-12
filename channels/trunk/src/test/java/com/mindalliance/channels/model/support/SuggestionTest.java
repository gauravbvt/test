// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.mindalliance.channels.model.TestModelObject;

import junit.framework.JUnit4TestAdapter;

/**
 * Test suite for Suggestions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class SuggestionTest {

    private static final String TEST_VALUE = "test value";
    private Suggestion<String> suggestion;
    private PropertyDescriptor property;

    /**
     * Setup before each test.
     * @throws IntrospectionException never...
     */
    @Before
    public void setUp() throws IntrospectionException {

        this.property = new PropertyDescriptor( "name", TestModelObject.class );
//        this.suggestion = new Suggestion<String>( this.property, TEST_VALUE );
    }

    /**
     * Test method for {@link Suggestion#hashCode()}.
     */
    @Test
    public final void testHashCode() {
        // TODO Debug...
//        assertEquals( TEST_VALUE.hashCode(), this.suggestion.hashCode() );
    }

    /**
     * Test method for {@link Suggestion#getProperty()}.
     */
    @Test
    public final void testGetProperty() {
        // TODO Debug...
//        assertSame( property, this.suggestion.getProperty() );
    }

    /**
     * Test method for {@link Suggestion#getValue()}.
     */
    @Test
    public final void testGetValue() {
        // TODO Debug...
//        assertSame( TEST_VALUE, this.suggestion.getValue() );
    }

    /**
     * Needed for compatibility when running inside eclipse...
     * @return a compatibility adapter
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( SuggestionTest.class );
    }
}
