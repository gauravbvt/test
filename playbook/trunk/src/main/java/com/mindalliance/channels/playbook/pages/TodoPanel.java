package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.Todo;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public class TodoPanel extends Panel {

    public TodoPanel( String s ) {
        super( s );

        Form form = new Form( "todos" ){
            protected void onSubmit() {
                List<Todo> todos = getTodos();
                if ( todos.size() > 0 ) {
                    Todo first = (Todo) (todos.get(0).deref());
                    first.getDescription();
                }
            }
        };
        add( form );

        RefreshingView rv = new RefreshingView( "todo" ){
            protected Iterator getItemModels() {
                final List<Todo> todos = getTodos();
                return new ModelIteratorAdapter( todos.iterator() ) {
                    protected IModel model( Object o ) {
                        return new Model( (Serializable) o );
                    }
                };
            }

            protected void populateItem( final Item item ) {
                Todo todo = (Todo) item.getModelObject();
                item.add( new TextField( "todo-name", new PropertyModel( todo, "description" ) ){} );
                item.add( new TextField( "todo-priority", new PropertyModel( todo, "priority" ) ) );
                item.add( new TextField( "todo-due", new PropertyModel( todo, "due" )) );
                item.add( new Button( "todo-remove" ){
                    public void onSubmit() {
                        Todo todo = (Todo) item.getModelObject();
                        getParticipation().removeTodo( todo );
                    }
                } );
            }
        };

        form.add( rv );
        form.add( new Button( "todo-new" ){
            public void onSubmit() {
                final Todo todo = new Todo();
                todo.persist();
                getParticipation().addTodo( todo );
            }
        } );
    }

    Participation getParticipation() {
        PlaybookSession s = (PlaybookSession) getSession();
        return (Participation) ( s.getParticipation().deref() );
    }

    List<Todo> getTodos() {
        PlaybookSession s = (PlaybookSession) getSession();
        Ref p = s.getParticipation();
        return (List<Todo>) p.deref( "todos" );
    }
}
