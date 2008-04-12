package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Iterator;

/**
 * ...
 */
public class Tab implements Serializable, IDataProvider {

    private boolean shared;
    private String name;
    private Filter filter;

    public Tab() {
    }

    public void detach() {
    }

    public Iterator iterator( int first, int count ) {
        return null;
    }

    public int size() {
        return 0;
    }

    public IModel model( Object object ) {
        return null;
    }
}
