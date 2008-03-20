package com.mindalliance.channels.playbook.ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 8:43:29 AM
 */
public interface Store {
    Referenceable retrieve(Reference reference);
    Reference persist(Referenceable referenceable);
    String getDefaultDb();
}
