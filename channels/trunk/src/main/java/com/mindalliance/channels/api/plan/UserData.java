package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a user.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:27 PM
 */
@XmlType( propOrder = {"username", "fullName", "email"} )
public class UserData {

    private ChannelsUser user;

    public UserData() {
        // required
    }

    public UserData( ChannelsUser user ) {
        this.user = user;
    }

    @XmlElement
    public String getUsername() {
        return StringEscapeUtils.escapeXml( user.getUsername() );
    }

    @XmlElement
    public String getFullName() {
        return StringEscapeUtils.escapeXml( user.getFullName() );
    }

    @XmlElement
    public String getEmail() {
        return user.getEmail();
    }
}
