package com.mindalliance.channels.playbook;

import com.mindalliance.channels.playbook.model.Project;
import com.mindalliance.channels.playbook.model.Todo;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Homepage
 */
public class HomePage extends Template {

	private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
        super( parameters );
        PlaybookSession session = (PlaybookSession) getSession();
        Project p = session.getProject();

        List<Todo> todos = session.getParticipation() == null ?
                           new ArrayList<Todo>() : session.getParticipation().getTodos();

        add( new Label("title", "Playbook") );

        // Add scenarios to the list
        add( new ListView( "sc-list", p.getScenarios() ){
            protected void populateItem( ListItem listItem ) {
                listItem.add(
                    new Label( "sc-item",
                       new PropertyModel( listItem.getModelObject(), "name" )));
                }
            } );

        // Add resources
        add( new ListView( "r-list", p.getResources() ){
            protected void populateItem( ListItem listItem ) {
                listItem.add(
                    new Label( "r-item",
                       new PropertyModel( listItem.getModelObject(), "name" )));
                }
            } );

        // Add todos
        final DateFormat dateFormat =
            DateFormat.getDateInstance( DateFormat.SHORT, session.getLocale() );
        add( new DataView( "todo", new ListDataProvider( todos )){
            protected void populateItem( Item item ) {
                Todo t = (Todo) item.getModelObject();
                item.add( new Label( "todo-name", t.getDescription() ) );
                item.add( new Label( "todo-priority", t.getPriority() ) );
                item.add( new Label( "todo-due", dateFormat.format( t.getDue() )) );
            }
        });
   }
}
