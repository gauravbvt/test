package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential. User: jf Date: Mar 23,
 * 2008 Time: 7:19:31 PM
 */
public class RefModel implements IModel<Ref> {

    private Ref ref;
    private static final long serialVersionUID = -3304071925434086081L;

    public RefModel( Ref obj ) {
        setObject( obj );
    }

    public RefModel( Referenceable obj ) {
        setObject( obj );
    }

    // Returns a Ref
    public Ref getObject() {
        return ref;
    }

    public final void setObject( Ref object ) {
        ref = object;
    }

    public final void setObject( Referenceable object ) {
        ref = object.getReference();
    }

    public void detach() {
        // Nothing to do
    }
}
