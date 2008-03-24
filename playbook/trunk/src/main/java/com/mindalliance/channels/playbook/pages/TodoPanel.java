package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

/**
 * ...
 */
public class TodoPanel extends Panel {

    public TodoPanel( String s, IDataProvider data ) {
        super( s );

        // Add todos
        add( new DataView( "todo", data ){
            protected void populateItem( Item item ) {
                item.add( new Label( "todo-name", new RefPropertyModel( item, "description" ) ) );
                item.add( new Label( "todo-priority", new RefPropertyModel( item, "priority" ) ) );
                item.add( new Label( "todo-due", new RefPropertyModel( item, "due" )) );
            }
        });
    }
}
