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
        List<String> causes = getAnalyst().findConceptualCauses( getQueryService(), flow );
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
                String format = flow.isAskedFor()
                        ? "Can answer {0}with"
                        : flow.isIfTaskFails()
                        ? "If task fails, would notify {0}of"
                        : "Can notify {0}of";
                return MessageFormat.format(
                        format,
                        ( published ? "" : "privately " ) );
            } else {
                // send
                Part part = (Part) node;
                String format = flow.isAskedFor()
                        ? "Answer {0}{1}{2}{3}{4} with"
                        : flow.isIfTaskFails()
                        ? "If task fails, notify {0}{1}{2}{3}{4} of"
                        : "Notify {0}{1}{2}{3}{4} of";

                return MessageFormat.format(
                        format,
                        ( published ? "" : "privately " ),
                        flow.getShortName( node, true ),
                        Flow.getOrganizationString( part ),
                        Flow.getJurisdictionString( part ),
                        ( flow.isRestricted() ? " if " + flow.getRestrictionString( isSend ) : "" ) );
            }
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() ) {
                String format = !flow.isAskedFor()
                        ? "Needs to be notified {0}of"
                        : flow.isIfTaskFails()
                        ? "If task fails, needs to ask {0}for"
                        : "Needs to ask {0}for";
                return MessageFormat.format(
                        format,
                        ( published ? "" : "privately " ) );
            } else {
                Part part = (Part) source;
                if ( flow.isAskedFor() ) {
                    String ask = flow.isIfTaskFails()
                            ? "If task fails, ask"
                            : "Ask";
                    return MessageFormat.format(
                            "{0}{1} {2}{3}{4}{5} for",
                            ask,
                            ( published ? "" : " privately" ),
                            flow.getShortName( part, false ),
                            Flow.getOrganizationString( part ),
                            Flow.getJurisdictionString( part ),
                            ( flow.isRestricted() ? " if " + flow.getRestrictionString( isSend ) : "" ) );
                } else {
                    String format = "Notified {0}of";
                    return MessageFormat.format(
                            format,
                            ( published ? "" : "privately " )
                    );
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
        Node node = flow.getTarget();
        if ( isSend ) {
            return node.isConnector() ? flow.getRestrictionString( isSend ) : "";
        } else {
            // receive
            Node source = flow.getSource();
            if ( source.isConnector() )
                return ( flow.isRestricted() ? "if " + flow.getRestrictionString( isSend ) : "" );

            else {
                Part part = (Part) source;
                return flow.isAskedFor() ?
                        "" :
                        MessageFormat.format( " by {0}{1}{2}{3}",
                                flow.getShortName( part, false ),
                                Flow.getOrganizationString( part ),
                                Flow.getJurisdictionString( part ),
                                ( flow.isRestricted() ? "if " + flow.getRestrictionString( isSend ) : "" ) );
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
                    new Model<String>( "images/" + image )
            ) );
            addTipTitle( overridesImage, new Model<String>( title ) );
        }
        overridesImage.setVisible( overridden || overriding );
        addOrReplace( overridesImage );
    }

}
