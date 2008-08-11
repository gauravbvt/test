package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** ... */
public class UserFilters extends AbstractFilters {

    public UserFilters() {
    }

    public enum Type {

        Normal, Admins, Analysts, Managers
    }

    private Map<Type, Integer> count = new EnumMap<Type, Integer>( Type.class );

    @SuppressWarnings( { "TypeMayBeWeakened" } )
    private int get( Type type ) {
        Integer i = count.get( type );
        return i == null ? 0 : i;
    }

    private void addTo( Type type ) {
        count.put( type, get( type ) + 1 );
    }

    @Override
    public void addFilters( Container container, List<Filter> results ) {
        int size = container.size();
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
            results.add( new AdminFilter() );
        if ( get( Type.Managers ) > 0 && get( Type.Managers ) < size )
            results.add( new ManagerFilter() );
        if ( get( Type.Analysts ) > 0 && get( Type.Analysts ) < size )
            results.add( new AnalystFilter() );
        if ( get( Type.Normal ) > 0 && get( Type.Normal ) < size )
            results.add( new NormalUserFilter() );
    }

    abstract static class UserFilter extends Filter {

        private static final long serialVersionUID = 8019612134908670407L;

        protected UserFilter( String text ) {
            super( text, text + "..." );
            setInclusion( true );
        }

        @Override
        protected List<Filter> createChildren( boolean selectionState ) {
            if ( isShowingLeaves() ) {
                List<Filter> results = new ArrayList<Filter>();
                for ( Ref ref : getContainer() ) {
                    RefFilter f = new RefFilter( ref );
                    f.setSelected( selectionState );
                    results.add( f );
                }
                return results;
            } else
                return Collections.emptyList();
        }

        @Override
        protected boolean allowsClassLocally( Class<?> c ) {
            return c.equals( User.class );
        }
    }

    static class AdminFilter extends UserFilter {

        private static final long serialVersionUID = 1312226051936967710L;

        AdminFilter() {
            super( "Administrators" );
        }

        @Override
        public boolean isMatching( Ref object ) {
            return "User".equals( object.getType() ) && ( (User) object
                    .deref() ).getAdmin();
        }
    }

    static class ManagerFilter extends UserFilter {

        private static final long serialVersionUID = 8940928416718004856L;

        ManagerFilter() {
            super( "Managers" );
        }

        @Override
        public boolean isMatching( Ref object ) {
            return "User".equals( object.getType() ) && ( (User) object
                    .deref() ).getManager();
        }
    }

    static class AnalystFilter extends UserFilter {

        private static final long serialVersionUID = -3759575510396620670L;

        AnalystFilter() {
            super( "Analysts" );
        }

        @Override
        public boolean isMatching( Ref object ) {
            return "User".equals( object.getType() ) && ( (User) object
                    .deref() ).getAnalyst();
        }
    }

    static class NormalUserFilter extends UserFilter {

        private static final long serialVersionUID = 6354327699147333774L;

        NormalUserFilter() {
            super( "Normal users" );
        }

        @Override
        public boolean isMatching( Ref object ) {
            if ( "User".equals( object.getType() ) ) {
                User user = (User) object.deref();
                return !user.getAdmin() && !user.getManager() && !user
                        .getAnalyst();
            }

            return false;
        }
    }
}
