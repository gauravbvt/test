package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.Todo;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * ...
 * Todo Ajaxify
 */
public class TodoPanel extends Panel {

    public TodoPanel( String s, IModel participation ) {
        super( s, participation );

        Form form = new Form( "todos" );
        add( form );

        final WebMarkupContainer table = new WebMarkupContainer( "todo-table" );
        form.add( table );

        final RefreshingView lv = new RefreshingView( "todo", new RefPropertyModel( participation, "todos" ) ){
            protected Iterator getItemModels() {
                final IModel model = getModel();
                final List list = (List) model.getObject();
                return new ModelIteratorAdapter( list.iterator() ) {
                    protected IModel model( Object o ) {
                        return new RefModel( o );
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
                        final Participation part = getParticipation();
                        part.removeTodo( todo );
                        todo.delete();
                        table.setVisible( part.getTodos().size() > 0 );
                    }
                } );
            }
        };

        table.setVisible( getParticipation().getTodos().size() > 0 );

        table.add( lv );
        form.add( new Button( "todo-new" ){
            public void onSubmit() {
                final Todo todo = new Todo();
                todo.persist();
                getParticipation().addTodo( todo.getReference() );
                table.setVisible( true );

            } } );
        Button submit = new Button( "todo-submit" );
        form.add( submit );
        form.setDefaultButton( submit );
    }

    Participation getParticipation() {
        Ref p = (Ref) getModel().getObject();
        return (Participation) ( p.deref() );
    }
}
