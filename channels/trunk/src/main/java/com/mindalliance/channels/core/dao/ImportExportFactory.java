/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.dao;

/**
 * A generator of importers and exporters.
 */
public interface ImportExportFactory {

    /**
     * Create an import context.
     *
     * @param userName the user doing the import
     * @param planDao the plan dao
     * @return an importer
     */
    Importer createImporter( String userName, PlanDao planDao );

    /**
     * Create an export context.
     *
     * @param userName the user doing the export
     * @param planDao the plan dao
     * @return an exporter
     */
    Exporter createExporter( String userName, PlanDao planDao );

}
