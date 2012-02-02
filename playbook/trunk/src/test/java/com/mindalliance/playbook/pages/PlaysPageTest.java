package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Test the plays page.
 */
public class PlaysPageTest extends AbstractPageTest {
    
    @Mock
    private Account account;
    
    @Mock
    private Playbook playbook;
    
    @Mock
    private PlayDao playDao;
    
    @Mock
    private Play oldPlay;
    
    @Mock
    private Play newPlay;

    @Override
    protected void init( ApplicationContextMock context ) {
        MockitoAnnotations.initMocks( this );
        when( account.getEmail() ).thenReturn( "someone@somewhere.com" );
        when( account.getPlaybook() ).thenReturn( playbook );
        
        List<Play> plays = new ArrayList<Play>();
        plays.add( oldPlay );
        when( playbook.getPlays() ).thenReturn( plays );

        when( playDao.load( 0L ) ).thenReturn( newPlay );
        
        context.putBean( "account", account );
        context.putBean( "playDao", playDao );
    }

    @Override
    public Class<? extends WebPage> getTestedClass() {
        return PlaysPage.class;
    }
    
    @Test
    public void addPlay() {
        tester.startPage( getTestedClass() );
        tester.clickLink( "addPlay" );
        tester.assertRenderedPage( EditPlay.class );
    }
}
