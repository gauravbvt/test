package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
import java.util.Iterator;

/**
 * Show external flows connected to a connector.
 */
public class ConnectedFlowList extends Panel {

    public ConnectedFlowList( String id, Connector connector ) {
        super( id );
        setRenderBodyOnly( true );
        final boolean input = connector.isSource();
        add( new Label( "label", "" ) );                                                  // NON-NLS

        add( new ConnectionView( "list", connector, input ) );                            // NON-NLS
    }

    /**
     * View list for external connections.
     */
    private static final class ConnectionView extends RefreshingView<ExternalFlow> {

        /**
         * The connector to follow.
         */
        private final Connector connector;

        /**
         * True if connector is an imput connector.
         */
        private final boolean input;

        private ConnectionView( String id, Connector connector, boolean input ) {
            super( id );
            this.connector = connector;
            this.input = input;
        }                                  // NON-NLS

        @Override
        @SuppressWarnings( {"unchecked"} )
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
            final ScenarioLink link = new ScenarioLink( "part",                           // NON-NLS
                    new Model<Node>( target ), flow );
            link.add( new Label( "part-label", target.getName() ) );                      // NON-NLS
            item.add( link );
            item.add( new Label( "scenario", target.getScenario().getName() ) );          // NON-NLS

            String c = Channel.toString( flow.getChannels() );
            final boolean needsChannel = input && flow.isAskedFor()
                    || !input && !flow.isAskedFor();
            if ( c != null && needsChannel ) {
                c = MessageFormat.format( "- {0}", c );
            } else {
                c = "";
            }
            item.add( new Label( "channels", c ) );                                        // NON-NLS
        }

        /**
         * Add issues annotations to a component.
         *
         * @param component the component
         * @param object    the object of the issues
         * @param property  the property of concern. If null, get issues of object
         * @todo refactor this here and there
         */
        protected void addIssues( Component component, ModelObject object, String property ) {

            final Analyst analyst = Channels.analyst();
            final String summary = property == null ? analyst.getIssuesSummary( object, false )
                    : analyst.getIssuesSummary( object, property );
            boolean hasIssues = analyst.hasIssues( object, Analyst.INCLUDE_PROPERTY_SPECIFIC );
            if ( !summary.isEmpty() ) {
                component.add( new AttributeModifier(
                        "class", true, new Model<String>( "error" ) ) );                  // NON-NLS
                component.add( new AttributeModifier(
                        "title", true, new Model<String>( summary ) ) );                    // NON-NLS
            } else {
            if ( hasIssues ) {
                // All waived issues
                component.add(
                        new AttributeModifier( "class", true, new Model<String>( "waived" ) ) );
                component.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            }
        }
        }
    }


}
