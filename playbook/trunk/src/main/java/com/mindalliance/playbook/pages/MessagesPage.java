package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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

    @SpringBean
    Account account;

    @SpringBean
    ConfirmationReqDao reqDao;

    public MessagesPage( PageParameters parameters ) {
        super( parameters );
        LOG.debug( "Generating for account: {}", account.getEmail() );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        List<ConfirmationReq> outgoing = reqDao.getOutgoingRequests();
        List<ConfirmationReq> incoming = reqDao.getIncomingRequests();

        add(
            new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ),

            new WebMarkupContainer( "empty" ).setVisible( outgoing.isEmpty() && incoming.isEmpty() ),

            new WebMarkupContainer( "outgoing" ).add(
//                new Label( "count", String.valueOf( incoming.size() ) ),
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
                                        "src", new Model<String>(
                                        "contacts/" + contact.getId() ) ) ).setVisible(
                                    contact.getPhoto() != null ) ) );
                    }
                } ).setVisible( !outgoing.isEmpty() ),

            new WebMarkupContainer( "incoming" ).add(
//                new Label( "count", String.valueOf( incoming.size() ) ),
                new ListView<ConfirmationReq>( "pending", incoming ) {
                    @Override
                    protected void populateItem( ListItem<ConfirmationReq> item ) {
                        final ConfirmationReq req = item.getModelObject();
                        final Collaboration collaboration = req.getCollaboration();
                        Contact contact = collaboration.getPlay().getPlaybook().getMe();
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
                                        "src", new Model<String>(
                                        "contacts/" + contact.getId() ) ) ) ) );
                    }
                } ).setVisible( !incoming.isEmpty() ),

            new BookmarkablePageLink<Settings>( "settingsLink", Settings.class ),
            new BookmarkablePageLink<PlaysPage>( "plays", PlaysPage.class ),
            new BookmarkablePageLink<TodoPage>( "todos", TodoPage.class ) );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Collaboration";
    }
}
