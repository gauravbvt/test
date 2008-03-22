package com.mindalliance.channels.playbook.support;

import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.Collection
import com.mindalliance.channels.playbook.ref.Ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 11:10:15 AM
 */
public interface Memorable {

    void storeAll(Collection<Referenceable> referenceables)
    Ref store(Referenceable referenceable)
    Referenceable retrieve(Ref ref)
    void clear(Ref ref)
    void clearAll()
    Ref getRoot()
}