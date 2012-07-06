package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.Attachment;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for an attachment documenting a model object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/11
 * Time: 9:30 AM
 */
@XmlType( propOrder = {"type", "name", "url"} )
public class DocumentData  implements Serializable {

    private String serverUrl;
    private Attachment attachment;

    public DocumentData() {
        // required
    }

    public DocumentData( String serverUrl, Attachment attachment ) {
        this.serverUrl = serverUrl;
        this.attachment = attachment;
    }

    @XmlElement
    public String getType() {
        return attachment.getType().getLabel();
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( attachment.getLabel() );
    }

    @XmlElement
    public String getUrl() {
        return StringEscapeUtils.escapeXml( attachment.isUploaded()
                ? correctServerUrl() + attachment.getUrl()
                : attachment.getUrl() );
    }

    private String correctServerUrl() {
        return serverUrl.endsWith( "/") ? serverUrl : (serverUrl + "/" );
    }
}
