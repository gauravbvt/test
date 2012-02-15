package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

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
        when( oldPlay.getId() ).thenReturn( 123L );
        
        List<Play> plays = new ArrayList<Play>();
        plays.add( oldPlay );
        when( playbook.getPlays() ).thenReturn( plays );
        when( playDao.load( 0L ) ).thenReturn( newPlay );
        
        context.putBean( account );
        context.putBean( playDao );
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

        verify( playDao ).save( (Play) notNull() );
        verify( oldPlay ).getTitle();
        verify( oldPlay ).getTagString();
    }
    
    /**
     * Test some essential links.
     */
    @Test
    public void links() {
        tester.startPage( getTestedClass() );
        tester.assertBookmarkablePageLink( "settingsLink", Settings.class, new PageParameters() );
        tester.assertBookmarkablePageLink( "todos", TodoPage.class, new PageParameters() );
        tester.assertBookmarkablePageLink( "messages", MessagesPage.class, new PageParameters() );
        tester.assertBookmarkablePageLink( "playbook.plays:0:editlink", EditPlay.class, new PageParameters().add( "id", 123L ) );
    }
}
