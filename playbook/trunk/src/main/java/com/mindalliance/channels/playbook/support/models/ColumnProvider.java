package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ...
 */
public class ColumnProvider implements IDataProvider, IDetachable {

    private IDataProvider data;
    private Set<Class<?>> classes ;
    private Map<String,RefMetaProperty> index ;
    private List<RefMetaProperty> columns ;

    public ColumnProvider( IDataProvider data ) {
        this.data = data;
    }

    private List<RefMetaProperty> getColumns() {
        if ( columns == null )
            populate();
        return columns;
    }

    private Map<String, RefMetaProperty> getIndex() {
        if ( index == null )
            populate();
        return index;
    }

    public Set<Class<?>> getClasses() {
        if ( classes == null )
            populate();
        return Collections.unmodifiableSet( classes );
    }

    private void populate() {
        Iterator i = data.iterator( 0, data.size() );
        classes = new HashSet<Class<?>>();
        index = new HashMap<String,RefMetaProperty>();
        columns = new ArrayList<RefMetaProperty>();

        while ( i.hasNext() ){
            Object item = i.next();
            if ( item instanceof Ref )
                item = ((Ref) item).deref();
            classes.add( item.getClass() );

            try {
                final PropertyDescriptor[] props =
                        Introspector.getBeanInfo( item.getClass(), ReferenceableImpl.class )
                            .getPropertyDescriptors();
                for ( PropertyDescriptor pd : props ){
                    final String name = pd.getName();
                    final Method getter = pd.getReadMethod();
                    if ( !index.containsKey( name ) && getter != null ) {
                        final Class<?> type = pd.getPropertyType();
                        if ( !type.isArray() && !Collection.class.isAssignableFrom( type ) ) {
                            final Object value = getter.invoke( item );
                            if ( value != null && !value.toString().trim().isEmpty() ) {
                                final RefMetaProperty rmp = new RefMetaProperty( name, type, toDisplay( name ) );
                                index.put( name, rmp );
                                columns.add( rmp );
                            }
                        }
                    }
                }
            } catch ( IntrospectionException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }
    }

    public Iterator<RefMetaProperty> iterator( int first, int count ) {
        return getColumns().subList( first, first+count ).iterator();
    }

    public int size() {
        return getColumns().size();
    }

    public IModel model( Object object ) {
        return new Model( (Serializable) object );
    }

    public void detach() {
        columns = null;
        index = null;
        classes = null;
    }

    public static String toDisplay( String propName ) {
        StringBuffer b = new StringBuffer();
        if ( propName.length() > 0 ) {
            b.append( Character.toUpperCase( propName.charAt( 0 ) ) );
            for ( int i=1; i < propName.length() ; i++ ) {
                final char c = propName.charAt( i );
                if ( Character.isUpperCase( c ) )
                    b.append( ' ' );
                b.append( c );
            }

        }
        return b.toString();
    }

    public boolean includes( String name ) {
        return getIndex().containsKey( name );
    }

    public RefMetaProperty get( String name ) {
        return getIndex().get( name );
    }
}
