package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ifm.Agent;
import com.mindalliance.channels.playbook.ifm.Locatable;
import com.mindalliance.channels.playbook.ifm.playbook.Event;
import com.mindalliance.channels.playbook.ifm.project.resources.Resource;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import org.apache.wicket.Session;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** ... */
public class ContainerSummary extends BeanImpl
        implements IDataProvider<RefMetaProperty> {

    private static final Logger Log =
            LoggerFactory.getLogger( ContainerSummary.class );

    private Container data;
    private transient Map<Class<?>, ClassUse> usage;
    private transient Map<String, RefMetaProperty> columnIndex;
    private transient List<RefMetaProperty> columns;
    private transient boolean timelineable;
    private transient boolean mappable;
    private transient boolean flowable;
    private transient boolean networkable;
    private static final long serialVersionUID = -6905202788793276032L;

    public ContainerSummary( Container data ) {
        if ( data == null )
            throw new IllegalArgumentException();
        this.data = data;
    }

    @Override
    public List<String> transientProperties() {
        List<String> list = super.transientProperties();
        list.addAll(
                Arrays.asList(
                        "timelineable", "mappable", "flowable", "usage",
                        "classes", "columnIndex", "columns" ) );
        return list;
    }

    private synchronized List<RefMetaProperty> getColumns() {
        if ( columns == null ) {
            List<RefMetaProperty> list = new ArrayList<RefMetaProperty>(
                    getColumnIndex().values() );
            if ( getUsage().size() > 1 ) {
                // More than one class. Add a "Class" column
                list.add( 0,
                    new RefMetaProperty( "type", String.class, "Type" ) );
            }
            columns = Collections.unmodifiableList( list );
        }
        return columns;
    }

    /**
     * Get common property values for instances of given class.
     *
     * @param c the class
     * @return values, indexed by setter
     */
    public Map<Method, Object> getCommonValues( Class<?> c ) {
        ClassUse use = getUsage().get( c );
        return use == null ?
               new HashMap<Method, Object>() :
               use.getCommonValues();
    }

    /**
     * Compute the column index for combined classes in the data.
     *
     * @return property descriptors, indexed by display name
     */
    private synchronized Map<String, RefMetaProperty> getColumnIndex() {
        if ( columnIndex == null ) {
            Map<String, RefMetaProperty> result = new TreeMap<String, RefMetaProperty>();
            for ( ClassUse use: getUsage().values() ) {
                Iterator<RefMetaProperty> cols = use.distinctColumns();
                if ( !cols.hasNext() ) // all values same (or only one object)
                    cols = use.keyColumns();
                while ( cols.hasNext() ) {
                    RefMetaProperty p = cols.next();
                    result.put( p.getDisplayName(), p );
                }
            }

            columnIndex = result;
        }
        return columnIndex;
    }

    private synchronized Map<Class<?>, ClassUse> getUsage() {
        if ( usage == null ) {
            Map<Class<?>, ClassUse> result = new TreeMap<Class<?>, ClassUse>(
                    new Comparator<Class<?>>() {
                        public int compare( Class<?> o1, Class<?> o2 ) {
                            return toDisplay( o1.getSimpleName() ).compareTo(
                                    toDisplay( o2.getSimpleName() ) );
                        }
                    } );

            mappable = false;
            timelineable = false;
            flowable = false;

            for ( Ref ref : data ) {
                assert ref != null;
                Referenceable object = ref.deref();

                assert object != null;
                Class<?> objectClass = object.getClass();
                ClassUse use = result.get( objectClass );
                if ( use == null ) {
                    if ( !mappable && object instanceof Locatable )
                        mappable = true;
                    if ( !timelineable && object instanceof Event )
                        timelineable = true;
                    if ( !flowable &&
                         ( object instanceof Event
                            || object instanceof Agent ) )
                        flowable = true;
                    if ( !networkable && object instanceof Resource )
                        networkable = true;
                    use = new ClassUse( objectClass );
                    result.put( objectClass, use );
                }
                use.tally();
                use.grok( object );
            }

            usage = result;
        }
        return usage;
    }

    public Iterator<Class<?>> classIterator() {
        return getUsage().keySet().iterator();
    }

    public Iterator<RefMetaProperty> iterator( int first, int count ) {
        return getColumns().subList( first, first + count ).iterator();
    }

    public int size() {
        return getColumns().size();
    }

    public IModel<RefMetaProperty> model( RefMetaProperty object ) {
        return new Model<RefMetaProperty>( object );
    }

    @Override
    public synchronized void detach() {
        columns = null;
        columnIndex = null;
        usage = null;
    }

    public static String toDisplay( String propName ) {
        StringBuilder b = new StringBuilder( 32 );
        if ( propName.length() > 0 ) {
            b.append( Character.toUpperCase( propName.charAt( 0 ) ) );
            for ( int i = 1; i < propName.length(); i++ ) {
                char c = propName.charAt( i );
                if ( Character.isUpperCase( c ) )
                    b.append( ' ' );
                b.append( c );
            }
        }
        return b.toString();
    }

    public int instancesOf( Class<?> clazz ) {
        ClassUse classUse = getUsage().get( clazz );
        return classUse == null ? 0 : classUse.getCount();
    }

    public boolean contains( String name ) {
        return getColumnIndex().containsKey( name );
    }

    public RefMetaProperty get( String name ) {
        return getColumnIndex().get( name );
    }

    public synchronized boolean isTimelineable() {
        getUsage();
        return timelineable;
    }

    public synchronized boolean isMappable() {
        getUsage();
        return mappable;
    }

    public synchronized boolean isFlowable() {
        getUsage();
        return flowable;
    }

    public synchronized boolean isNetworkable() {
        getUsage();
        return networkable;
    }

    //====================================
    /** Class usage, mapped by property names. */
    static class ClassUse {

        private Map<String, Set<Object>> values = new HashMap<String, Set<Object>>();
        private Map<String, Set<String>> printValues = new HashMap<String, Set<String>>();
        private Collection<PropertyDescriptor> properties;
        private Set<String> hiddenProperties;
        private Set<String> keyProperties;
        private int count;
        private Set<RefMetaProperty> allColumns;

        ClassUse( Class<?> c ) {
            properties = new ArrayList<PropertyDescriptor>();
            try {
                for ( PropertyDescriptor pd : Introspector
                        .getBeanInfo( c, ReferenceableImpl.class )
                        .getPropertyDescriptors() ) {

                    boolean readable = pd.getReadMethod() != null
                        && Modifier.isPublic(
                            pd.getReadMethod().getModifiers() );
                    Class<?> type = pd.getPropertyType();
                    boolean isCollection = type.isArray()
                        || Collection.class.isAssignableFrom( type );

                    if ( readable && !isCollection )
                        properties.add( pd );
                }
            } catch ( IntrospectionException e ) {
                LoggerFactory.getLogger( getClass() ).error(
                        "Unable to read properties", e );
            }
        }

        public static String valueToDisplay( Object object ) {
            if ( !isBound( object ) )
                return "";
            else if ( object instanceof Ref )
                return ((Ref)object).deref().toString().trim();

            else if ( object instanceof Date )
                return DateFormat.getDateInstance(
                        DateFormat.SHORT, Session.get().getLocale() )
                        .format( (Date) object );

            return object.toString().trim();
        }

        private static boolean isBound( Object value ) {
            return value != null
                   && !( value instanceof Ref && !( (Ref) value ).isFresh() );
        }

        private static boolean isWritable( PropertyDescriptor pd ) {
            Method writeMethod = pd.getWriteMethod();
            return writeMethod != null
                   && Modifier.isPublic( writeMethod.getModifiers() );
        }

        private Set<String> getHiddenProperties() {
            return hiddenProperties;
        }

        private void setHiddenProperties( Set<String> hiddenProperties ) {
            this.hiddenProperties = hiddenProperties;
        }

        private Set<String> getKeyProperties() {
            return keyProperties;
        }

        private void setKeyProperties( Set<String> keyProperties ) {
            this.keyProperties = keyProperties;
        }

        @SuppressWarnings( { "unchecked" } )
        public void grok( Referenceable item ) {
            try {
                if ( getHiddenProperties() == null )
                    setHiddenProperties( item.hiddenProperties() );
                if ( getKeyProperties() == null )
                    setKeyProperties( item.keyProperties() );

                for ( PropertyDescriptor pd : properties ) {
                    String name = pd.getName();
                    if ( !getHiddenProperties().contains( name ) ) {
                        Method getter = pd.getReadMethod();
                        Object value = getter.invoke( item );
                        Set<String> propStrings = printValues.get( name );
                        if ( propStrings == null ) {
                            propStrings = new HashSet<String>();
                            printValues.put( name, propStrings );
                        }
                        propStrings.add( valueToDisplay( value ) );

                        Set<Object> propValues = values.get( name );
                        if ( propValues == null ) {
                            propValues = new HashSet<Object>();
                            values.put( name, propValues );
                        }
                        if ( isBound( value ) )
                            propValues.add( value );
                    }
                }
            } catch ( InvocationTargetException e ) {
                LoggerFactory.getLogger( getClass() ).error(
                        "Unable to get a property value", e );
            } catch ( IllegalAccessException e ) {
                LoggerFactory.getLogger( getClass() ).error(
                        "Unable to get a property value", e );
            }
        }

        public void tally() {
            count++;
        }

        private static RefMetaProperty newRMP( PropertyDescriptor p ) {
            String name = p.getName();
            return new RefMetaProperty(
                    name, p.getPropertyType(), toDisplay( name ) );
        }

        public Iterator<RefMetaProperty> allColumns() {
            if ( allColumns == null ) {
                Set<RefMetaProperty> result = new TreeSet<RefMetaProperty>();
                for ( PropertyDescriptor p : properties )
                    if ( !getHiddenProperties().contains( p.getName() ) )
                        result.add( newRMP( p ) );
                allColumns = result;
            }
            return allColumns.iterator() ;
        }

        public Iterator<RefMetaProperty> distinctColumns() {
            return new FilterIterator(){
                @Override
                boolean isApplicable( RefMetaProperty p ) {
                    return printValues.get( p.getPropertyName() ).size() > 1;
                }
            };
        }

        public Iterator<RefMetaProperty> keyColumns() {
            return new FilterIterator(){
                @Override
                boolean isApplicable( RefMetaProperty p ) {
                    return getKeyProperties().contains( p.getPropertyName() );
                }
            };
        }

        public Set<String> getPrintValues( String propertyName ) {
            return printValues.get( propertyName );
        }

        public Map<Method, Object> getCommonValues() {
            Map<Method, Object> result = new HashMap<Method, Object>();
            for ( PropertyDescriptor p : properties ) {
                String s = p.getName();
                if ( isWritable( p ) && !getHiddenProperties().contains( s )
                     && !getKeyProperties().contains( s ) ) {
                    Collection<Object> objects = values.get( s );
                    if ( objects.size() == 1 )
                        result.put(
                                p.getWriteMethod(), objects.iterator().next() );
                }
            }
            return result;
        }

        public int getCount() {
            return count;
        }

        //=========================================
        private abstract class FilterIterator
                implements Iterator<RefMetaProperty> {

            private Iterator<RefMetaProperty> all = allColumns();
            private RefMetaProperty next = null;

            private FilterIterator() {
            }

            private boolean isNotDone() {
                while ( next == null && all.hasNext() ) {
                    RefMetaProperty p = all.next();
                    if ( isApplicable( p ) )
                        next = p;
                }

                return next != null;
            }

            abstract boolean isApplicable( RefMetaProperty p );

            public boolean hasNext() {
                return isNotDone();
            }

            public RefMetaProperty next() {
                if ( !isNotDone() )
                    throw new NoSuchElementException();
                RefMetaProperty result = next;
                next = null;
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
