package com.mindalliance.channels.pages;

import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.pages.components.ResourceSpecsPanel;
import com.mindalliance.channels.pages.components.ScenariosPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 10:12:52 AM
 */
public class IndexPage extends WebPage {
    /**
     * Maximum number of resource specs to show in table before paging.
     */
    private static final int PAGE_SIZE = 20;

    /**
     * Resource Specifications shown
     */
    private List<ResourceSpec> resourceSpecs = new ArrayList<ResourceSpec>();

    public IndexPage( PageParameters parameters ) {
        super( parameters );

        final DataQueryObject dqo = ( (Channels) getApplication() ).getDqo();
        resourceSpecs = dqo.findAllResourceSpecs();
        init( dqo );
    }

    @SuppressWarnings( {"unchecked"} )
    private void init( DataQueryObject dqo ) {
        add( new Label( "title", "Index" ) );

        List<Scenario> scenarios = dqo.list( Scenario.class );

        add( new ScenariosPanel( "all-scenarios",
                new Model<ArrayList<Scenario>>( (ArrayList) scenarios ), null ) );
        Form form = new Form( "resourceSpecs-form" ) {
            protected void onSubmit() {
                setResponsePage( new RedirectPage( "index.html" ) );
            }
        };
        form.add( new ResourceSpecsPanel( "all-resourceSpecs",
                new Model<ArrayList<ResourceSpec>>(
                        (ArrayList) resourceSpecs ),
                PAGE_SIZE,
                null) );
        add( form );
    }
}
