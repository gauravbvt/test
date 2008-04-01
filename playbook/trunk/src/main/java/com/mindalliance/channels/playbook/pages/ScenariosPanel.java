package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.RootFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * ...
 */
public class ScenariosPanel extends TabPanel {

    /** The selected scenario. */
    private IModel scenario ;

    public ScenariosPanel( String id, IModel project, List<Class<?>> classes ) {
        super( id, new ContainerModel( new Model(), "occurrences", classes ){
            public Filter getFilter() {
                return new RootFilter( Filter.ScenarioItems( this ) );
            }
        } );

        // TODO save/restore scenario selection
        IModel scenarios = new RefPropertyModel( project, "scenarios" );
        final ContainerModel containerModel = (ContainerModel) getModel();
        scenario = (IModel) containerModel.getTarget();
        List<Ref> ss = (List<Ref>) scenarios.getObject();
        scenario.setObject( ss.get(0) );
        containerModel.detach();

        PropertyModel sp = new PropertyModel( this, "scenario" );
        add( new Label( "content-title", new RefPropertyModel( sp.getObject(), "name" ) ) );
        add( new Label( "sc-desc", new RefPropertyModel( sp.getObject(), "description" ) ) );

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
                return object == getScenario().getObject();
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
