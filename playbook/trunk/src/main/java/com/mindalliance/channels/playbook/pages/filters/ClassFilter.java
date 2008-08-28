package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Accept anything of the given objectType. */
public class ClassFilter extends Filter {

    private Class<?> objectType;
    private Class<?> filtersType;
    private static final String PACKAGE = "com.mindalliance.channels.playbook.pages.filters.";
    private static final long serialVersionUID = 4079849251018097695L;
    private static final Logger Log = LoggerFactory
            .getLogger( ClassFilter.class );

    public ClassFilter() {
        objectType = null;
        filtersType = null;
    }

    public ClassFilter(
            String collapsed, String expanded, Class<?> objectType ) {
        super( collapsed, expanded );
        setObjectType( objectType );
    }

    public ClassFilter( Class<?> objectType ) {
        this(
                collapsedText( objectType ), expandedText( objectType ),
                objectType );
    }

    @Override
    public synchronized Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put( "objectType", getObjectType().getName() );
        if ( getFiltersType() != null )
            map.put( "filtersType", getFiltersType().getName() );

        return map;
    }

    @Override
    public void initFromMap( Map<String, Object> map ) {
        super.initFromMap( map );
        String filterClassName = (String) map.get( "filtersType" );
        String objectClassName = (String) map.get( "objectType" );
        try {
            objectType = Class.forName( objectClassName );
        } catch ( ClassNotFoundException e ) {
            Log.warn( objectClassName + " not found", e );
        }
        if ( filterClassName != null )
            try {
                filtersType = Class.forName( filterClassName );
            } catch ( ClassNotFoundException e ) {
                Log.warn( filterClassName + " not found", e );
            }
    }

    public final Class<?> getObjectType() {
        return objectType;
    }

    public final void setObjectType( Class<?> type ) {
        objectType = type;
        filtersType = null;
        while ( type != ReferenceableImpl.class && filtersType == null )
            try {
                filtersType = Class.forName(
                        MessageFormat.format( "{0}{1}Filters",
                                PACKAGE, type.getSimpleName() ) );
            } catch ( ClassNotFoundException ignored ) {
                type = type.getSuperclass();

                // Don't apply filters if already applied in a super-filter
                Object parent = getParent();
                if ( parent instanceof ClassFilter && type.equals(
                        ( (ClassFilter) parent ).getFiltersType() ) )
                    break;
            }
    }

    public final void setFiltersType( Class<?> filtersType ) {
        this.filtersType = filtersType;
    }

    public final Class<?> getFiltersType() {
        return filtersType;
    }

    @Override
    public boolean isMatching( Ref object ) {
        return getObjectType().isAssignableFrom( object.deref().getClass() );
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return getObjectType().isAssignableFrom( c );
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        List<Filter> result = new ArrayList<Filter>();
        Collection<Class<?>> subclasses = getSubclasses();
        Container filtered = new FilteredContainer(
                getContainer(), this, true );

        // Impersonate...
        if ( subclasses.size() == 1 && getFiltersType() == null ) {
            Class<?> c = subclasses.iterator().next();
//            if ( card( getObjectType() ) == card( c ) ) {
            setObjectType( c );
            setExpandedText( expandedText( c ) );
            setCollapsedText( collapsedText( c ) );
            subclasses = getSubclasses();
//            }
        }

        if ( !subclasses.isEmpty() && isConcrete( getObjectType() ) )
            result.add( new ConcreteClassFilter( getObjectType() ) );
        addSubclassFilters( result, subclasses, filtered );
        addOtherFilters( result, filtered );
        addLeaves( result, filtered );

        for ( Filter f : result )
            f.setSelected( selectionState );

        return result;
    }

    private void addLeaves(
            Collection<Filter> result, Iterable<Ref> filtered ) {

        if ( isShowingLeaves() ) {
            for ( Ref ref : filtered ) {
                Bean object = ref.deref();
                if ( object.getClass() == objectType )
                    result.add( new RefFilter( ref ) );
                object.detach();
            }
        }
    }

    private void addOtherFilters(
            Collection<Filter> result, Container filtered ) {

        ClassFilter parent = null;
        if ( getParent() != null && getParent() instanceof ClassFilter )
            parent = (ClassFilter) getParent();

        Class<?> type = getFiltersType();
        if ( type != null && ( parent == null || !type
                .equals( parent.getFiltersType() ) ) )
            try {
                // Apply specialized filters
                AbstractFilters fs = (AbstractFilters) type.newInstance();
                result.addAll( fs.getFilters( filtered, false ) );
            } catch ( InstantiationException e ) {
                Log.warn( "Unable to instantiate filters for " + type, e );
            } catch ( IllegalAccessException e ) {
                Log.warn( "Error accessing " + type, e );
            }
    }

    private void addSubclassFilters(
            Collection<Filter> result, Collection<Class<?>> subclasses,
            Container filtered ) {
        if ( subclasses.size() >= 0 )
            for ( Class<?> c : subclasses ) {
                ClassFilter cf = new ClassFilter( c );
                cf.setShowingLeaves( isShowingLeaves() );
                cf.setContainer( filtered );
                result.add( cf );
            }
    }

    private boolean isConcrete( Class<?> sup ) {
        return getContainer().getSummary().instancesOf( sup ) != 0;
    }

    private static String collapsedText( Class<?> clazz ) {
        return MessageFormat.format(
                "Any {0}", ContainerSummary
                .toDisplay( clazz.getSimpleName() ).toLowerCase() );
    }

    private static String expandedText( Class<?> clazz ) {
        String s = ContainerSummary.toDisplay( clazz.getSimpleName() );
        s = s.substring( 0, 1 ) + s.substring( 1 ).toLowerCase();
        return MessageFormat.format( "{0}...", RefUtils.pluralize( s ) );
    }

    private Collection<Class<?>> getSubclasses() {
        Set<Class<?>> result = new TreeSet<Class<?>>(
                new Comparator<Class<?>>() {
                    public int compare( Class<?> o1, Class<?> o2 ) {
                        return collapsedText( o1 )
                                .compareTo( collapsedText( o2 ) );
                    }
                } );

        Map<Class<?>, Set<Class<?>>> map = getRawSubclasses();
        Set<Class<?>> classes = map.get( getObjectType() );
        if ( classes != null )
            for ( Class<?> c : classes )
                simplifySet( result, c, map, true );

        return result;
    }

    private Map<Class<?>, Set<Class<?>>> getRawSubclasses() {
        Map<Class<?>, Set<Class<?>>> result = new HashMap<Class<?>, Set<Class<?>>>();

        for ( Class<?> c : getContainer().getSummary().getClasses() ) {
            Class<?> sc = c;
            while ( sc != getObjectType() && getObjectType()
                    .isAssignableFrom( sc ) ) {
                Class<?> ssc = sc.getSuperclass();
                Set<Class<?>> value = result.get( ssc );
                if ( value == null )
                    value = new HashSet<Class<?>>();
                value.add( sc );
                result.put( ssc, value );
                sc = ssc;
            }
        }
        return result;
    }

    private void simplifySet(
            Set<Class<?>> classes, Class<?> objectClass,
            Map<Class<?>, Set<Class<?>>> map, boolean dig ) {

        Set<Class<?>> subs = map.get( objectClass );
        if ( subs == null )
            classes.add( objectClass );

        else {
            if ( subs.size() == 1 ) {
                if ( isConcrete( objectClass ) ) {
                    // Superclass is not abstract and has instances
                    classes.add( objectClass );
                } else
                    simplifySet( classes, subs.iterator().next(), map, dig );
            } else // if ( !dig )
                classes.add( objectClass );

//            else
//                for ( Class<?> c : subs ) {
//                    Set<Class<?>> ssubs = map.get( c );
//                    if ( ssubs == null || ssubs.size() > 1 )
//                        classes.add( c );
//                    else
//                        simplifySet( classes, c, map, false );
//                }
        }
    }
}
