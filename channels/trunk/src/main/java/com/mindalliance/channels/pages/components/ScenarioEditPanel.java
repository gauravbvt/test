package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.PageParameters;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends Panel {

    public ScenarioEditPanel( String id, final Scenario scenario ) {
        super( id, new CompoundPropertyModel<Scenario>( scenario ) );

        final TextField<String> name = new TextField<String>( "name" );                   // NON-NLS
        add( new FormComponentLabel( "name-label", name ) );                              // NON-NLS
        add( name );

        final TextArea<String> desc = new TextArea<String>( "description" );              // NON-NLS
        add( new FormComponentLabel( "description-label", desc ) );                       // NON-NLS
        add( desc );
        Set<Long> expansions = new HashSet<Long>( );  // TODO - get expansion parameters
        add (new IssuesPanel( "issues", new Model<ModelObject>(scenario), expansions));
    }
}
