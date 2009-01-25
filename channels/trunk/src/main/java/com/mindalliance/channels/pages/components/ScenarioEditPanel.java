package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.PageParameters;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends Panel {

    public ScenarioEditPanel( String id, final Scenario scenario, final PageParameters parameters ) {
        super( id, new CompoundPropertyModel<Scenario>( scenario ) );

        final TextField<String> name = new TextField<String>( "name" );                   // NON-NLS
        add( new FormComponentLabel( "name-label", name ) );                              // NON-NLS
        add( name );

        final TextArea<String> desc = new TextArea<String>( "description" );              // NON-NLS
        add( new FormComponentLabel( "description-label", desc ) );                       // NON-NLS
        add( desc );
        add (new IssuesPanel( "issues", new Model<ModelObject>(scenario), parameters));
    }
}
