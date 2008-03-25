package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.Todo;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
                Playbook p = (Playbook) getPage();
                p.refreshControls();
            }
        };
        add( form );

        final RefreshingView lv = new RefreshingView( "todo" ){
            protected Iterator getItemModels() {
                final List<Ref> todos = getTodos();
                return new ModelIteratorAdapter( todos.iterator() ) {
                    protected IModel model( Object o ) {
                        return new Model( (Serializable) o );
                    }
                };
            }

            protected void populateItem( final Item item ) {
                Ref todo = (Ref) item.getModelObject();
                item.add( new TextField( "todo-name", new RefPropertyModel( todo, "description" ) ){} );
                item.add( new TextField( "todo-priority", new RefPropertyModel( todo, "priority" ) ) );
                item.add( new DateTextField( "todo-due",
                             new RefPropertyModel( todo,"due") ) );
                item.add( new Button( "todo-remove" ) {
                    public void onSubmit() {
                        Ref todo = (Ref) item.getModelObject();
                        getParticipation().removeTodo( todo );
                    }
                } );
            }
        };
        form.add( lv );
        form.add( new Button( "todo-new" ){
            public void onSubmit() {
                final Todo todo = new Todo();
                todo.persist();
                getParticipation().addTodo( todo.getReference() );
            } } );
        Button submit = new Button( "todo-submit" );
        form.add( submit );
        form.setDefaultButton( submit );
    }

    Participation getParticipation() {
        PlaybookSession s = (PlaybookSession) getSession();
        return (Participation) ( s.getParticipation().deref() );
    }

    List<Ref> getTodos() {
        PlaybookSession s = (PlaybookSession) getSession();
        Ref p = s.getParticipation();
        return (List<Ref>) p.get( "todos" );
    }
}
