package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Part summary panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2010
 * Time: 7:19:59 PM
 */
public class PartSummaryPanel extends AbstractUpdatablePanel {

    private IModel<Part> partModel;

    public PartSummaryPanel( String id, IModel<Part> partModel ) {
        super( id );
        this.partModel = partModel;
        init();
    }

    private void init() {
        addSummaryLabels( );
    }

    private void addSummaryLabels( ) {
        Label preLabel = new Label( "pre", new Model<String>( getPre() ) );
        add( preLabel );
        Label infoLabel = new Label( "task", new Model<String>( getTask() ) );
        add( infoLabel );
        Label postLabel = new Label( "post", new Model<String>( getPost() ) );
        add( postLabel );
    }

    private String getPre() {
        Part part = getPart();
        StringBuilder sb = new StringBuilder();
        if ( part.getActor() != null ) {
            sb.append( part.getActor().getName() );
            if ( part.getActor().isType() ) {
                Actor impliedActor = getQueryService().getKnownActualActor( part );
                if ( impliedActor != null ) {
                    sb.append( " " );
                    sb.append( impliedActor.getName() );
                }
            }
        }
        if ( part.getRole() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( ' ' );
            if ( part.getActor() == null ) {
                Actor impliedActor = getQueryService().getKnownActualActor( part );
                if ( impliedActor != null ) {
                    sb.append( impliedActor.getName() );
                } else {
                    sb.append( "Any " );
                }
            }
            if ( getQueryService().getKnownActualActor( part ) != null ) {
                if ( part.getActor() != null ) {
                    sb.append( " as " );
                } else {
                    sb.append( " as the only " );
                }
            }
            sb.append( part.getRole().getName() );
        }
        if ( part.getActor() == null && part.getRole() == null ) {
            sb.append( "Someone" );
        }
        if ( part.getJurisdiction() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " for " );
            sb.append( part.getJurisdiction().getName() );
        }
        if ( part.getOrganization() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " at " );
            sb.append( part.getOrganization().getName() );
        }
        sb.append( " is assigned task" );
        return sb.toString();
    }

    private String getTask() {
        String task = getPart().getTask();
        Part.Category category = getPart().getCategory();
        if ( category != null ) {
            task += " (" + category.getLabel().toLowerCase() + ")";
        }
        return task;
    }

    private String getPost() {
        Part part = getPart();
        StringBuilder sb = new StringBuilder();
        if ( part.getLocation() != null && !part.getLocation().toString().isEmpty() ) {
            sb.append( "to be executed " );
            sb.append( part.getLocation().toString() );
        }
        sb.append( "." );
        if ( part.isRepeating()
                || part.isSelfTerminating()
                || part.initiatesEvent()
                || part.isAutoStarted()
                || part.isTerminatesEventPhase() ) {
            sb.append( " The task" );
            StringBuilder sb1 = new StringBuilder();
            Phase phase = part.getSegment().getPhase();
            if ( part.isStartsWithSegment() ) {
                sb1.append( " is triggered by witnessing the " );
                String timing =  phase.isPreEvent()
                        ? "possibility"
                        : phase.isConcurrent()
                        ? "start"
                        : "end";
                sb1.append( timing );
                sb1.append( " of event \"" );
                sb1.append( part.getSegment().getEvent().getName() );
                sb1.append( "\"" );
            }
            if ( part.isOngoing() ) {
                sb1.append( " is ongoing" );
            }
            if ( part.isRepeating() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !part.initiatesEvent() && !part.isSelfTerminating() && !part.isTerminatesEventPhase() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " is repeated " );
                sb1.append( part.getRepeatsEvery().toString() );
            }
            if ( part.initiatesEvent() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !part.isSelfTerminating() && !part.isTerminatesEventPhase() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " initiates event \"" );
                sb1.append( part.getInitiatedEvent().getName() );
                sb1.append( "\"" );
            }
            if ( part.isTerminatesEventPhase() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !part.isSelfTerminating() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " can end \"" );
                sb1.append( part.getSegment().getPhaseEventTitle().toLowerCase() );
                sb1.append( "\"" );
            }
            if ( part.isSelfTerminating() ) {
                if ( !sb1.toString().isEmpty() ) sb1.append( " and" );
                sb1.append( " terminates by itself" );
            }
            sb1.append( "." );
            sb.append( sb1 );
        }
        if ( part.isAsTeam() ) {
            sb.append( " Assignees work as a team." );
        }
        if ( !part.getAssetConnections().isEmpty() ) {
            sb.append(" ")
                    .append( StringUtils.capitalize( part.getAssetConnections().getLabel( ) ) )
                    .append(".");
        }
        if ( part.isProhibited() ) {
            sb.append( " PROHIBITED." );
        }
        return sb.toString();
    }

    private Part getPart() {
        return partModel.getObject();
    }
}
