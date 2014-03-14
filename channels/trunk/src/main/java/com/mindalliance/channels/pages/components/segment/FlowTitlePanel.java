/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
import java.util.List;

/**
 * Flow title panel.
 */
public class FlowTitlePanel extends AbstractUpdatablePanel {

    private Flow flow;
    private boolean isSend;

    public FlowTitlePanel( String id, Flow flow, boolean isSend ) {
        super( id );
        this.flow = flow;
        this.isSend = isSend;
        init();
    }

    private void init() {
        addConceptual();
        addTitleLabels();
        addOverridesImage();
    }

    private void addConceptual() {
        WebMarkupContainer conceptualImage = new WebMarkupContainer( "conceptual" );
        List<String> causes = getAnalyst().findConceptualCausesInPlan( getCommunityService(), flow );
        conceptualImage.setVisible( !causes.isEmpty() );
        if ( !causes.isEmpty() ) {
            addTipTitle(
                    conceptualImage,
                    new Model<String>( "Can not be realized: "
                            + StringUtils.capitalize( ChannelsUtils.listToString( causes, ", and " ) ) ) );
        }
        add( conceptualImage );

    }

    private void addTitleLabels() {
        Label preLabel = new Label( "pre", new Model<String>( getPre() ) );
        add( preLabel );
        Label infoLabel = new Label( "info", new Model<String>( getInfo() ) );
        add( infoLabel );
        Label postLabel = new Label(
                "post",
                new Model<String>( getPost() + "." + prohibitedString() ) );
        add( postLabel );
    }

    private String prohibitedString() {
        return flow.canGetProhibited() && flow.isProhibited()
                ? " PROHIBITED."
                : "";
    }

    private String getPre() {
        boolean published = flow.isPublished();
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
                        ( flow.isRestricted() ? " if " + flow.getRestrictionString( isSend ) : "" ) );
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
                            ( flow.isRestricted() ? " if " + flow.getRestrictionString( isSend ) : "" ) );
                } else {
                    return "Notified of";
                }
            }

        }
    }

    private String getInfo() {
        String message = flow.getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        Flow.Intent intent = flow.getIntent();
        if ( intent != null )
            message += " (" + intent.getLabel() + ")";
        return message.toLowerCase();
    }

    private String getPost() {
        StringBuilder sb = new StringBuilder(  );
        Node node = flow.getTarget();
        if ( isSend ) {
            sb.append( node.isConnector() ? flow.getRestrictionString( isSend ) : "" );
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() )
                sb.append( ( flow.isRestricted() ? "if " + flow.getRestrictionString( isSend ) : "" ) );

            else {
                Part part = (Part) source;
                sb.append( flow.isAskedFor() ?
                        "" :
                        MessageFormat.format( " by {0}{1}{2}{3}",
                                flow.getShortName( part, false ),
                                Flow.getOrganizationString( part ),
                                Flow.getJurisdictionString( part ),
                                ( flow.isRestricted() ? " if " + flow.getRestrictionString( isSend ) : "" ) ) );
            }
        }
        if ( flow.canSetAssets( isSend ) && !flow.getAssetConnections().isEmpty() ) {
            sb.append(". ")
                    .append( StringUtils.capitalize( flow.getAssetConnections().getLabel() ) );
        }
        return sb.toString();
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
                    new Model<String>( "images/" + image )
            ) );
            addTipTitle( overridesImage, new Model<String>( title ) );
        }
        overridesImage.setVisible( overridden || overriding );
        addOrReplace( overridesImage );
    }

}
