/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.elements.reference;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.util.GUID;

/**
 * ReferenceElement data
 * 
 * @author jf
 */
public class ReferenceElement extends AbstractElement {

    public ReferenceElement() {
        super();
    }

    public ReferenceElement( GUID guid ) {
        super( guid );
    }

}
