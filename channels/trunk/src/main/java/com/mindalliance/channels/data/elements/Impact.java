/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.data.Timed;
import com.mindalliance.channels.util.GUID;

/**
 * An effect on something that happens that's traceable to an issue,
 * possibly caused indirectly by another impact of that issue.
 * 
 * @author jf
 */
public class Impact extends AbstractElement {

    enum Effect {
        ENABLED, DISABLED
    };

    private Timed impacted;
    private Effect effect;
    private Impact causedBy;

    public Impact() {
    }

    public Impact( GUID guid ) {
        super( guid );
    }

    /**
     * @return the causedBy
     */
    public Impact getCausedBy() {
        return causedBy;
    }

    /**
     * @param causedBy the causedBy to set
     */
    public void setCausedBy( Impact causedBy ) {
        this.causedBy = causedBy;
    }

    /**
     * @return the effect
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * @param effect the effect to set
     */
    public void setEffect( Effect effect ) {
        this.effect = effect;
    }

    /**
     * @return the impacted
     */
    public Timed getImpacted() {
        return impacted;
    }

    /**
     * @param impacted the impacted to set
     */
    public void setImpacted( Timed impacted ) {
        this.impacted = impacted;
    }

}
