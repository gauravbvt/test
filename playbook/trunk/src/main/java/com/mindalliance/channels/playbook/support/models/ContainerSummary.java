package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ifm.Agent;
import com.mindalliance.channels.playbook.ifm.Locatable;
import com.mindalliance.channels.playbook.ifm.playbook.Event;
import com.mindalliance.channels.playbook.ifm.playbook.FlowAct;
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship;
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement;
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

/** ... */
public class ContainerSummary extends BeanImpl
        implements IDataProvider<RefMetaProperty> {

    private Container data;
    private transient Map<Class<?>, ClassUse> usage;
    private transient Set<Class<?>> classes;
    private transient Map<String, RefMetaProperty> columnIndex;
    private transient List<RefMetaProperty> columns;
    private transient boolean timelineable;
    private transient boolean mappable;
    private transient boolean flowable;
    private transient boolean networkable;
    private static final long serialVersionUID = -6905202788793276032L;

    public ContainerSummary( Container data ) {
        if ( data == null )
            throw new NullPointerException();
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

    public synchronized List<RefMetaProperty> getColumns() {
        if ( columns == null ) {
            List<RefMetaProperty> list = new ArrayList<RefMetaProperty>(
                    getColumnIndex().values() );
            if ( getUsage().size() > 1 ) {
                // More than on class. Add a "Class" column
                list.add(
                        0,
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
            boolean oneClassShown = data.size() == 1;
            for ( ClassUse use : getUsage().values() ) {
                Collection<RefMetaProperty> cols = use.getDistinctColumns();
                if ( oneClassShown && cols.isEmpty() )
                    cols = use.getAllColumns();
                for ( RefMetaProperty p : cols )
                    result.put( p.getDisplayName(), p );
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
                    if ( !mappable
                         && Locatable.class.isAssignableFrom( objectClass ) )
                        mappable = true;
                    if ( !timelineable
                         && Event.class.isAssignableFrom( objectClass ) )
                        timelineable = true;
                    if ( !flowable && (
                            Event.class.isAssignableFrom( objectClass )
                            || Agent.class.isAssignableFrom( objectClass ) ) )
                        flowable = true;
                    if ( !networkable && (
                            Agent.class.isAssignableFrom( objectClass )
                            || FlowAct.class.isAssignableFrom( objectClass )
                            || SharingAgreement.class
                                    .isAssignableFrom( objectClass )
                            || Relationship.class
                                    .isAssignableFrom( objectClass ) ) )
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

    public synchronized Set<Class<?>> getClasses() {
        if ( classes == null )
            classes = Collections.unmodifiableSet( getUsage().keySet() );
        return classes;
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

    static private boolean isBound( Object value ) {
        return value != null && !( value instanceof Ref && !( (Ref) value )
                .isFresh() );
    }

    public static String valueToDisplay( Object object ) {
        if ( !isBound( object ) )
            return "";

        if ( object instanceof Date )
            return DateFormat.getDateInstance(
                    DateFormat.SHORT, Session.get().getLocale() )
                    .format( (Date) object );

        return object.toString().trim();
    }

    public boolean includes( String name ) {
        return getColumnIndex().containsKey( name );
    }

    public RefMetaProperty get( String name ) {
        return getColumnIndex().get( name );
    }

    public boolean isTimelineable() {
        getUsage();
        return timelineable;
    }

    public boolean isMappable() {
        getUsage();
        return mappable;
    }

    public boolean isFlowable() {
        getUsage();
        return flowable;
    }

    public boolean isNetworkable() {
        getUsage();
        return networkable;
    }

    //====================================
    /** Class usage, mapped by property names. */
    static class ClassUse {

        private Map<PropertyDescriptor, Set<Object>> values = new HashMap<PropertyDescriptor, Set<Object>>();
        private Map<PropertyDescriptor, Set<String>> printValues = new HashMap<PropertyDescriptor, Set<String>>();
        private List<PropertyDescriptor> properties;
        private Set hiddenProperties;
        private Set keyProperties;
        private int count;

        public ClassUse( Class<?> c ) {
            properties = new ArrayList<PropertyDescriptor>();
            try {
                for ( PropertyDescriptor pd : Introspector
                        .getBeanInfo( c, ReferenceableImpl.class )
                        .getPropertyDescriptors() ) {

                    final boolean readable =
                            pd.getReadMethod() != null && Modifier.isPublic(
                                    pd.getReadMethod().getModifiers() );
                    final Class<?> type = pd.getPropertyType();
                    final boolean isCollection =
                            type.isArray() || Collection.class
                                    .isAssignableFrom( type );

                    if ( readable && !isCollection )
                        properties.add( pd );
                }
            } catch ( IntrospectionException e ) {
                e.printStackTrace();
            }
        }

        private boolean isWritable( PropertyDescriptor pd ) {
            return pd.getWriteMethod() != null && Modifier
                    .isPublic( pd.getWriteMethod().getModifiers() );
        }

        public Set getHiddenProperties() {
            return hiddenProperties;
        }

        public void setHiddenProperties( Set hiddenProperties ) {
            this.hiddenProperties = hiddenProperties;
        }

        public Set getKeyProperties() {
            return keyProperties;
        }

        public void setKeyProperties( Set keyProperties ) {
            this.keyProperties = keyProperties;
        }

        public void grok( Referenceable item ) {
            try {
                if ( getHiddenProperties() == null )
                    setHiddenProperties( item.hiddenProperties() );
                if ( getKeyProperties() == null )
                    setKeyProperties( item.keyProperties() );

                for ( PropertyDescriptor pd : properties ) {
                    if ( !getHiddenProperties().contains( pd.getName() ) ) {
                        final Method getter = pd.getReadMethod();
                        final Object value = getter.invoke( item );
                        Set<String> propStrings = printValues.get( pd );
                        if ( propStrings == null ) {
                            propStrings = new HashSet<String>();
                            printValues.put( pd, propStrings );
                        }

                        Set<Object> propValues = values.get( pd );
                        if ( propValues == null ) {
                            propValues = new HashSet<Object>();
                            values.put( pd, propValues );
                        }
                        if ( isBound( value ) )
                            propValues.add( value );
                        propStrings.add( valueToDisplay( value ) );
                    }
                }
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }

        public void tally() {
            count++;
        }

        private RefMetaProperty newRMP( PropertyDescriptor p ) {
            String name = p.getName();
            return new RefMetaProperty(
                    name, p.getPropertyType(), toDisplay( name ) );
        }

        public Set<RefMetaProperty> getAllColumns() {
            Set<RefMetaProperty> result = new TreeSet<RefMetaProperty>();
            for ( PropertyDescriptor p : properties )
                if ( !getHiddenProperties().contains( p.getName() ) )
                    result.add( newRMP( p ) );
            return result;
        }

        public Set<RefMetaProperty> getDistinctColumns() {
            Set<RefMetaProperty> result = new TreeSet<RefMetaProperty>();
            for ( PropertyDescriptor p : properties ) {
                if ( !getHiddenProperties().contains( p.getName() ) )
                    if ( printValues.get( p ).size() > 1 )
                        result.add( newRMP( p ) );
            }
            return result;
        }

        public Map<Method, Object> getCommonValues() {
            Map<Method, Object> result = new HashMap<Method, Object>();
            for ( PropertyDescriptor p : properties ) {
                String s = p.getName();
                if ( isWritable( p ) && !getHiddenProperties().contains( s )
                     && !getKeyProperties().contains( s ) ) {
                    Set<Object> objects = values.get( p );
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
    }
}
