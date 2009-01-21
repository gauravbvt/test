package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Deletable;

/**
 * A flow panel with a delete checkbox.
 */
public interface DeletableFlow extends Deletable {

    /**
     * @return the underlying flow
     */
    Flow getFlow();
}
