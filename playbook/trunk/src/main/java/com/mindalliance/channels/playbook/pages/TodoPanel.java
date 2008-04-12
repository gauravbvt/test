package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.Todo;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
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

        final Form form = new Form( "todos" );
        add( form );

        final WebMarkupContainer table = new WebMarkupContainer( "todo-table" );
        table.setOutputMarkupId( true );
        form.add( table );

        table.add( new RefreshingView( "todo", new RefPropertyModel( participation, "todos" ) ){
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
                final TextField desc = new TextField( "todo-name", new RefPropertyModel( todo, "description" ) );
//                desc.setOutputMarkupId( true );
//                desc.add( new OnChangeAjaxBehavior(){
//                    protected void onUpdate( AjaxRequestTarget target ) {
//                        System.out.println( "update..." );
//                    }
//                } );
                item.add( desc );
                item.add( new TextField( "todo-priority", new RefPropertyModel( todo, "priority" ) ) );
                item.add( new DateTextField( "todo-due",
                             new RefPropertyModel( todo,"due") ) );
                item.add( new AjaxFallbackButton( "todo-remove", form ) {
                    protected void onSubmit( AjaxRequestTarget target, Form form ) {
                        Ref todo = (Ref) item.getModelObject();
                        final Participation part = getParticipation();
                        part.removeTodo( todo );
                        todo.delete();
                        if ( target != null )
                            target.addComponent( table );
                        else
                            table.renderComponent();
                    }
                } );
            }
        } );

        table.add( new Button( "todo-new" ){
            public void onSubmit() {
                final Todo todo = new Todo();
                todo.persist();
                getParticipation().addTodo( todo.getReference() );
            }
        } );

        Button submit = new Button( "todo-submit" );
        form.add( submit );
        form.setDefaultButton( submit );
    }

    Participation getParticipation() {
        Ref p = (Ref) getModel().getObject();
        return (Participation) ( p.deref() );
    }
}
