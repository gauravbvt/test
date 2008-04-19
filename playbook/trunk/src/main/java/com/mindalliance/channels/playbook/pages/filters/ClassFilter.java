package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Accept anything of the given objectType.
 */
public class ClassFilter extends Filter {

    private Class<?> objectType;
    private Class<?> filtersType;

    public ClassFilter( String collapsed, String expanded, Class<?> objectType, Container container ) {
        super( collapsed, expanded, container );
        this.objectType = objectType;

        try {
            filtersType = Class.forName( objectType.getSimpleName() + "Filter" );
        } catch ( ClassNotFoundException e ) {
            filtersType = null;
        }
    }

    public ClassFilter( Class<?> objectType, Container container ) {
        this( collapsedText( objectType ), expandedText( objectType ), objectType, container );
    }

    public final Class<?> getObjectType() {
        return objectType;
    }

    public Class<?> getFiltersType() {
        return filtersType;
    }

    public boolean match( Ref object ) {
        return getObjectType().isAssignableFrom( object.deref().getClass() );
    }

    protected List<Filter> createChildren() {
        List<Filter> result = new ArrayList<Filter>();
        Set<Class<?>> subclasses = getSubclasses( getObjectType() );
        Container filtered = new FilteredContainer( getContainer(), this, true );

        if ( subclasses.size() > 1 )
            for ( Class<?> c : subclasses )
                result.add( new ClassFilter( c, filtered ) );
        else if ( getFiltersType() != null ) try {
            // Apply specialized filters
            AbstractFilters fs = (AbstractFilters) getFiltersType().newInstance();
            return fs.getFilters( filtered );

            } catch ( InstantiationException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }

        return result;
    }

    public static String collapsedText( Class<?> clazz ) {
        return MessageFormat.format( "any {0}",
            ColumnProvider.toDisplay( clazz.getSimpleName() ).toLowerCase() );
    }

    public static String expandedText( Class<?> clazz ) {
        return MessageFormat.format( "{0}s...",
            ColumnProvider.toDisplay( clazz.getSimpleName() ).toLowerCase());
    }

    private Set<Class<?>> getSubclasses( Class<?> stopClass ) {
        Set<Class<?>> classes = getContainer().getColumnProvider().getClasses();
        Map<Class<?>,Set<Class<?>>> map = new HashMap<Class<?>,Set<Class<?>>>();

        // Build subclass map
        for ( Class<?> c : classes ) {
            Class<?> sc = c;
            while( sc != stopClass && stopClass.isAssignableFrom( sc ) ) {
                Class<?> ssc = sc.getSuperclass();
                Set<Class<?>> value = map.get( ssc );
                if ( value == null )
                    value = new HashSet<Class<?>>();
                value.add( sc );
                map.put( ssc, value );
                sc = ssc ;
            }
        }

        return simplifiedSet(
            new TreeSet<Class<?>>( new Comparator<Class<?>>(){
                public int compare( Class<?> o1, Class<?> o2 ) {
                    return collapsedText( o1 ).compareTo( collapsedText( o2 ) );
                } } ),
            stopClass,
            map,
            true );
    }

    private Set<Class<?>> simplifiedSet(
            Set<Class<?>> classes, Class<?> objectClass,
            Map<Class<?>, Set<Class<?>>> map,
            boolean dig ) {

        final Set<Class<?>> subs = map.get( objectClass );
        if ( subs == null )
            classes.add( objectClass );

        else if ( subs.size() == 1 )
            simplifiedSet( classes, subs.iterator().next(), map, dig );

        else if ( !dig )
            classes.add( objectClass );

        else for ( Class<?> c : subs ) {
            Set<Class<?>> ssubs = map.get( c );
            if ( ssubs == null || ssubs.size() > 1 )
                classes.add( c );
            else
                simplifiedSet( classes, c, map, false );
        }

        return classes;
    }
}
