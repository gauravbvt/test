package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a plan summary.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 1:36 PM
 */
@XmlRootElement( name = "modelSummary", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"modelIdentifier", "dateVersioned", "description", "developers", "documentation"} )

public class ModelSummaryData implements Serializable {

    private List<UserData> developers;
    private DocumentationData documentation;
    private ModelIdentifierData modelIdentifierData;
    private CollaborationModel collaborationModel;

    public ModelSummaryData() {
        // required
    }

    public ModelSummaryData( String serverUrl, CommunityService communityService ) {
        init(  serverUrl, communityService );
    }

    private void init(
            String serverUrl,
            CommunityService communityService ) {
        collaborationModel = communityService.getPlan();
        initDevelopers( communityService );
        documentation = new DocumentationData( serverUrl, getCollaborationModel() );
        modelIdentifierData = new ModelIdentifierData( communityService );
    }

    private void initDevelopers( CommunityService communityService ) {
        developers = new ArrayList<UserData>();
        for ( ChannelsUser planner : communityService.getUserRecordService().getDevelopers( getCollaborationModel().getUri() ) ) {
            developers.add( new UserData( planner, communityService ) );
        }

    }

    @XmlElement
    public ModelIdentifierData getModelIdentifier() {
        return modelIdentifierData;
    }


    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( getCollaborationModel().getWhenVersioned() );
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( getCollaborationModel().getDescription() );
    }

    @XmlElement( name = "planner" )
    public List<UserData> getDevelopers() {
        return developers;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    private CollaborationModel getCollaborationModel() {
        return collaborationModel;
    }
}
