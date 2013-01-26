package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Channel;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a user.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:27 PM
 */
@XmlType( propOrder = {"username", "fullName", "email", "personalChannels"} )
public class UserData  implements Serializable {

    private ChannelsUser user;
    private List<ChannelData> personalChannels;

    public UserData() {
        // required
    }

    public UserData( ChannelsUser user, PlanCommunity planCommunity ) {
        this.user = user;
        initPersonalChannels( planCommunity );
    }

    private void initPersonalChannels( PlanCommunity planCommunity ) {
        ChannelsUserInfo userInfo = user.getUserInfo();
        personalChannels = new ArrayList<ChannelData>();
        if ( userInfo != null ) {
            for ( Channel channel : planCommunity.getPlanService().getUserContactInfoService().findChannels( userInfo, planCommunity ) ) {
                personalChannels.add( new ChannelData(
                        channel.getMedium().getId(),
                        channel.getAddress(),
                        planCommunity ) );
            }
        }

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

    @XmlElement( name = "personalChannel" )
    public List<ChannelData> getPersonalChannels() {
        return personalChannels;
    }

}
