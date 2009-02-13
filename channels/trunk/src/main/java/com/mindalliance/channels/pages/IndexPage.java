package com.mindalliance.channels.pages;

import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.components.ResourceSpecPanel;
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

        final Service service = ( (Project) getApplication() ).getService();
        resourceSpecs = service.findAllResourceSpecs();
        init( service );
    }

    @SuppressWarnings( {"unchecked"} )
    private void init( Service service ) {
        add( new Label( "title", "Index" ) );

        List<Scenario> scenarios = service.list( Scenario.class );

        add( new ScenariosPanel( "all-scenarios",
                new Model<ArrayList<Scenario>>( (ArrayList) scenarios ) ) );
        Form form = new Form( "resourceSpecs-form" ) {
            protected void onSubmit() {
                setResponsePage( new RedirectPage( "index.html" ) );
            }
        };
        form.add( new ResourceSpecsPanel( "all-resourceSpecs",
                new Model<ArrayList<ResourceSpec>>(
                        (ArrayList) resourceSpecs ),
                PAGE_SIZE ) );
        add( form );
        add( new ResourceSpecPanel( "new-resourceSpec",
                new Model<ResourceSpec>( new ResourceSpec() ) ) );
    }
}
