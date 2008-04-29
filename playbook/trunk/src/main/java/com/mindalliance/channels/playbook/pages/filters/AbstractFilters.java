package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.List;
import java.util.ArrayList;

/**
 * Specialized set of filters attached to object of a certain
 * class.
 * <p>When subclassing, name the class "<Class>Filters"...</p>
 */
public abstract class AbstractFilters {

    public AbstractFilters() {
    }

    /**
     * Add applicable filters for the given objects to the given list.
     * @param container the objects
     * @param results list to add to
     */
    abstract public void addFilters( Container container, List<Filter> results );

    public List<Filter> getFilters( Container container, boolean showLeaves ) {
       List<Filter> result = new ArrayList<Filter>();

       addFilters( container, result );

       if ( showLeaves ) {
           if ( result.size() == 0 )
               for ( Ref ref : container  )
                   result.add( new RefFilter( ref ) );
           else
               for ( Filter f : result )
                   f.setShowingLeaves( true );
       }

       return result;
   }
}