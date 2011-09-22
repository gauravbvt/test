package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 9:03:31 AM
 */
public class TestDuplicatePart extends AbstractChannelsTest {

    private Segment segment;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.createSegment();
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testDuplicatePart() {
        int count = countParts();
        Part part = segment.getDefaultPart();
        Command duplicatePart = new DuplicatePart( User.current().getUsername(), part );
        assertTrue( getCommander().canDo( duplicatePart ) );
        Change change = getCommander().doCommand( duplicatePart );
        assertTrue( change.isAdded() );
        Part duplicate = (Part) change.getSubject( getCommander().getQueryService() );
        assertTrue( duplicate.getName().equals( part.getName() ) );
        assertTrue( countParts() == count + 1 );
        assertTrue( getCommander().canUndo( User.current().getUsername() ) );
        assertTrue( getCommander().undo( User.current().getUsername() ).isRecomposed() );
        assertTrue( countParts() == count );
        assertTrue( getCommander().canRedo( User.current().getUsername() ) );
        assertTrue( getCommander().redo( User.current().getUsername() ).isUnknown() );
        assertTrue( countParts() == count + 1 );
    }

    private int countParts() {
        int count = 0;
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            parts.next();
            count++;
        }
        return count;
    }


}
