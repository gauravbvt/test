/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
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
        boolean input = connector.isSource();
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
            ExternalFlow flow = item.getModelObject();
            Node target = input ? flow.getSource() : flow.getTarget();
            ModelObjectLink link = new ModelObjectLink(
                    "part",
                    new Model<Node>( target ),
                    new Model<String>( target.getTitle() ) );
            item.add( link );
            item.add( new Label( "segment", target.getSegment().getName() ) );

            String c = Channel.toString( flow.getChannels() );
            boolean needsChannel = input && flow.isAskedFor()
                    || !input && !flow.isAskedFor();
            c = c != null && needsChannel ? MessageFormat.format( "- {0}", c ) : "";
            item.add( new Label( "channels", c ) );
        }
    }


}
