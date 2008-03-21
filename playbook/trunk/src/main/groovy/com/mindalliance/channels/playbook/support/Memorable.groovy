package com.mindalliance.channels.playbook.support;

import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.Reference;

import java.util.Collection;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 11:10:15 AM
 */
public interface Memorable {

    void storeAll(Collection<Referenceable> referenceables)
    Reference store(Referenceable referenceable)
    Referenceable retrieve(Reference ref)
    void clear(Reference ref)
    void clearAll()
    Reference getRoot()
}