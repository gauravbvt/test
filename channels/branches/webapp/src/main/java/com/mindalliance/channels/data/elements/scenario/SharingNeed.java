/*
 * Created on Apr 27, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Agreeable;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.elements.assertions.AgreedTo;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.Regulated;
import com.mindalliance.channels.util.GUID;

/**
 * A match between a need to know and a know denoting a requirement
 * for communication. A SharingNeed is an occurrence; it exists for a
 * period of time.
 * 
 * @author jf
 */
public class SharingNeed extends AbstractOccurrence implements Regulatable,
        Agreeable {

    private NeedsToKnow needToKnow;
    private Known known;

    public SharingNeed() {
        super();
    }

    public SharingNeed( GUID guid ) {
        super( guid );
    }

    public List<Regulated> getRegulatedAssertions() {
        List<Regulated> regulatedAssertions = new ArrayList<Regulated>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof Regulated )
                regulatedAssertions.add( (Regulated) assertion );
        }
        return regulatedAssertions;
    }

    public List<AgreedTo> getAgreedToAssertions() {
        List<AgreedTo> agreedToAssertions = new ArrayList<AgreedTo>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof AgreedTo )
                agreedToAssertions.add( (AgreedTo) assertion );
        }
        return agreedToAssertions;
    }

    /**
     * @return the known
     */
    public Known getKnown() {
        return known;
    }

    /**
     * @param known the known to set
     */
    public void setKnown( Known known ) {
        this.known = known;
    }

    /**
     * @return the needToKnow
     */
    public NeedsToKnow getNeedToKnow() {
        return needToKnow;
    }

    /**
     * @param needToKnow the needToKnow to set
     */
    public void setNeedToKnow( NeedsToKnow needToKnow ) {
        this.needToKnow = needToKnow;
    }

}
