package com.mindalliance.channels.playbook.ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 8:42:31 AM
 */
public interface Reference {
    String getId();
    String getDb();
    Referenceable getReferenced(Store store);
    Reference getReference(); // returns self
}
