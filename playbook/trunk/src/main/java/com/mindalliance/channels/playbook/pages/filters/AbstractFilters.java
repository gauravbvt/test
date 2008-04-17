package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.support.models.Container;

import java.util.List;

/**
 * Specialized set of filters attached to object of a certain
 * class.
 * <p>When subclassing, name the class "<Class>Filters"...</p>
 */
public abstract class AbstractFilters {

    public AbstractFilters() {
    }

    /**
     * Get a list of filters applicable to the given object.
     * @param container the objects
     */
    abstract public List<Filter> getFilters( Container container );

}
