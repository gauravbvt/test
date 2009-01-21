package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.pages.components.ResourceSpecPanel;
import com.mindalliance.channels.pages.components.ResourceSpecsPanel;
import com.mindalliance.channels.pages.components.ScenariosPanel;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Scenario;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 10:12:52 AM
 */
public class IndexPage extends WebPage {

    private ArrayList<ResourceSpec> resourceSpecs = new ArrayList<ResourceSpec>();
    final private Dao dao;

    public IndexPage( PageParameters parameters ) {
        super( parameters );
        dao = Project.getProject().getDao();
        Iterator<ResourceSpec> allResourceSpecs = dao.resourceSpecs();
        // Take a local copy of all known resource specs
        while ( allResourceSpecs.hasNext() ) resourceSpecs.add( new ResourceSpec(allResourceSpecs.next()) );
        init();
    }

    private void init() {
        add( new Label( "title", "Index" ) );
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        Iterator<Scenario> iterator = dao.scenarios();
        while (iterator.hasNext()) scenarios.add(iterator.next());
        add (new ScenariosPanel("all-scenarios", new Model<ArrayList<Scenario>>(scenarios)));
        Form form = new Form( "resourceSpecs-form" ) {
            protected void onSubmit() {
                for (ResourceSpec spec: resourceSpecs) {
                    if (spec.isMarkedForDeletion()) {
                        dao.removeResourceSpec( spec );
                    }
                }
            }
        };
        form.add( new ResourceSpecsPanel("all-resourceSpecs", new Model<ArrayList<ResourceSpec>>(resourceSpecs)));
        add( form );
        add( new ResourceSpecPanel( "new-resourceSpec", new Model<ResourceSpec>( new ResourceSpec() ) ) );
    }
}
