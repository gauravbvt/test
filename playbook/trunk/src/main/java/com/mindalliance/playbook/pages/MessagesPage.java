package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.pages.panels.RequestItem;
import com.mindalliance.playbook.pages.panels.StepItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Collaboration messages summary page.
 */
public class MessagesPage extends NavigablePage {

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
        LOG.debug( "Generating for account: {}", account );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        final List<ConfirmationReq> outgoing = reqDao.getOutgoingRequests();
        List<ConfirmationReq> incoming = reqDao.getIncomingRequests();
        List<Collaboration> unconfirmed = stepDao.getUnconfirmed();
        List<Collaboration> rejected = stepDao.getRejected();
        List<Collaboration> incomplete = stepDao.getIncomplete();

        add(
            new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
            new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ),

            new WebMarkupContainer( "empty" )
                .setVisible( outgoing.isEmpty() && incoming.isEmpty() 
                             && unconfirmed.isEmpty() && rejected.isEmpty() ),

            new WebMarkupContainer( "outgoing" ).add( newRequestList( false, outgoing ) )
                .setRenderBodyOnly( true )
                .setVisible( !outgoing.isEmpty() ),

            new WebMarkupContainer( "unconfirmed" ).add( newStepList( unconfirmed ) )
                .setRenderBodyOnly( true )
                .setVisible( !unconfirmed .isEmpty() ),

            new WebMarkupContainer( "rejected" ).add( newStepList( rejected ) )
                .setRenderBodyOnly( true )
                .setVisible( !rejected.isEmpty() ),

            new WebMarkupContainer( "incomplete" ).add( newStepList( incomplete ) )
                .setRenderBodyOnly( true )
                .setVisible( !incomplete.isEmpty()
            ),

            new WebMarkupContainer( "incoming" ).add( newRequestList( true, incoming ) )
                .setRenderBodyOnly( true )
                .setVisible( !incoming.isEmpty() ) );
    }

    private ListView<ConfirmationReq> newRequestList( final boolean incoming, final List<ConfirmationReq> requests ) {
        return new ListView<ConfirmationReq>( "pending", requests ) {
            @Override
            protected void populateItem( ListItem<ConfirmationReq> item ) {
                item.add(
                    new RequestItem( "step", item.getModel(), incoming )
                    );
            }
        };
    }

    private ListView<Collaboration> newStepList( final List<Collaboration> unconfirmed ) {
        return new ListView<Collaboration>( "pending", unconfirmed ) {
            @Override
            protected void populateItem( ListItem<Collaboration> item ) {
                item.add(
                    new StepItem( "step", item.getModel(), true ) );
            }
        };
    }

    @Override
    public String getPageTitle() {
        return "Messages";
    }
}
