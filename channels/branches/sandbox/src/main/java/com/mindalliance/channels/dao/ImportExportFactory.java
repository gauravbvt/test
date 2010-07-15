package com.mindalliance.channels.dao;

/**
 * A generator of importers and exporters.
 */
public interface ImportExportFactory {

    /**
     * Create an import context.
     * @param planDao the plan dao
     * @return an importer
     */
    Importer createImporter( PlanDao planDao );

    /**
     * Create an export context.
     * @param planDao the plan dao
     * @return an exporter
     */
    Exporter createExporter( PlanDao planDao );

}
