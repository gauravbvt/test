package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Task;
import com.mindalliance.playbook.pages.panels.StepItem;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Play editor.
 */
public class EditPlay extends NavigablePage {

    private static final long serialVersionUID = 7036708047688040702L;

    @SpringBean
    private Account account;

    @SpringBean
    private PlayDao playDao;
    
    private int dragStart;

    private static final Logger LOG = LoggerFactory.getLogger( EditPlay.class );

    public EditPlay( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        final Play play = getPlay( parameters );
        setDefaultModel( new CompoundPropertyModel<Play>( play ) );

        add(
            new BookmarkablePageLink<PlaysPage>( "back", PlaysPage.class ),
            new StatelessForm( "form" ).add(
                new TextField( "title" ).add(
                    new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            playDao.save( play );
                        }
                    } ), new TextArea( "description" ).add(
                new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        playDao.save( play );
                    }
                } ),

                new StatelessLink( "deletePlay" ) {
                    @Override
                    public void onClick() {
                        playDao.delete( play );
                        setResponsePage( PlaysPage.class );
                    }
                },

                new StatelessLink( "addStep" ) {
                    @Override
                    public void onClick() {
                        Task step = new Task( play );
                        step.setTitle( "Unnamed" );
                        play.addStep( step );

                        List<Step> steps = play.getSteps();
                        for ( int i = 0, stepsSize = steps.size(); i < stepsSize; i++ )
                            steps.get( i ).setSequence( i + 1 );

                        playDao.save( play );
                        setResponsePage( EditStep.class, new PageParameters().add( "id", step.getId() ) );
                    }
                },

                new TextField( "tagString" ).add(
                    new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            playDao.save( play );
                        }
                    } ),

                new ListView<Step>( "steps" ) {
                    @Override
                    protected void populateItem( final ListItem<Step> item ) {
                        item.add( new StepItem( "step", item.getModel(), false ) );
                    }
                }
            ) );
    }

    private Play getPlay( PageParameters parameters ) {
        StringValue id = parameters.get( "id" );
        if ( id.isNull() )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        Play play = playDao.load( id.toLong() );
        if ( play == null )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        // Use getId() because .equals() doesn't work on proxies, for some reason...  
        if ( account.getId() != play.getAccount().getId() )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_FORBIDDEN, "Unauthorized" );
        return play;
    }

    @Override
    public String getPageTitle() {
        return "Edit play";
    }
}
