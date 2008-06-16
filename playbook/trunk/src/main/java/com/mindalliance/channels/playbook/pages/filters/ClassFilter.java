package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
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
        setObjectType( objectType );
    }

    public ClassFilter( Class<?> objectType ) {
        this( collapsedText( objectType ), expandedText( objectType ), objectType );
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();
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
        try {
            filtersType = Class.forName( Package + objectType.getSimpleName() + "Filters" );
        } catch ( ClassNotFoundException e ) {
            filtersType = null;
        }
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

//    public boolean filter( Ref object ) {
//        return super.filter( object )
//            || ( Event.class.equals( getObjectType() ) && !isSelected()
//                    && Event.class.equals( object.deref().getClass() ) );
//    }

    protected boolean strictlyAllowsClass( Class<?> c ) {
        return getObjectType().isAssignableFrom( c );
    }

    protected List<Filter> createChildren() {
        List<Filter> result = new ArrayList<Filter>();
        Set<Class<?>> subclasses = getSubclasses();
        Container filtered = new FilteredContainer( getContainer(), this, true );

        // Impersonate...
//        if ( subclasses.size() == 1 ) {
//            Class<?> c = subclasses.iterator().next();
////            if ( card( getObjectType() ) == card( c ) ) {
//                setObjectType( c );
//                setExpandedText( expandedText( c ) );
//                setCollapsedText( collapsedText( c ) );
////            }
//        }

        if ( subclasses.size() >= 1 )
            for ( Class<?> c : subclasses ) {
                ClassFilter cf = new ClassFilter( c );
                cf.setShowingLeaves( isShowingLeaves() );
                cf.setContainer( filtered );
                result.add( cf );
            }
        else if ( getFiltersType() != null ) try {
            // Apply specialized filters
            AbstractFilters fs = (AbstractFilters) getFiltersType().newInstance();
            result.addAll( fs.getFilters( filtered, isShowingLeaves() ) );

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

        return result;
    }

    private boolean isConcrete( Class<?> sup ) {
        return getContainer().getSummary().instancesOf( sup ) != 0 ;
    }

    private int card( Class<?> c, Map<Class<?>, Set<Class<?>>> map ) {
        int count = getContainer().getSummary().instancesOf( c );
        Set<Class<?>> classSet = map.get( c );
        if ( classSet != null )
            for ( Class<?> sc : classSet )
                    count +=  card( sc, map );

        return count;
    }

    public static String collapsedText( Class<?> clazz ) {
        return MessageFormat.format( "Any {0}",
            ContainerSummary.toDisplay( clazz.getSimpleName() ).toLowerCase() );
    }

    public static String expandedText( Class<?> clazz ) {
        String s = ContainerSummary.toDisplay( clazz.getSimpleName() );
        s = s.substring( 0,1 ) + s.substring( 1 ).toLowerCase();
        return MessageFormat.format( "{0}s...", s );
    }

    private Set<Class<?>> getSubclasses() {
        TreeSet<Class<?>> result = new TreeSet<Class<?>>( new Comparator<Class<?>>() {
            public int compare( Class<?> o1, Class<?> o2 ) {
                return collapsedText( o1 ).compareTo( collapsedText( o2 ) );
            }
        } );

        Map<Class<?>, Set<Class<?>>> map = getRawSubclasses();
        Set<Class<?>> classes = map.get( getObjectType() );
        if ( classes != null )
            for ( Class<?> c : classes ) {
                simplifySet( result, c, map, true );
        }

        return result;
    }

    private Map<Class<?>, Set<Class<?>>> getRawSubclasses() {
        Map<Class<?>,Set<Class<?>>> result = new HashMap<Class<?>,Set<Class<?>>>();
        ContainerSummary summary = getContainer().getSummary();
        Set<Class<?>> classes = summary.getClasses();

        for ( Class<?> c : classes ) {
            Class<?> sc = c;
            while( sc != getObjectType() && getObjectType().isAssignableFrom( sc ) ) {
                Class<?> ssc = sc.getSuperclass();
                Set<Class<?>> value = result.get( ssc );
                if ( value == null )
                    value = new HashSet<Class<?>>();
                value.add( sc );
                result.put( ssc, value );
                sc = ssc ;
            }
        }
        return result;
    }

    private Set<Class<?>> simplifySet(
            Set<Class<?>> classes, Class<?> objectClass,
            Map<Class<?>, Set<Class<?>>> map,
            boolean dig ) {

        final Set<Class<?>> subs = map.get( objectClass );
        if ( subs == null )
            classes.add( objectClass );

        else {
            Class<?> first = subs.iterator().next();
            if ( subs.size() == 1 ) {
                if ( isConcrete( objectClass ) ) {
                    // Superclass is not abstract and has instances
                    classes.add( objectClass );
                } else {
                    simplifySet( classes, first, map, dig );
                }
            } else if ( !dig )
                classes.add( objectClass );

            else for ( Class<?> c : subs ) {
                Set<Class<?>> ssubs = map.get( c );
                if ( ssubs == null || ssubs.size() > 1 )
                    classes.add( c );
                else
                    simplifySet( classes, c, map, false );
            }
        }

        return classes;
    }
}
