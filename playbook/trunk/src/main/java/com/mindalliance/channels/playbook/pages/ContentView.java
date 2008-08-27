package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.pages.filters.UserScope;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authentication.AuthenticatedWebSession;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * A visualization of the contents of a tab panel.
 */
public class ContentView extends Panel implements SelectionManager {

    private SelectionManager selectionManager;
    private boolean menuVisible;
    private Ref selected;
    private WebMarkupContainer menu;
    private static final long serialVersionUID = -7502067766103274758L;

    protected ContentView(
            String id, IModel<? extends Container> model,
            SelectionManager masterSelection ) {
        super( id, model );
        if ( masterSelection == null || model == null )
            throw new IllegalArgumentException();
        selectionManager = masterSelection;
        setOutputMarkupId( true );

        load();
    }

    protected void load() {
        addOrReplace( getPager( "content-pager" ) );

        addOrReplace(
                new Link( "content-delete" ) {
                    private static final long serialVersionUID =
                            -6769812495977103745L;

                    @Override
                    public boolean isEnabled() {
                        Ref ref = getSelected();
                        return ref != null && !ref.isReadOnly();
                    }

                    @Override
                    public void onClick() {
                        Container container = getContainer();
                        Ref ref = getSelected();
                        int index = container.indexOf( ref );
                        container.remove( ref );
                        if ( container.size() == 0 )
                            setSelected( null );
                        else
                            setSelected(
                                    container.get(
                                            Math.min(
                                                    index, container.size()
                                                           - 1 ) ) );
                    }
                } );

        menu = createNewMenu();
        addOrReplace(
                new AjaxLink<String>(
                        "new-item", new Model<String>( "New..." ) ) {
                    private static final long serialVersionUID =
                            5290637379338712371L;

                    @Override
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

    private UserScope getUserScope() {
        Object container = getContainer();
        while ( container != null ) {
            if ( container instanceof FilteredContainer )
                container = ((FilteredContainer) container).getData();
            else if ( container instanceof Tab )
                container = ((Tab) container).getBase();
            else if ( container instanceof UserScope )
                return (UserScope) container;
            else
                container = null;
        }

        return (UserScope) container;
    }

    private Ref getTarget( Object object ) {
        Object objectClass = object.getClass();
        if ( Channels.contentClasses().contains( objectClass ) )
            return Channels.reference();

        PlaybookSession session = (PlaybookSession) AuthenticatedWebSession.get();
        Ref uRef = session.getUser();
        if ( uRef != null && User.contentClasses().contains( objectClass ) )
            return uRef;

        return getSelected();
    }

    /**
     * Create a new referenceable with all the trimmings
     * and add it to the container.
     * @param c the class of the new instance
     * @return the new object
     */
    private Ref createInstance( Class<? extends Referenceable> c ) {

        try {
            Referenceable object = c.newInstance();
            Container container = getContainer();
            Map<Method, Object> defaults =
                    container.getSummary().getCommonValues( c );
            for ( Method setter : defaults.keySet() ) {
                Object value = defaults.get( setter );
                setter.invoke( object, value );
            }

            Ref ref = object.persist();
            getUserScope().add( getTarget( object ), object );
            container.detach();

            return ref;
        } catch ( InstantiationException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Return classes that can be created, given current selection.
     * @return sorted list of class
     */
    public List<Class<?>> getCreatableClasses() {
        Collection<Class<?>> result = new TreeSet<Class<?>>(
            new Comparator<Class<?>>(){
                public int compare( Class<?> o1, Class<?> o2 ) {
                    return ContainerSummary
                            .toDisplay( o1.getSimpleName() )
                            .compareTo(
                                    ContainerSummary.toDisplay(
                                            o2.getSimpleName() ) );
                }
            } );
        result.addAll( getContainer().getAllowedClasses() );
        Ref selection = getSelected();
        if ( selection != null ) {
            Referenceable r = selection.deref();
            result.addAll( r.childClasses() );
        }

        return new ArrayList<Class<?>>( result );
    }

    private WebMarkupContainer createNewMenu() {

        WebMarkupContainer list = new WebMarkupContainer( "new-popup" );
        list.setOutputMarkupId( true );
        list.add(
                new AttributeModifier(
                        "style", true, new AbstractReadOnlyModel() {
                    private static final long serialVersionUID =
                            -4488949702777520421L;

                    @Override
                    public Object getObject() {
                        return menuVisible ?
                               "display: block !important;" :
                               "display: none !important;";
                    }
                } ) );
        list.add(
                new ListView<Class<? extends Referenceable>>(
                        "new-popup-item", new RefPropertyModel(
                        ContentView.this, "creatableClasses" ) ) {
                    private static final long serialVersionUID =
                            3109213407264000628L;

                    @Override
                    protected void populateItem(
                            ListItem<Class<? extends Referenceable>> item ) {
                        final Class<? extends Referenceable> c =
                                item.getModelObject();
                        AjaxLink<?> link = new AjaxLink( "new-item-link" ) {
                            private static final long serialVersionUID =
                                    5162954411019183107L;

                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                Ref newObject = createInstance( c );
                                menuVisible = false;
                                target.addComponent( menu );
                                doAjaxSelection( newObject, target );
                            }
                        };
                        item.add( link );
                        link.add(
                                new Label(
                                        "new-item-text",
                                        ContainerSummary.toDisplay(
                                                c.getSimpleName() ) ) );
                    }
                } );
        return list;
    }

    public Container getContainer() {
        return (Container) getDefaultModelObject();
    }

    public final SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref ref ) {
        if ( selected != ref && ( selected == null || !selected.equals(
                ref ) ) ) {

            this.selected = ref;
            getSelectionManager().setSelected( ref );
        }
    }

    public void setSelected( int index ) {
        Container container = getContainer();
        if ( index >= 0 && index < container.size() )
            setSelected( container.get( index ) );
        else
            setSelected( null );
    }

    public void doAjaxSelection( Ref ref, AjaxRequestTarget target ) {
        getSelectionManager().doAjaxSelection( ref, target );
    }

    //============================
    public class DeferredProvider implements IDataProvider<Ref> {

        private transient IDataProvider<Ref> actual;
        private static final long serialVersionUID = 1487122522996998407L;

        public DeferredProvider() {
        }

        private synchronized IDataProvider<Ref> getActual() {
            if ( actual == null )
                actual = getContainer();
            return actual;
        }

        public synchronized void detach() {
            actual = null;
        }

        public Iterator<? extends Ref> iterator( int first, int count ) {
            return getActual().iterator( first, count );
        }

        public IModel<Ref> model( Ref object ) {
            return getActual().model( object );
        }

        public int size() {
            return getActual().size();
        }
    }

    public class DeferredSummary implements IDataProvider<RefMetaProperty> {

        private transient IDataProvider<RefMetaProperty> actual;
        private static final long serialVersionUID = 9032999745902649705L;

        public DeferredSummary() {
        }

        private synchronized IDataProvider<RefMetaProperty> getActual() {
            if ( actual == null )
                actual = getContainer().getSummary();
            return actual;
        }

        public synchronized void detach() {
            actual = null;
        }

        public Iterator<? extends RefMetaProperty> iterator(
                int first, int count ) {
            return getActual().iterator( first, count );
        }

        public IModel<RefMetaProperty> model( RefMetaProperty object ) {
            return getActual().model( object );
        }

        public int size() {
            return getActual().size();
        }
    }
}
