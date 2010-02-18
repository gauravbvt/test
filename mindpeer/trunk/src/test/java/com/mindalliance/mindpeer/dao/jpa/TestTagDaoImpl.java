// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.TagDao;
import com.mindalliance.mindpeer.model.Tag;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ...
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/applicationContext.xml", "/integratedTestContext.xml" } )
public class TestTagDaoImpl {

    @Autowired
    private TagDao tagDao;

    @Test
    @Transactional
    @Rollback
    public void testCreate() {
        Tag tag1 = tagDao.get( "bla" );
        Tag tag2 = tagDao.get( tag1.getId() );
        Tag tag3 = tagDao.get( "Bla" );

        assertTrue( tag1.getId() != 0L );

        assertEquals( tag1, tag2 );
        assertEquals( tag1, tag3 );
        assertEquals( tag3, tag2 );

        assertEquals( 1, tagDao.countAll() );

        try {
            tagDao.get( (String) null );
            fail();
        } catch ( IllegalArgumentException ignored ) {
            assertEquals( 1, tagDao.countAll() );
        }

        List<Tag> tags = tagDao.getAll();
        assertEquals( 1, tags.size() );
        assertEquals( tag1, tags.get(0) );

        Tag tag4 = tagDao.get( "foo" );
        assertEquals( 2, tagDao.countAll() );

        tagDao.delete( tag4 );
        assertEquals( 1, tagDao.countAll() );
    }

    @Test
    @Transactional
    @Rollback
    public void testGetSet() {
        Set<Tag> tags1 = tagDao.get( new HashSet<String>( Arrays.asList( "foo", "bar", "baz" ) ) );
        assertEquals( 3, tags1.size() );
        assertEquals( 3, tagDao.countAll() );

        Set<Tag> tags2 = tagDao.get( new HashSet<String>( Arrays.asList( "bla", "bar", "baz" ) ) );
        assertEquals( 3, tags2.size() );
        assertEquals( 4, tagDao.countAll() );

        Set<Tag> tags3 = tagDao.get( new HashSet<String>( Arrays.asList( "bar", "bar", "baz" ) ) );
        assertEquals( 2, tags3.size() );
        assertEquals( 4, tagDao.countAll() );
    }
}
