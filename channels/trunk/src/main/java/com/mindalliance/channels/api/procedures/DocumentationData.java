package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for documentation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/11
 * Time: 9:21 AM
 */
public class DocumentationData  implements Serializable {

    private Attachable attachable;

    public DocumentationData() {
        // required
    }

    public DocumentationData( Attachable attachable ) {
        this.attachable = attachable;
    }

    @XmlElement
    public List<DocumentData> getDocuments() {
        List<DocumentData> documents = new ArrayList<DocumentData>(  );
        for ( Attachment attachment : attachable.getAttachments() ) {
            documents.add( new DocumentData( attachment ) );
        }
        return documents;
    }
}
