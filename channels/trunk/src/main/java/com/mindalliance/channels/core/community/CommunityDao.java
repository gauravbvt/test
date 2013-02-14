package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.Importer;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Plan community dao.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/5/13
 * Time: 9:33 AM
 */
public class CommunityDao extends AbstractModelObjectDao {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityDao.class );


    private CommunityDefinition communityDefinition;
    private PlanCommunity planCommunity;

    public CommunityDao( CommunityDefinition communityDefinition ) {
        this.communityDefinition = communityDefinition;
    }

    @Override
    protected void addSpecific( ModelObject object, Long id ) {
        // Do nothing
    }

    @Override
    protected <T extends ModelObject> void setContextKindOf( T object ) {
        object.setInCommunity();
    }

    @Override
    public ModelObjectContext getModelObjectContext() {
        return getPlanCommunity();
    }

    @Override
    public void defineImmutableEntities() {
        // WARNING: Don't change the order and only add at the end!
        getIdGenerator().setImmutableMode();
        if ( Requirement.UNKNOWN == null ) {
            Requirement.UNKNOWN = findOrCreateModelObject( Requirement.class, Requirement.UnknownName, null );
        }
        getIdGenerator().setMutableMode();
    }

    @Override
    public boolean isLoaded() {
        return planCommunity != null;
    }

    @Override
    protected boolean isJournaled() {
        return true;
    }

    @Override
    protected File getJournalFile() throws IOException {
        return communityDefinition.getJournalFile();
    }

    @Override
    protected void afterLoad() {
        add(  planCommunity, planCommunity.getId() );
    }

    @Override
    protected long getRecordedLastAssignedId() throws IOException {
        return communityDefinition.getLastId();
    }

    @Override
    protected File getDataFile() throws IOException {
        File dataFile = communityDefinition.getDataFile();
        if ( dataFile.createNewFile() )
            LOG.info( "Created {}", dataFile );
        return dataFile;
    }

    @Override
    protected void beforeSnapshot() throws IOException {
        communityDefinition.setLastId( getIdGenerator().getIdCounter( planCommunity.getUri() ) );
    }

    @Override
    protected void afterSnapshot() throws IOException {
        communityDefinition.getJournalFile().delete();
    }

    @Override
    protected void afterSaveJournal() throws IOException {
        // Do nothing
    }

    @Override
    protected void beforeSaveJournal() throws IOException {
        communityDefinition.setLastId( getIdGenerator().getIdCounter( getModelObjectContext().getUri() ) );
    }

    public void validate() {
        // do nothing
    }

    @Override
    protected void importModelObjectContext( Importer importer, FileInputStream in ) throws IOException {
        importer.importPlanCommunity( in );
    }

    public PlanCommunity resetCommunity() {
        planCommunity = communityDefinition.createPlanCommunity( );
        return planCommunity;
    }

    public PlanCommunity getPlanCommunity() {
        return planCommunity;
    }

    public void assignFirstIdTo( PlanCommunity planCommunity ) {
        long startId = getPlanDao().getLastAssignedId() + 1;
        planCommunity.setId( getIdGenerator().assignId( startId, planCommunity.getUri() ) );
    }

    private PlanDao getPlanDao() {
        return (PlanDao) getSubDao();
    }
}
