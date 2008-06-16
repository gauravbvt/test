package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * A visualization of the contents of a tab panel.
 */
public class ContentView extends Panel implements SelectionManager {

    private SelectionManager selectionManager;
    private boolean menuVisible;
    private Ref selected;

    protected ContentView( String id, IModel model, SelectionManager masterSelection ) {
        super( id, model );
        if ( masterSelection == null || model == null )
            throw new NullPointerException();
        this.selectionManager = masterSelection;
        setOutputMarkupId( true );

        load();
    }

    protected void load() {
        addOrReplace( getPager( "content-pager" ) );

        addOrReplace( new Link( "content-delete" ){
            public boolean isEnabled() {
                return getSelected() != null;
            }

            public void onClick() {
                final Container container = getContainer();
                Ref ref = getSelected();
                int index = container.indexOf( ref );
                container.remove( ref );
                if ( container.size() == 0 )
                    setSelected( null );
                else
                    setSelected( container.get( Math.min( index, container.size()-1 ) ) );
            }
        } );

        final WebMarkupContainer menu = createNewMenu();
        addOrReplace( new AjaxLink( "new-item", new Model("New...") ){
            public void onClick( AjaxRequestTarget target ) {
                menuVisible = !menuVisible;
                target.addComponent( menu );
            }
        } );
        addOrReplace( menu );
    }

    protected Panel getPager( String id ) {
        return new EmptyPanel( id );
    }

    private WebMarkupContainer createNewMenu() {
        final ListView items = new ListView( "new-popup-item", new RefPropertyModel( this, "container.allowedClasses" ) ) {
            protected void populateItem( final ListItem item ) {
                final Class c = (Class) item.getModelObject();
                final AjaxLink link = new AjaxLink( "new-item-link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        try {
                            final Referenceable object = (Referenceable) c.newInstance();
                            Ref ref = object.persist();
                            Container container = getContainer();
                            container.add( object );
                            doAjaxSelection( ref, target );
                        } catch ( InstantiationException e ) {
                            e.printStackTrace();
                        } catch ( IllegalAccessException e ) {
                            e.printStackTrace();
                        }
                    }
                };
                item.add( link );
                String displayName = ContainerSummary.toDisplay( c.getSimpleName() );
                link.add( new Label( "new-item-text", displayName ) );
            }
        };

        WebMarkupContainer list = new WebMarkupContainer( "new-popup" );
        list.add( new AttributeModifier( "style", true, new AbstractReadOnlyModel(){
            public Object getObject() {
                return menuVisible? "display: block !important;" : "display: none !important;";
            } } ) );
        list.setOutputMarkupId( true );
        list.add( items );
        return list;
    }

    public Container getContainer() {
        return (Container) getModelObject();
    }

    public final SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        if ( this.selected != selected
             && ( this.selected == null || !this.selected.equals( selected ) ) ) {

            this.selected = selected;
            getSelectionManager().setSelected( selected );
        }
    }

    public void setSelected( int index ) {
        Container container = getContainer();
        if ( index >= 0 && index < container.size() )
            setSelected( container.get( index ) );
        else
            setSelected( null );
    }

    public void doAjaxSelection( Ref newSelection, AjaxRequestTarget target ) {
        getSelectionManager().doAjaxSelection( newSelection, target );
    }

    //============================
    public class DeferredProvider implements IDataProvider {
        private transient IDataProvider actual;
        private boolean summary;

        public DeferredProvider( boolean summary ) {
            this.summary = summary;
        }

        private synchronized IDataProvider getActual() {
            if ( actual == null )
                actual = summary ? getContainer().getSummary()
                                : getContainer();
            return actual;
        }

        public synchronized void detach() {
            actual = null;
        }

        public Iterator iterator( int first, int count ) {
            return getActual().iterator( first, count );
        }

        public IModel model( Object object ) {
            return getActual().model( object );
        }

        public int size() {
            return getActual().size();
        }
    }
}
