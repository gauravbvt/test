package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * ...
 */
public class ScenariosPanel extends Panel {

    /** The selected scenario. */
    private RefModel scenarioModel;

    public ScenariosPanel( String id, Ref scenario ) {
        super( id );
        PlaybookSession session = (PlaybookSession) getSession();
        final List<Ref> scenarios = (List<Ref>) session.getProject().get( "scenarios" );

        scenarioModel = new RefModel(
            scenario != null ? scenario
                             : scenarios.size() > 0 ?  scenarios.get(0)
                             : null );

        refresh( scenarioModel.getRef() );

        final Form form = new Form( "sc-form" );
        add( form );

        form.add( new DropDownChoice(
            "sc-list", scenarioModel, scenarios,
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
                return object == scenarioModel;
            }

            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            protected void onSelectionChanged( Object newSelection ) {
                refresh( (Ref) newSelection );
                // TODO store in user preference
            } } );
    }

    private void refresh( Ref scenarioRef ) {
        addOrReplace( new ContentPanel( "contents", new RefDataProvider( scenarioRef, "occurrences" ) ) );
        addOrReplace( new Label( "sc-name", new RefPropertyModel( scenarioRef, "name" ) ) );
        addOrReplace( new Label( "sc-desc", new RefPropertyModel( scenarioRef, "description" ) ) );
    }
}
