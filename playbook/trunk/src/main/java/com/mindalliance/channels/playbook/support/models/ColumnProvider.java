package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import org.apache.wicket.Session;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * ...
 */
public class ColumnProvider extends BeanImpl implements IDataProvider {

    private Container data;

    private transient Map<Class<?>,ClassUse> usage ;

    private transient Set<Class<?>> classes ;
    private transient Map<String,RefMetaProperty> columnIndex;
    private transient List<RefMetaProperty> columns ;

    public ColumnProvider( Container data ) {
        this.data = data;
    }

    public List<String> transientProperties() {
        final List<String> list = super.transientProperties();
        list.addAll( Arrays.asList( "usage", "classes", "columnIndex", "columns" ) );
        return list;
    }

    public synchronized List<RefMetaProperty> getColumns() {
        if ( columns == null ) {
            ArrayList<RefMetaProperty> list = new ArrayList<RefMetaProperty>( getColumnIndex().values() );
            if ( getUsage().size() > 1 ) {
                // More than on class. Add a "Class" column
                list.add( 0, new RefMetaProperty( "type", String.class, "Type" ) );
            }
            columns = Collections.unmodifiableList( list );
        }
        return columns;
    }

    /**
     * Compute the column index for combined classes in the data.
     * @return property descriptors, indexed by display name
     */
    private synchronized Map<String, RefMetaProperty> getColumnIndex() {
        if ( columnIndex == null ) {
            Map<String, RefMetaProperty> result = new TreeMap<String,RefMetaProperty>();
            for( ClassUse use : getUsage().values() )
                for ( RefMetaProperty p : use.getDistinctColumns() )
                    result.put( p.getDisplayName(), p );

            columnIndex = result;
        }
        return columnIndex;
    }

    private synchronized Map<Class<?>,ClassUse> getUsage() {
        if ( usage == null ) {
            Map<Class<?>,ClassUse> result = new TreeMap<Class<?>,ClassUse>(
                new Comparator<Class<?>>(){
                    public int compare( Class<?> o1, Class<?> o2 ) {
                        return toDisplay( o1.getSimpleName() ).compareTo( toDisplay( o2.getSimpleName() ) );
                    }
                }
            );

            Iterator i = data.iterator( 0, data.size() );
            while ( i.hasNext() ){
                Ref ref = (Ref) i.next();
                Referenceable object = ref.deref();
                Class<?> objectClass = object.getClass();
                ClassUse use = result.get( objectClass );
                if ( use == null ) {
                    use = new ClassUse( objectClass );
                    result.put( objectClass, use);
                }
                use.grok( object );
            }

            usage = result;
        }
        return usage;
    }

    public synchronized Set<Class<?>> getClasses() {
        if ( classes == null )
            classes = Collections.unmodifiableSet( getUsage().keySet() );
        return classes ;
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

    public synchronized void detach() {
        columns = null;
        columnIndex = null;
        classes = null;
        usage = null;
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

    public static String valueToDisplay( Object object ) {
        if ( object == null )
            return "" ;

        if ( object instanceof Date )
            return DateFormat.getDateInstance( DateFormat.SHORT, Session.get().getLocale() )
                    .format( (Date) object );

        return object.toString().trim();
    }

    public boolean includes( String name ) {
        return getColumnIndex().containsKey( name );
    }

    public RefMetaProperty get( String name ) {
        return getColumnIndex().get( name );
    }

    //====================================
    /**
     * Class usage, mapped by property names.
     */
    static class ClassUse {
        private Map<PropertyDescriptor,Set<Object>> values = new HashMap<PropertyDescriptor,Set<Object>>();
        private List<PropertyDescriptor> properties;
        private boolean hasTransients;
        private Set<String> transients;

        public ClassUse( Class<?> c ) {
            properties = new ArrayList<PropertyDescriptor>();
            try {
                for ( PropertyDescriptor pd :
                        Introspector.getBeanInfo( c, ReferenceableImpl.class ).getPropertyDescriptors() ) {

                    final boolean readable = pd.getReadMethod() != null
                                             && Modifier.isPublic( pd.getReadMethod().getModifiers() );
                    final boolean writeable = pd.getWriteMethod() != null
                                              && Modifier.isPublic( pd.getWriteMethod().getModifiers() );
                    final Class<?> type = pd.getPropertyType();
                    final boolean isCollection = type.isArray() || Collection.class.isAssignableFrom( type );

                    if ( readable && writeable && !isCollection )
                        properties.add( pd );
                }

                try {
                    c.getMethod( "transientProperties" );
                    hasTransients = true;
                } catch ( NoSuchMethodException e ) {
                    hasTransients = false;
                }

            } catch ( IntrospectionException e ) {
                e.printStackTrace();
            }
        }

        private Set<String> getTransients() {
            if ( transients == null )
                transients = new HashSet<String>();
            return transients;
        }

        private Set<String> getTransients( Referenceable item ) {
            if ( transients == null ) {
                Set<String> result = getTransients();
                Class itemClass = item.getClass();
                try {
                    List<String> t = (List<String>) itemClass.getMethod( "transientProperties" )
                                            .invoke( item );

                    result.addAll( t );

                } catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                } catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                } catch ( NoSuchMethodException e ) {
                    e.printStackTrace();
                }
            }

            return transients;
        }

        public void grok( Referenceable item ) {
            try {
                for ( PropertyDescriptor pd : properties ){
                    if ( !hasTransients || !getTransients( item ).contains( pd.getName() ) ) {
                        final Method getter = pd.getReadMethod();
                        final Object value = getter.invoke( item );
                        Set<Object> propValues = values.get( pd );
                        if ( propValues == null ) {
                            propValues = new HashSet<Object>();
                            values.put( pd, propValues );
                        }
                        propValues.add( valueToDisplay( value ) );
                    }
                }
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }

        private RefMetaProperty newRMP( PropertyDescriptor p ) {
            String name = p.getName();
            return new RefMetaProperty( name, p.getPropertyType(), toDisplay( name ) );
        }

        public Set<RefMetaProperty> getAllColumns() {
            Set<RefMetaProperty> result = new TreeSet<RefMetaProperty>();
            for ( PropertyDescriptor p : properties )
                if ( !hasTransients || !getTransients().contains( p.getName() ) )
                    result.add( newRMP( p ) );
            return result;
        }

        public Set<RefMetaProperty> getDistinctColumns() {
            Set<RefMetaProperty> result = new TreeSet<RefMetaProperty>();
            for ( PropertyDescriptor p : properties ) {
                if ( !hasTransients || !getTransients().contains( p.getName() ) )
                    if ( values.get( p ).size() > 1 )
                        result.add( newRMP( p ) );
            }
            return result;
        }
    }
}
