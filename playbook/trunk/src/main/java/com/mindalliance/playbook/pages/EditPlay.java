package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Task;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Play editor.
 */
public class EditPlay extends MobilePage {

    private static final long serialVersionUID = 7036708047688040702L;

    @SpringBean
    private Account account;

    @SpringBean
    private PlayDao playDao;

    private String newStepName;

    public EditPlay( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        final Play play = getPlay( parameters );
        setDefaultModel( new CompoundPropertyModel<Play>( play ) );

        add(
            new Label( "hTitle", new PropertyModel<Object>( play, "title" ) ),
            new BookmarkablePageLink<PlaysPage>( "back", PlaysPage.class ),
            new StatelessForm( "form" ) {
                @Override
                protected void onSubmit() {
                    super.onSubmit();

                    if ( newStepName != null && !newStepName.isEmpty() ) {
                        Task step = new Task( play );
                        step.setTitle( newStepName );
                        play.addStep( step );

                        List<Step> steps = play.getSteps();
                        for ( int i = 0, stepsSize = steps.size(); i < stepsSize; i++ )
                            steps.get( i ).setSequence( i + 1 );
                    }

                    playDao.save( play );

                    // TODO find the proper way of doing this
                    WebResponse response = (WebResponse) getResponse();
                    response.sendRedirect(
                        newStepName == null ? "../plays.html" : Long.toString( play.getId() ) );
                }
            }.add(
                new TextField( "title" ), new TextField( "schedule" ), new TextArea( "description" ),

                new TextField<String>( "newStep", new PropertyModel<String>( this, "newStepName" ) ),

                new StatelessLink( "deletePlay" ) {
                    @Override
                    public void onClick() {
                        playDao.delete( play );
                        setResponsePage( PlaysPage.class );
                    }
                }, 
                
                new TextField( "tagString" ),

                new WebMarkupContainer( "stepDiv" ).add(
                    new ListView<Step>( "steps" ) {
                        @Override
                        protected void populateItem( ListItem<Step> item ) {
                            Step step = item.getModelObject();

                            long contactId = 0L;
                            boolean hasPhoto = false;
                            if ( step.isCollaboration() ) {
                                Contact with = ( (Collaboration) step ).getWith();
                                if ( with != null ) {
                                    contactId = with.getId();
                                    hasPhoto = with.getPhoto() != null;
                                }
                            }

                            item.add(
                                new BookmarkablePageLink<EditStep>(
                                    "editStep", EditStep.class, new PageParameters().add( "id", step.getId() ) ).add(

                                    // TODO figure out what is the right way of doing this...
                                    new WebMarkupContainer( "photo" ).add(
                                        new AttributeModifier(
                                            "src", new Model<String>(
                                            "../contacts/" + contactId ) ) ).setVisible( contactId != 0L && hasPhoto ),

                                    new Label( "sequence", new PropertyModel<Integer>( step, "sequence" ) ),
                                    new Label( "title", new PropertyModel<String>( step, "title" ) ) ) );
                        }
                    } ).setVisible( !play.getSteps().isEmpty() )

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

        if ( play.getAccountId() != account.getId() )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_FORBIDDEN, "Unauthorized" );
        return play;
    }

    @Override
    public String getPageTitle() {
        return "Edit play";
    }

    public String getNewStepName() {
        return newStepName;
    }

    public void setNewStepName( String newStepName ) {
        this.newStepName = newStepName;
    }
}
