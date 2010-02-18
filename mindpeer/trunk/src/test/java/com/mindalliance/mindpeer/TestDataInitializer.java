// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

/**
 * ...
 */
public class TestDataInitializer {

    private DataInitializer di;

    @Mock
    private UserDao userDao;

    /**
     * Create a new TestDataInitializer instance.
     */
    public TestDataInitializer() {
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks( this );

        SecuredAspects.bypass( true );

        di = new DataInitializer();
        di.setUserDao( userDao );
    }

    @Test
    public void testInit() throws Exception {
        Mockito.when( userDao.countAll() ).thenReturn( 0 );

        di.afterPropertiesSet();

        verify( userDao ).countAll();
        verify( userDao, new Times( 2 ) ).save( (User) any() );
        verifyNoMoreInteractions( userDao );
    }

    @Test
    public void testSkipInit() throws Exception {
        Mockito.when( userDao.countAll() ).thenReturn( 1 );

        di.afterPropertiesSet();

        verify( userDao ).countAll();
        verifyNoMoreInteractions( userDao );
    }
}
