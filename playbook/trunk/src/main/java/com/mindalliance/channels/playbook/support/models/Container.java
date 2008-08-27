package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.util.List;

/**
 * ...
 */
public interface Container extends IDataProvider<Ref>, Iterable<Ref>, Mappable {

    Ref get( int index );

    int indexOf( Ref ref );

    boolean contains( Ref ref );

    void add( Referenceable item );

    void remove( Ref ref );

    void remove( Referenceable ref );

    List<Class<? extends Referenceable>> getAllowedClasses();

    ContainerSummary getSummary();
}
