package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.pages.filters.RootFilter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * ...
 */
public class ScenariosPanel extends Panel {

    /** The selected scenario. */
    private IModel scenario;

    public ScenariosPanel( String id, IModel project ) {
        super( id, project );
        final IModel scenarios = new RefPropertyModel( project, "scenarios" );

        if ( scenario == null ) {
            List<Ref> ss = (List<Ref>) scenarios.getObject();
            if ( ss.size() > 0 )
                scenario = new RefModel( ss.get(0) );
        }

        final FilterPanel filter = new FilterPanel( "filter",
            new RootFilter(),
            new RefDataProvider( scenario.getObject(), "occurrences" ) );
        add( filter );
        add( new ContentPanel( "contents", new RefDataProvider( scenario.getObject(), "occurrences" ) ) );
        add( new Label( "sc-name", new RefPropertyModel( scenario, "name" ) ) );
        add( new Label( "sc-desc", new RefPropertyModel( scenario, "description" ) ) );

        final Form form = new Form( "sc-form" );
        add( form );

        form.add( new DropDownChoice(
            "sc-list", scenario, scenarios,
                new IChoiceRenderer() {
                    public Object getDisplayValue( Object object ) {
                        Ref s = (Ref) object;
                        return s.deref( "name" );
                    }

                    public String getIdValue( Object object, int index ) {
                        return (String) getDisplayValue( object );
                    }
                } ) {

            protected boolean isSelected( Object object, int index, String selected ) {
                return object == scenario.getObject();
            }

            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            protected void onSelectionChanged( Object newSelection ) {
                // TODO store in user preference
            } } );
    }

    public IModel getScenario() {
        return scenario;
    }

    public void setScenario( IModel scenario ) {
        this.scenario = scenario;
    }
}
