// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.mindalliance.channels.data.support.GUID;

/**
 * A match between a need to know and a know denoting a requirement
 * for communication. A SharingNeed is an occurrence; it exists for a
 * period of time.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class SharingNeed extends Occurrence
    implements Regulatable, Agreeable {

    private NeedsToKnow needToKnow;
    private Known known;

    /**
     * Default constructor.
     */
    public SharingNeed() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public SharingNeed( GUID guid ) {
        super( guid );
    }

    /**
     * Return the known.
     */
    public Known getKnown() {
        return known;
    }

    /**
     * Set the known.
     * @param known the known
     */
    public void setKnown( Known known ) {
        this.known = known;
    }

    /**
     * Return the needToKnow.
     */
    public NeedsToKnow getNeedToKnow() {
        return needToKnow;
    }

    /**
     * Set the needToKnow.
     * @param needToKnow the needToKnow
     */
    public void setNeedToKnow( NeedsToKnow needToKnow ) {
        this.needToKnow = needToKnow;
    }

}
