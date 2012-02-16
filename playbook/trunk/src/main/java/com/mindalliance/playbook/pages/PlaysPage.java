package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master list of plays.
 */
public class PlaysPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( PlaysPage.class );

    private static final long serialVersionUID = -7557454314650272142L;

    @SpringBean
    Account account;
    
    @SpringBean
    PlayDao playDao;

    public PlaysPage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );
        LOG.debug( "Generating for account: {}", account.getEmail() );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        add( new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
             new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ),

             new ListView<Play>( "playbook.plays" ) {
                 @Override
                 protected void populateItem( ListItem<Play> item ) {
                     Play play = item.getModelObject();
                     item.add( new BookmarkablePageLink<EditPlay>( "editlink",
                                                                   EditPlay.class,
                                                                   new PageParameters().add( "id", play.getId() ) ).add(
                         new Label( "title", new PropertyModel<String>( play, "title" ) ),
                         new Label( "tags", new PropertyModel<String>( play, "tagString" ) ) )
                     );
                 }
             },
             
             new StatelessLink( "addPlay" ) {
                 @Override
                 public void onClick() {
                     Play play = new Play( account.getPlaybook(), "Unnamed play" );
                     playDao.save( play );
                     setResponsePage( EditPlay.class, new PageParameters().add( "id", play.getId() ) );                    
                 }
             } );
    }

    @Override
    public String getPageTitle() {
        return "Plays";
    }
}
