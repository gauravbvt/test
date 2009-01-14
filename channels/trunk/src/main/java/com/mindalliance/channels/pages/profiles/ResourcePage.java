package com.mindalliance.channels.pages.profiles;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * ...
 */
public class ResourcePage extends WebPage {

    /** The scenario parameter. */
    private static final String SCENARIO_PARM = "scenario";                               // NON-NLS

    /** The part parameter. */
    private static final String PART_PARM = "part";                                       // NON-NLS

    public ResourcePage( PageParameters parameters ) {
        this( findPart( parameters ) );
    }

    public ResourcePage( Part part ) {
        super( new CompoundPropertyModel<Part>( part ) );

        add( new Label( "title" ) );                                                      // NON-NLS
    }

    private static Part findPart( PageParameters parameters ) {
        final Dao dao = ( (Project) WebApplication.get() ).getDao();
        final Scenario defaultScenario = dao.getDefaultScenario();
        final long scenarioId = parameters.getLong( SCENARIO_PARM, defaultScenario.getId() );
        Scenario scenario;
        try {
            scenario = dao.findScenario( scenarioId );
        } catch ( NotFoundException ignored ) {
            scenario = defaultScenario;
        }

        final long partId = parameters.getAsLong( PART_PARM, -1L );
        Part part = (Part) scenario.getNode( partId );
        if ( part == null )
            part = scenario.getDefaultPart();

        return part;
    }


}
