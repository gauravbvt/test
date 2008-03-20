package com.mindalliance.channels.playbook.ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 8:43:13 AM
 */
public interface Referenceable  extends java.io.Serializable {
    Reference getReference();
    Referenceable copy();
}
