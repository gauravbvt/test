package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.MessageFormat;

/**
 * Flow title panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2010
 * Time: 5:18:21 PM
 */
public class FlowTitlePanel extends Panel {

    @SpringBean
    private QueryService queryService;

    private Flow flow;
    private boolean isSend;

    public FlowTitlePanel( String id, Flow flow, boolean isSend ) {
        super( id );
        this.flow = flow;
        this.isSend = isSend;
        init();
    }

    private void init() {
        WebMarkupContainer summaryContainer = new WebMarkupContainer( "summary" );
        String priority = getPriorityCssClass();
        summaryContainer.add( new AttributeModifier( "class", true, new Model<String>(priority)));
        add( summaryContainer );
        addTitleLabels( summaryContainer );
    }

    private String getPriorityCssClass() {
        if ( flow.isSharing() ) {
            Level priority = queryService.computeSharingPriority( flow );
            return  priority.getNegativeLabel().toLowerCase();
        } else {
            return "none";
        }
    }

    private void addTitleLabels( WebMarkupContainer summaryContainer ) {
        Label preLabel = new Label( "pre", new Model<String>( getPre() ) );
        summaryContainer.add( preLabel );
        Label infoLabel = new Label( "info", new Model<String>( getInfo() ) );
        summaryContainer.add( infoLabel );
        Label postLabel = new Label( "post", new Model<String>( getPost() ) );
        summaryContainer.add( postLabel );
    }

    private String getPre() {
        if ( isSend ) {
            Node node = flow.getTarget();
            if ( node.isConnector() ) {
                return flow.isAskedFor() ? "Can answer with"
                        : "Can notify of";

            } else {
                // receive
                Part part = (Part) node;
                String format = flow.isAskedFor() ? "Answer {0}{1}{2} with"
                        : "Notify {0}{1}{2} of";

                return MessageFormat.format(
                        format,
                        flow.getShortName( node, true ),
                        Flow.getOrganizationString( part ),
                        Flow.getJurisdictionString( part ) );
            }
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() ) {
                return flow.isAskedFor()
                        ? "Needs to ask for"
                        : "Needs to be notified of";
            } else {
                Part part = (Part) source;
                if ( flow.isAskedFor() )
                    return MessageFormat.format(
                            "Ask {0}{1}{2} for",
                            flow.getShortName( part, false ),
                            Flow.getOrganizationString( part ),
                            Flow.getJurisdictionString( part ) );
                else
                    return "Notified of";
            }

        }
    }

    private String getInfo() {
        String message = flow.getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        return message.toLowerCase();
    }

    private String getPost() {
        if ( isSend ) {
            return "";
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() ) {
                return "";

            } else {
                Part part = (Part) source;
                if ( flow.isAskedFor() ) {
                    return "";
                } else {
                    return MessageFormat.format(
                            " by {0}{1}{2}",
                            flow.getShortName( part, false ),
                            Flow.getOrganizationString( part ),
                            Flow.getJurisdictionString( part ) );
                }
            }
        }

    }
}
