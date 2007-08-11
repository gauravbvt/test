// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import com.mindalliance.channels.models.Timed;
import com.mindalliance.channels.profiles.InferableObject;
import com.mindalliance.channels.support.GUID;

/**
 * An effect on something that happens that's traceable to an issue,
 * possibly caused indirectly by another impact of that issue.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Impact extends InferableObject {

    /**
     * Effect of an impact on the impacted object.
     */
    enum Effect {
        /** The impact has an enabling effect. */
        ENABLED,

        /** The impact has a disabling effect. */
        DISABLED
    };

    private Timed impacted;
    private Effect effect;
    private Impact causedBy;

    /**
     * Default constructor.
     */
    public Impact() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Impact( GUID guid ) {
        super( guid );
    }

    /**
     * Return the cause.
     */
    public Impact getCausedBy() {
        return causedBy;
    }

    /**
     * Set the cause.
     * @param causedBy the causedBy to set
     */
    public void setCausedBy( Impact causedBy ) {
        this.causedBy = causedBy;
    }

    /**
     * Return the effect.
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Set the effect.
     * @param effect the effect to set
     */
    public void setEffect( Effect effect ) {
        this.effect = effect;
    }

    /**
     * Return the impacted.
     */
    public Timed getImpacted() {
        return impacted;
    }

    /**
     * Set the impacted.
     * @param impacted the impacted to set
     */
    public void setImpacted( Timed impacted ) {
        this.impacted = impacted;
    }
}
