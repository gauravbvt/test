package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 7:16:56 PM
 */
public class RefDataProvider implements IDataProvider<Ref> {

    private Serializable source;
    private String path;
    private static final long serialVersionUID = -3689491407501378349L;

    public RefDataProvider( Serializable obj, String path ) {
        source = obj;
        this.path = path;
    }

    @SuppressWarnings( { "unchecked" } )
    private List<Ref> allRefs() {
        return (List<Ref>) RefUtils.get( source, path );
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return allRefs().subList( first, first + count ).iterator();
    }

    public int size() {
        return allRefs().size();
    }

    public IModel<Ref> model( Ref object ) {
        Ref ref;
        if ( object instanceof Referenceable ) {
            ref = ( (Referenceable) object ).getReference();
        } else if ( object instanceof Ref ) {
            ref = (Ref) object;
        } else {
            throw new IllegalArgumentException(
                    "$object is neither a Ref or a Referenceable" );
        }
        return new RefModel( ref );
    }

    public Collection<RefMetaProperty> getColumns() {
        Collection<RefMetaProperty> set = new TreeSet<RefMetaProperty>();
        for ( Ref ref : allRefs() ) {
            Referenceable r = ref.deref();
            for ( RefMetaProperty mp : (List<RefMetaProperty>) r.metaProperties() )
                if ( mp.isScalar() )
                    set.add( mp );
        }
        return set;
    }

    public void detach() {
        // Do nothing - nothing to detach with Ref's
    }
}