package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Collaboration messages summary page.
 */
public class MessagesPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( MessagesPage.class );

    private static final long serialVersionUID = 879820312918620140L;

    @SpringBean
    Account account;

    @SpringBean
    ConfirmationReqDao reqDao;

    @SpringBean
    private StepDao stepDao;

    public MessagesPage( PageParameters parameters ) {
        super( parameters );
        LOG.debug( "Generating for account: {}", account.getEmail() );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        final List<ConfirmationReq> outgoing = reqDao.getOutgoingRequests();
        List<ConfirmationReq> incoming = reqDao.getIncomingRequests();
        List<Collaboration> unconfirmed = stepDao.getUnconfirmed();
        List<Collaboration> rejected = stepDao.getRejected();
        List<Collaboration> incomplete = stepDao.getIncomplete();

        add(
            new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ),

            new WebMarkupContainer( "empty" )
                .setVisible( outgoing.isEmpty() && incoming.isEmpty() 
                             && unconfirmed.isEmpty() && rejected.isEmpty() ),

            new WebMarkupContainer( "outgoing" ).add(
                new ListView<ConfirmationReq>( "pending", outgoing ) {
                    @Override
                    protected void populateItem( ListItem<ConfirmationReq> item ) {
                        final Collaboration collaboration = item.getModelObject().getCollaboration();
                        Contact contact = collaboration.getWith();
                        item.add(
                            new StatelessLink( "link" ) {
                                @Override
                                public void onClick() {
                                    setResponsePage( new ConfirmPage( collaboration ) );
                                }
                            }.add(
                                new Label( "title", collaboration.getTitle() ),

                                // TODO figure out what is the right way of doing this...
                                new WebMarkupContainer( "photo" ).add(
                                    new AttributeModifier(
                                        "src", new Model<String>( "contacts/" + contact.getId() ) ) ).setVisible(
                                    contact.getPhoto() != null ) ) );
                    }
                } ).setVisible( !outgoing.isEmpty() ),

            new WebMarkupContainer( "unconfirmed" ).add(
                newStepList( unconfirmed ) ).setVisible( !unconfirmed.isEmpty() ),

            new WebMarkupContainer( "rejected" ).add(
                newStepList( rejected ) ).setVisible( !rejected.isEmpty() ),

            new WebMarkupContainer( "incomplete" ).add(
                newStepList( incomplete ) ).setVisible( !incomplete.isEmpty() ),

            new WebMarkupContainer( "incoming" ).add(
                new ListView<ConfirmationReq>( "pending", incoming ) {
                    @Override
                    protected void populateItem( ListItem<ConfirmationReq> item ) {
                        final ConfirmationReq req = item.getModelObject();
                        final Collaboration collaboration = req.getCollaboration();
                        Contact contact = req.getSender();
                        item.add(
                            new StatelessLink( "link" ) {
                                @Override
                                public void onClick() {
                                    redirectToInterceptPage( new AckPage( req ) );
                                }
                            }.add(
                                new Label( "title", collaboration.getTitle() ),

                                // TODO figure out what is the right way of doing this...
                                new WebMarkupContainer( "photo" ).add(
                                    new AttributeModifier(
                                        "src", new Model<String>( "contacts/" + contact.getId() ) ) ) 
                            ) );
                    }
                } ).setVisible( !incoming.isEmpty() ) );
    }

    private ListView<Collaboration> newStepList( final List<Collaboration> unconfirmed ) {
        return new ListView<Collaboration>( "pending", unconfirmed ) {
            @Override
            protected void populateItem( ListItem<Collaboration> item ) {
                final Collaboration collaboration = item.getModelObject();
                Contact contact = collaboration.getWith();
                long id = contact == null ? 0L : contact.getId();
                item.add(
                    new StatelessLink( "link" ) {
                        @Override
                        public void onClick() {
                            setResponsePage( EditStep.class, new PageParameters().add( "id",
                                collaboration.getId() ) );
                        }
                    }.add(
                        new Label( "title", collaboration.getTitle() ),

                        // TODO figure out what is the right way of doing this...
                        new WebMarkupContainer( "photo" ).add(
                            new AttributeModifier(
                                "src", new Model<String>( "contacts/" + id ) )
                        ).setVisible( contact != null && contact.getPhoto() != null ) ) );
            }
        };
    }

    @Override
    public String getPageTitle() {
        return "Messages";
    }
}
