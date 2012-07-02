package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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

    @SuppressWarnings( "unchecked" )
    public List<DocumentData> reportableDocuments() {
        return (List<DocumentData>)CollectionUtils.select(
             getDocuments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        DocumentData docData = (DocumentData)object;
                        String type = docData.getType();
                        return type.equals( Attachment.Type.Reference.getLabel() )
                                || type.equals( Attachment.Type.Policy.getLabel() )
                                || type.equals( Attachment.Type.PolicyCant.getLabel() )
                                || type.equals( Attachment.Type.PolicyMust.getLabel() )
                                || type.equals( Attachment.Type.MOU.getLabel() )
                                || type.equals( Attachment.Type.Image.getLabel() );
                    }
                }
        );
    }

    public boolean hasReportableDocuments() {
        return !reportableDocuments().isEmpty();
    }
}
