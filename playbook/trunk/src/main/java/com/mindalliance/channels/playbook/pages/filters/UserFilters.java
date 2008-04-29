package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    public void addFilters( Container container, List<Filter> result ) {
        final int size = container.size();
        for ( Ref userRef : container ) {
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

        if ( get( Type.Admins ) > 0 && get( Type.Admins ) < size )
            result.add( new AdminFilter() );
        if ( get( Type.Managers ) > 0 && get( Type.Managers ) < size )
            result.add( new ManagerFilter() );
        if ( get( Type.Analysts ) > 0 && get( Type.Analysts ) < size )
            result.add( new AnalystFilter() );
        if ( get( Type.Normal ) > 0 && get( Type.Normal ) < size )
            result.add( new NormalUserFilter() );
    }

    static abstract class UserFilter extends Filter {

        public UserFilter( String text ) {
            super( text, text + "..." );
        }

        protected List<Filter> createChildren() {
            if ( isShowingLeaves() ) {
                List<Filter> results = new ArrayList<Filter>();
                for ( Ref ref : getContainer() )
                    results.add( new RefFilter( ref ) );
                return results;

            } else
                return Collections.emptyList();
        }

        protected boolean strictlyAllowsClass( Class<?> c ) {
            return c.equals( User.class );
        }
    }

    static class AdminFilter extends UserFilter {

        public AdminFilter() {
            super( "administrators" );
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getAdmin();
        }
    }

    static class ManagerFilter extends UserFilter {

        public ManagerFilter() {
            super( "managers" );
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getManager();
        }
    }

    static class AnalystFilter extends UserFilter {

        public AnalystFilter() {
            super( "analysts" );
        }

        public boolean match( Ref object ) {
            return object.getType().equals( "User" ) && ( (User) object.deref() ).getAnalyst();
        }
    }

    static class NormalUserFilter extends UserFilter {

        public NormalUserFilter() {
            super( "normal users" );
        }

        public boolean match( Ref object ) {
            if ( object.getType().equals( "User" ) ) {
                final User user = (User) object.deref();
                return !user.getAdmin() && !user.getManager() && !user.getAnalyst();
            }

            return false;
        }
    }
}
