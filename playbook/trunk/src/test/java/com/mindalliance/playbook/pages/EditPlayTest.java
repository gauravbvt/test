package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Play edition test.
 */
public class EditPlayTest extends AbstractPageTest {
    
    @Mock
    private Account account;
    
    @Mock
    private PlayDao playDao;
    
    private Play play;
    
    private Playbook playbook;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return EditPlay.class;
    }

    @Override
    protected PageParameters getParameters() {
        return new PageParameters().add( "id", 1L );
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        MockitoAnnotations.initMocks( this );
        Mockito.when( account.getEmail() ).thenReturn( "someone@example.com" );
        
        playbook = new Playbook( account );
        
        play = new Play( playbook, "Test" );
        Mockito.when( playDao.load( 1L ) ).thenReturn( play );
        
        context.putBean( account );
        context.putBean( playDao );
    }
    
    @Test
    public void invalidPlay() {
        tester.startPage( getTestedClass() );
        Assert.assertEquals( 404, tester.getLastResponse().getStatus() );

        tester.startPage( getTestedClass(), new PageParameters().add( "id", 2L ) );
        Assert.assertEquals( 404, tester.getLastResponse().getStatus() );
    }
}
