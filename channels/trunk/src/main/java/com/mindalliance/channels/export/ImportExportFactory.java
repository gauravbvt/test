package com.mindalliance.channels.export;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Plan;

/**
 * A generator of importers and exporters.
 */
public interface ImportExportFactory {

    /**
     * Create an import context.
     * @param service the query service
     * @param plan the current plan
     * @return an importer
     */
    Importer createImporter( QueryService service, Plan plan );

    /**
     * Create an export context.
     * @param service the query service
     * @param plan the current plan
     * @return an exporter
     */
    Exporter createExporter( QueryService service, Plan plan );

}
