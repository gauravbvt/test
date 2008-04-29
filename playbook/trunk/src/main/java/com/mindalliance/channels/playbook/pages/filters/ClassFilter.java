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

    private static final String Package = "com.mindalliance.channels.playbook.pages.filters." ;
    private Class<?> objectType;
    private Class<?> filtersType;

    public ClassFilter() {
        super();
    }

    public ClassFilter( String collapsed, String expanded, Class<?> objectType ) {
        super( collapsed, expanded );
        this.objectType = objectType;

        try {
            filtersType = Class.forName( Package + objectType.getSimpleName() + "Filters" );
        } catch ( ClassNotFoundException e ) {
            filtersType = null;
        }
    }

    public ClassFilter( Class<?> objectType ) {
        this( collapsedText( objectType ), expandedText( objectType ), objectType );
    }

    public Map toMap() {
        Map map = super.toMap();
        map.put( "objectType", getObjectType().getName() );
        if ( getFiltersType() != null )
            map.put( "filtersType", getFiltersType().getName() );

        return map;
    }

    public void initFromMap( Map map ) {
        super.initFromMap( map );
        try {
            objectType = Class.forName( (String) map.get( "objectType" ) );
            if ( map.containsKey( "filtersType" ) )
                filtersType = Class.forName( (String) map.get( "filtersType" ) );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    public final Class<?> getObjectType() {
        return objectType;
    }

    public void setObjectType( Class<?> objectType ) {
        this.objectType = objectType;
    }

    public void setFiltersType( Class<?> filtersType ) {
        this.filtersType = filtersType;
    }

    public Class<?> getFiltersType() {
        return filtersType;
    }

    public boolean match( Ref object ) {
        return getObjectType().isAssignableFrom( object.deref().getClass() );
    }

    protected boolean strictlyAllowsClass( Class<?> c ) {
        return getObjectType().isAssignableFrom( c );
    }

    protected List<Filter> createChildren() {
        List<Filter> result = new ArrayList<Filter>();
        Set<Class<?>> subclasses = getSubclasses( getObjectType() );
        Container filtered = new FilteredContainer( getContainer(), this, true );

        if ( subclasses.size() > 1 )
            for ( Class<?> c : subclasses ) {
                ClassFilter cf = new ClassFilter( c );
                cf.setShowingLeaves( isShowingLeaves() );
                cf.setContainer( filtered );
                result.add( cf );
            }
        else if ( getFiltersType() != null ) try {
            // Apply specialized filters
            AbstractFilters fs = (AbstractFilters) getFiltersType().newInstance();
            return fs.getFilters( filtered, isShowingLeaves() );

            } catch ( InstantiationException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        else if ( isShowingLeaves() ) {
            for( Ref ref : filtered ) {
                result.add( new RefFilter( ref ) );
            }
        }

        // Impersonate...
        if ( subclasses.size() == 1 ) {
            Class<?> c = subclasses.iterator().next();
            setExpandedText( expandedText( c ) );
            setCollapsedText( collapsedText( c ) );
        }

        return result;
    }

    public static String collapsedText( Class<?> clazz ) {
        return MessageFormat.format( "Any {0}",
            ColumnProvider.toDisplay( clazz.getSimpleName() ).toLowerCase() );
    }

    public static String expandedText( Class<?> clazz ) {
        String s = ColumnProvider.toDisplay( clazz.getSimpleName() );
        return MessageFormat.format( "{0}s...", s );
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
