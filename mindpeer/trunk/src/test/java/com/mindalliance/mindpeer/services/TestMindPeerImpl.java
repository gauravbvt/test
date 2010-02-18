// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.WicketApplication;
import com.mindalliance.mindpeer.dao.UserDao;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

/**
 * ...
 */
public class TestMindPeerImpl {

    private MindPeerImpl mindPeer;

    private Mailer mailer;

    private WicketApplication wicketApplication;

    private UserDao userDao;

    @Before
    public void init() {
        mailer = mock( Mailer.class );
        wicketApplication = mock( WicketApplication.class );
        userDao = mock( UserDao.class );

        mindPeer = new MindPeerImpl();
        mindPeer.setMailer( mailer );
        mindPeer.setWicketApplication( wicketApplication );
        mindPeer.setUserDao( userDao );
    }

    /**
     * Bogus tests for coverage.
     */
    @Test
    public void testAccessors() {
        assertSame( mailer, mindPeer.getMailer() );
        assertSame( wicketApplication, mindPeer.getWicketApplication() );
        assertSame( userDao, mindPeer.getUserDao() );

    }

}
