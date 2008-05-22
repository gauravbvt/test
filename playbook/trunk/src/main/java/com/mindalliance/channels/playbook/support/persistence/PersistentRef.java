package com.mindalliance.channels.playbook.support.persistence;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.ref.impl.ComputedRef;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2008
 * Time: 8:14:22 PM
 */
public class PersistentRef {

    String id;
    String db;
    boolean computed = false;

    public PersistentRef() {}

    static public PersistentRef fromRef (Ref ref) {
        PersistentRef pRef = new PersistentRef();
        pRef.id = ref.getId();
        pRef.db = ref.getDb();
        pRef.computed = ref.isComputed();
        return pRef;
    }

    public String getId() {
        return id;
}

    public void setId(String id) {
        this.id = id;
    }

    public Ref toRef() {
        Ref ref;
        if (computed) {
            ref = new ComputedRef();
        }
        else {
            ref = new RefImpl();
        }
        ref.setId(id);
        ref.setDb(db);
        return ref;
    }
}
