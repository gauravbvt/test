package com.mindalliance.channels.api;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;

/**
 * Web Service data element for a model object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 10:49 AM
 */
abstract public class ModelObjectData  implements Serializable {

    private ModelObject modelObject;
    private Plan plan;
    private PlanCommunity planCommunity;
    private DocumentationData documentation;
    private String serverUrl;

    public ModelObjectData(  ) {
        // required
    }

    public ModelObjectData( String serverUrl, ModelObject modelObject, CommunityService communityService ) {
        this.serverUrl = serverUrl;
        assert modelObject != null;
        this.modelObject = modelObject;
        plan = communityService.getPlan();
        planCommunity = communityService.getPlanCommunity();
        documentation = new DocumentationData(  serverUrl, modelObject );
    }

    public long getId() {
        return modelObject.getId();
    }

    public String getName() {
        return StringEscapeUtils.escapeXml( modelObject.getName() );
    }

    public String getDescription() {
        return StringEscapeUtils.escapeXml( modelObject.getDescription() );
    }

    public DocumentationData getDocumentation() {
        return documentation;
    }

    protected ModelObject getModelObject() {
        return modelObject;
    }

    protected Plan getPlan() {
        return plan;
    }

    protected PlanCommunity getPlanCommunity() {
        return planCommunity;
    }

    protected String getServerUrl() {
        return serverUrl;
    }


}
