/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.db.services.users.UserRecordService;

/**
 * A query service targeted to a single plan.
 */
public class ModelService extends DefaultQueryService {

    private CollaborationModel collaborationModel;

    private String serverUrl;

    public ModelService() {
    }

    public ModelService( ModelManager modelManager, SemanticMatcher semanticMatcher, UserRecordService userDao,
                         AttachmentManager attachmentManager, SurveysDAO surveysDao,
                         CollaborationModel collaborationModel ) {

        this( modelManager,
                semanticMatcher,
                userDao,
                attachmentManager,
                surveysDao );
        this.collaborationModel = collaborationModel;
    }

    public ModelService( ModelManager modelManager, SemanticMatcher semanticMatcher, UserRecordService userDao,
                         AttachmentManager attachmentManager, SurveysDAO surveysDao ) {

        super( modelManager,
                attachmentManager,
                semanticMatcher,
                userDao,
                surveysDao );
    }

    @Override
    public CollaborationModel getCollaborationModel() {
        return collaborationModel;
    }

    public void setCollaborationModel( CollaborationModel collaborationModel ) {
        this.collaborationModel = collaborationModel;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }


}
