package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
public class UserFilters extends AbstractFilters {

    public enum Type { Normal, Admins, Analysts, Managers }
    private Map<Type,Integer> count = new HashMap<Type,Integer>();

    private int get( Type type ) {
        Integer c = count.get( type );
        return c == null ? 0 : c ;
    }

    private void addTo( Type type ) {
        count.put( type, get( type ) + 1 );
    }

    public List<Filter> getFilters( Container container ) {

        final int size = container.size();
        Iterator i = container.iterator( 0, size );
        while ( i.hasNext() ) {
            Ref userRef = (Ref) i.next();
            User user = (User) userRef.deref();
            if ( user.getAdmin() )
                addTo( Type.Admins );
            if ( user.getManager() )
                addTo( Type.Managers );
            if ( user.getAnalyst() )
                addTo( Type.Analysts );
            else if ( !user.getAdmin() && !user.getManager() )
                addTo( Type.Normal );
        }

        List<Filter> result = new ArrayList<Filter>();
        if ( get( Type.Admins ) > 0 && get( Type.Admins ) < size )
            result.add( new AdminFilter() );
        if ( get( Type.Managers ) > 0 && get( Type.Managers ) < size )
            result.add( new ManagerFilter() );
        if ( get( Type.Analysts ) > 0 && get( Type.Analysts ) < size )
            result.add( new AnalystFilter() );
        if ( get( Type.Normal ) > 0 && get( Type.Normal ) < size )
            result.add( new NormalUserFilter() );
        return result;
    }

    static class AdminFilter extends Filter {

        public AdminFilter() {
            super( "administrators" );
        }

        protected List<Filter> createChildren() {
            return new ArrayList<Filter>();
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getAdmin();
        }

        protected boolean strictlyAllowsClass( Class<?> c ) {
            return c.equals( User.class );
        }
    }

    static class ManagerFilter extends Filter {

        public ManagerFilter() {
            super( "managers" );
        }

        protected List<Filter> createChildren() {
            return new ArrayList<Filter>();
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getManager();
        }

        protected boolean strictlyAllowsClass( Class<?> c ) {
            return c.equals( User.class );
        }
    }

    static class AnalystFilter extends Filter {

        public AnalystFilter() {
            super( "analysts" );
        }

        protected List<Filter> createChildren() {
            return new ArrayList<Filter>();
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getAnalyst();
        }

        protected boolean strictlyAllowsClass( Class<?> c ) {
            return c.equals( User.class );
        }
    }

    static class NormalUserFilter extends Filter {

        public NormalUserFilter() {
            super( "normal users" );
        }

        protected List<Filter> createChildren() {
            return new ArrayList<Filter>();
        }

        public boolean match( Ref object ) {
            if ( object.getType().equals( "User" ) ) {
                final User user = (User) object.deref();
                return !user.getAdmin() && !user.getManager() && !user.getAnalyst();
            }

            return false;
        }

        protected boolean strictlyAllowsClass( Class<?> c ) {
            return c.equals( User.class );
        }
    }

}
