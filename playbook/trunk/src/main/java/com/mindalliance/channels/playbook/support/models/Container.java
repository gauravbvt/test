package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public interface Container extends IDataProvider, IModel, Iterable<Ref>, Mappable {

    Ref get( int index );

    int indexOf( Ref ref );

    boolean contains( Ref ref );

    void add( Referenceable ref );

    void remove( Ref ref );

    void remove( Referenceable ref );

    void detach();

    List<Class<?>> getAllowedClasses();

    ContainerSummary getSummary();

    Iterator<Ref> iterator( int first, int count );

    Iterator<Ref> iterator();

    int size();

    IModel model( Object object );
}
