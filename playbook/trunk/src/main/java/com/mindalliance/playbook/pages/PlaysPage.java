package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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

import java.util.List;

/**
 * Master list of plays.
 */
public class PlaysPage extends NavigablePage {

    private static final Logger LOG = LoggerFactory.getLogger( PlaysPage.class );

    private static final long serialVersionUID = -7557454314650272142L;

    @SpringBean
    Account account;

    @SpringBean
    PlayDao playDao;

    private String searchTerm = "";

    public PlaysPage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );
        LOG.debug( "Generating for account: {}", account );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        final WebMarkupContainer list = new WebMarkupContainer( "list" );
        add(
            new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
            new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ),
            new Form<String>( "form" ).add( new TextField<String>(
                "search", new PropertyModel<String>( this, "searchTerm" ) ).add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        target.add( list );
                        target.appendJavaScript( "$('#" + list.getMarkupId() + "').listview();" );
                    }
                } ) ),
            

            list.add(
                new ListView<Play>(
                    "filteredPlays", new PropertyModel<List<Play>>( PlaysPage.this, "filteredPlays" ) ) {
                    @Override
                    protected void populateItem( ListItem<Play> item ) {
                        Play play = item.getModelObject();
                        item.add(
                            new BookmarkablePageLink<EditPlay>(
                                "editlink", EditPlay.class, new PageParameters().add( "id", play.getId() ) ).add(
                                new Label( "title", new PropertyModel<String>( play, "title" ) ),
                                new Label( "desc", new PropertyModel<String>( play, "description" ) ),
                                new Label( "tags", new PropertyModel<String>( play, "tagString" ) ) ) 
                        );
                    }
                }

            ).setOutputMarkupId( true ),

            new StatelessLink( "addPlay" ) {
                @Override
                public void onClick() {
                    Play play = new Play( account.getPlaybook(), "Unnamed play" );
                    playDao.save( play );
                    setResponsePage( EditPlay.class, new PageParameters().add( "id", play.getId() ) );
                }
            }
        );
    }

    @Override
    public String getPageTitle() {
        return "Plays";
    }

    public List<Play> getFilteredPlays() {
        return playDao.find( searchTerm );
    }
    
    public void setFilteredPlays( List<Play> plays ) {
        // Wicket's property models need both getters and setters...    
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm( String searchTerm ) {
        this.searchTerm = searchTerm;
    }
    
}
