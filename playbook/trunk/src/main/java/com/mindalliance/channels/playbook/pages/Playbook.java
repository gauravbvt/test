package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;

/**
 * ...
 */
public class Playbook extends Page {

    Playbook( PageParameters parms ){
        super( parms );
        Session session = getSession();

        add( new Label("title", "Playbook" ));
        add( new Label("name", new RefPropertyModel(this, "session.user.name")));
        add( new Label("project", new RefPropertyModel(this, "session.project.name")));
        add( new BookmarkablePageLink("signout", SignOutPage.class, parms));

        // Add scenarios to the list
        add( new ListView( "sc-list", new RefPropertyModel(session, "participation.scenarios") ){
            protected void populateItem( ListItem listItem ) {
                listItem.add(
                    new Label( "sc-item",
                       new PropertyModel( listItem.getModelObject(), "name" )));
                }
            } );

        // Add resources
        add( new ListView( "r-list", new RefPropertyModel(session, "participation.project.resources") ){
            protected void populateItem( ListItem listItem ) {
                listItem.add(
                    new Label( "r-item",
                       new PropertyModel( listItem.getModelObject(), "name" )));
                }
            } );

        // Add todos
        add( new DataView( "todo", new ListDataProvider( new ArrayList() )){
            protected void populateItem( Item item ) {
                item.add( new Label( "todo-name", new RefPropertyModel( item, "description" ) ) );
                item.add( new Label( "todo-priority", new RefPropertyModel( item, "priority" ) ) );
                item.add( new Label( "todo-due", new RefPropertyModel( item, "due" )) );
            }
        });
    }

}
