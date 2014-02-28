/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.CollaborationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator and cache for plan services.
 */
public class ModelServiceFactoryImpl implements ModelServiceFactory, ApplicationContextAware {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelServiceFactoryImpl.class );

    private final Map<CollaborationModel,ModelService> services = new HashMap<CollaborationModel, ModelService>();

    private ApplicationContext applicationContext;
    private ModelManager modelManager;

    /**
     * Notify all services of impending doom...
     */
    public synchronized void destroy() {
        for ( ModelService modelService : services.values() )
            modelService.onDestroy();
        services.clear();
    }

    @Override
    public synchronized void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized ModelService getService( CollaborationModel collaborationModel ) {
        ModelService modelService = services.get( collaborationModel );
        if ( modelService != null )
            return modelService;

        ModelService newModelService = (ModelService) applicationContext.getBean( "modelService" );  // a prototype, not a singleton
        newModelService.setCollaborationModel( collaborationModel );
        services.put( collaborationModel, newModelService );
        return newModelService;
    }

    @Override
    public ModelService getService( String modelUri, int modelVersion ) {
        CollaborationModel collaborationModel = modelManager.getModel( modelUri, modelVersion );
        if ( collaborationModel == null ) {
            LOG.error( "Model not found " + modelUri + " v." + modelVersion );
            throw new RuntimeException( "Model not found " + modelUri + " v." + modelVersion );
        }
        return getService( collaborationModel );
    }

    public void setModelManager( ModelManager modelManager ) {
        this.modelManager = modelManager;
    }
}
