package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.dao.impl.StepDaoImpl.StepInformationImpl;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Task;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * Step edition tests
 */
public class EditStepTest extends AbstractPageTest {
    
    @Mock
    private Account account;

    @Mock
    private StepDao stepDao;

    @Mock
    private PlayDao playDao;
    
    private Play play;
    
    private Step step;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return EditStep.class;
    }

    @Override
    protected PageParameters getParameters() {
        return new PageParameters().add( "id", 0L );
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        when( account.getId() ).thenReturn( 123L );
        when( account.getUserId() ).thenReturn( "someone@example.com" );
        
        play = new Play( new Playbook( account, new Contact(
            new EmailMedium( "EMAIL", account.getUserId() ) ) ), "Test play" );
        step = new Task( play );
        
        when( stepDao.getInformation( 0L ) ).thenReturn( new StepInformationImpl( step, null, null ) );
        
        context.putBean( account );
        context.putBean( stepDao );
        context.putBean( playDao );
    }
}
