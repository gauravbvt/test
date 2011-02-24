package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Flow;
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
        addTitleLabels();
        addOverridesImage();
    }

    private void addTitleLabels() {
        Label preLabel = new Label( "pre", new Model<String>( getPre() ) );
        add( preLabel );
        Label infoLabel = new Label( "info", new Model<String>( getInfo() ) );
        add( infoLabel );
        Label postLabel = new Label(
                "post",
                new Model<String>( getPost() + "." + operationalString() + prohibitedString() ) );
        add( postLabel );
    }

    private String prohibitedString() {
        return flow.canGetProhibited() && flow.isProhibited()
                ? " PROHIBITED."
                : "";
    }

    private String operationalString() {
        return flow.canGetOperational() && !flow.isEffectivelyOperational()
                ? " Not operational."
                : "";
    }

    private String getPre() {
        if ( isSend ) {
            Node node = flow.getTarget();
            if ( node.isConnector() ) {
                return flow.isAskedFor()
                        ? "Can answer with"
                        : flow.isIfTaskFails()
                        ? "If task fails, would notify of"
                        : "Can notify of";
            } else {
                // send
                Part part = (Part) node;
                String format = flow.isAskedFor()
                        ? "Answer {0}{1}{2}{3} with"
                        : flow.isIfTaskFails()
                        ? "If task fails, notify {0}{1}{2}{3} of"
                        : "Notify {0}{1}{2}{3} of";

                return MessageFormat.format(
                        format,
                        flow.getShortName( node, true ),
                        Flow.getOrganizationString( part ),
                        Flow.getJurisdictionString( part ),
                        flow.getRestrictionString( isSend ) );
            }
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() ) {
                return !flow.isAskedFor()
                        ? "Needs to be notified of"
                        : flow.isIfTaskFails()
                        ? "If task fails, needs to ask for"
                        : "Needs to ask for";
            } else {
                Part part = (Part) source;
                if ( flow.isAskedFor() ) {
                    String ask = flow.isIfTaskFails()
                            ? "If task fails, ask"
                            : "Ask";
                    return MessageFormat.format(
                            "{0} {1}{2}{3}{4} for",
                            ask,
                            flow.getShortName( part, false ),
                            Flow.getOrganizationString( part ),
                            Flow.getJurisdictionString( part ),
                            flow.getRestrictionString( isSend ) );
                } else
                    return "Notified of";
            }

        }
    }

    private String getInfo() {
        String message = flow.getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        Flow.Intent intent = flow.getIntent();
        if ( intent != null ) {
            message += " (" + intent.getLabel() + ")";
        }
        return message.toLowerCase();
    }

    private String getPost() {
        Node node = flow.getTarget();
        if ( isSend ) {
            if ( node.isConnector() ) {
                return flow.getRestrictionString( isSend );
            } else {
                return "";
            }
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() ) {
                return flow.getRestrictionString( isSend );

            } else {
                Part part = (Part) source;
                if ( flow.isAskedFor() ) {
                    return "";
                } else {
                    return MessageFormat.format(
                            " by {0}{1}{2}{3}",
                            flow.getShortName( part, false ),
                            Flow.getOrganizationString( part ),
                            Flow.getJurisdictionString( part ),
                            flow.getRestrictionString( isSend ) );
                }
            }
        }

    }

    private void addOverridesImage() {
        boolean overriding = getQueryService().isOverriding( flow );
        boolean overridden = getQueryService().isOverridden( flow );
        boolean overrides = overriding && overridden;
        String image = overrides
                ? "overridden-overriding.png"
                : overriding
                ? "overriding.png"
                : overridden
                ? "overridden.png"
                : "";
        String title = overrides
                ? "This flow is overridden by and is overriding one or more flows"
                : overriding
                ? "This flow is overriding one or more flows"
                : overridden
                ? "This flow is overridden by one or more flows"
                : "";
        WebMarkupContainer overridesImage = new WebMarkupContainer( "overrides" );
        if ( overridden || overriding ) {
            overridesImage.add( new AttributeModifier(
                    "src",
                    true,
                    new Model<String>( "images/" + image )
            ) );
            overridesImage.add( new AttributeModifier(
                    "title",
                    true,
                    new Model<String>( title )
            ) );
        }
        overridesImage.setVisible( overridden || overriding );
        addOrReplace( overridesImage );
    }

    public QueryService getQueryService() {
        return queryService;
    }
}
