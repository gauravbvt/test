package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Node;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.text.MessageFormat;

/**
 * Show external flows connected to a connector.
 */
public class ConnectedFlowList extends Panel {

    public ConnectedFlowList( String id, Connector connector ) {
        super( id );
        setRenderBodyOnly( true );
        final boolean input = connector.isInput();
        add( new Label( "label", "" ) );                                                  // NON-NLS

        add( new ConnectionView( "list", connector, input ) );                            // NON-NLS
    }

    /**
     * View list for external connections.
     */
    private static class ConnectionView extends RefreshingView<ExternalFlow> {

        /** The connector to follow. */
        private final Connector connector;

        /** True if connector is an imput connector. */
        private final boolean input;

        private ConnectionView( String id, Connector connector, boolean input ) {
            super( id );
            this.connector = connector;
            this.input = input;
        }                                  // NON-NLS

        @Override
        @SuppressWarnings( { "unchecked" } )
        protected Iterator<IModel<ExternalFlow>> getItemModels() {
            return new TransformIterator( connector.externalFlows(),
                new Transformer() {
                    public Object transform( Object o ) {
                        return new Model<ExternalFlow>( (ExternalFlow) o );
                    }
                } );
        }

        @Override
        protected void populateItem( Item<ExternalFlow> item ) {
            final ExternalFlow flow = item.getModelObject();
            final Node target = input ? flow.getSource() : flow.getTarget();
            final ScenarioLink link = new ScenarioLink( "part", target, flow );           // NON-NLS
            link.add( new Label( "part-label", target.getName() ) );                      // NON-NLS
            item.add( link );
            item.add( new Label( "scenario", target.getScenario().getName() ) );          // NON-NLS

            String c = flow.getChannel();
            if ( c != null )
                c = MessageFormat.format( "- {0}", c );
            item.add( new Label( "channel", c ) );                                        // NON-NLS
        }
    }
}
