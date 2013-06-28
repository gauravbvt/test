package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.db.data.users.UserRecord;
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

    public UserData( ChannelsUser user, CommunityService communityService ) {
        this.user = user;
        initPersonalChannels( communityService );
    }

    private void initPersonalChannels( CommunityService communityService ) {
        UserRecord userInfo = user.getUserRecord();
        personalChannels = new ArrayList<ChannelData>();
        if ( userInfo != null ) {
            for ( Channel channel : communityService.getPlanService().getUserInfoService().findChannels( userInfo, communityService ) ) {
                personalChannels.add( new ChannelData(
                        channel.getMedium().getId(),
                        channel.getAddress(),
                        communityService ) );
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
