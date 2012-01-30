package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Play;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Request acknowledgement page.
 */
public class AckPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( AckPage.class );

    @SpringBean
    private Account account;
    
    @SpringBean
    private AckDao ackDao;

    private Play existingPlay;
    
    private String newPlay;

    public AckPage( final ConfirmationReq request ) {
        super();
        IModel<ConfirmationReq> model = new CompoundPropertyModel<ConfirmationReq>( request );
        setDefaultModel( model );

        add(
            new Label( "hTitle", getPageTitle() ),

            new Label( "collaboration.play.playbook.me" ),
            //new Label( "collaboration.with.fullName" ),
            new Label( "description" ),
            new Form( "form" ) {
                @Override
                protected void onSubmit() {
                    // TODO implement this
                    LOG.debug( "Submitted" );
                    if ( newPlay != null && !newPlay.trim().isEmpty() )
                        ackDao.createNewPlay( request, newPlay );
                    setResponsePage( MessagesPage.class );
                }
            }.add(
                new StatelessLink( "cancel" ) {
                    @Override
                    public void onClick() {
                        LOG.debug( "Cancelled" );
                        setResponsePage( MessagesPage.class );
                    }
                },
                
                new TextField<String>( "newPlay", new PropertyModel<String>( this, "newPlay" ) ),

                new Select<Play>( "play", new PropertyModel<Play>( this, "existingPlay" ) ).add(
                    new SelectOptions<Play>(
                        "plays",
                        new PropertyModel<Collection<? extends Play>>( account, "playbook.plays" ),
                        new IOptionRenderer<Play>() {
                            @Override
                            public String getDisplayValue( Play object ) {
                                return object.getTitle();
                            }

                            @Override
                            public IModel<Play> getModel( Play value ) {
                                return new Model<Play>( value );
                            }
                        } ) ) ) );
    }

    @Override
    public String getPageTitle() {
        return "Request confirmation";
    }

    public Play getExistingPlay() {
        return existingPlay;
    }

    public void setExistingPlay( Play existingPlay ) {
        this.existingPlay = existingPlay;
    }

    public String getNewPlay() {
        return newPlay;
    }

    public void setNewPlay( String newPlay ) {
        this.newPlay = newPlay;
    }
}
