package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Part summary panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2010
 * Time: 7:19:59 PM
 */
public class PartSummaryPanel extends Panel {

    @SpringBean
    private QueryService queryService;

    private IModel<Part> partModel;

    public PartSummaryPanel( String id, IModel<Part> partModel ) {
        super( id );
        this.partModel = partModel;
        init();
    }

    private void init() {
        WebMarkupContainer summaryContainer = new WebMarkupContainer( "summary" );
        String priority = getPriorityCssClass();
        summaryContainer.add( new AttributeModifier( "class", true, new Model<String>( priority ) ) );
        add( summaryContainer );
        addSummaryLabels( summaryContainer );
    }

    private String getPriorityCssClass() {
        Level priority = queryService.computePartPriority( getPart() );
        return priority.getNegativeLabel().toLowerCase();
    }

    private void addSummaryLabels( WebMarkupContainer summaryContainer ) {
        Label preLabel = new Label( "pre", new Model<String>( getPre() ) );
        summaryContainer.add( preLabel );
        Label infoLabel = new Label( "task", new Model<String>( getTask() ) );
        summaryContainer.add( infoLabel );
        Label postLabel = new Label( "post", new Model<String>( getPost() ) );
        summaryContainer.add( postLabel );
    }

    private String getPre() {
        Part part = getPart();
        StringBuilder sb = new StringBuilder();
        if ( part.getActor() != null ) {
            sb.append( part.getActor().getName() );
            if ( part.getActor().isType() ) {
                Actor impliedActor = part.getKnownActualActor();
                if ( impliedActor != null ) {
                    sb.append( " " );
                    sb.append( impliedActor.getName() );
                }
            }
        }
        if ( part.getRole() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( ' ' );
            if ( part.getActor() == null ) {
                Actor impliedActor = part.getKnownActualActor();
                if ( impliedActor != null ) {
                    sb.append( impliedActor.getName() );
                } else {
                    sb.append( "Any " );
                }
            }
            if ( part.getKnownActualActor() != null ) {
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
        return getPart().getTask();
    }

    private String getPost() {
        Part part = getPart();
        StringBuilder sb = new StringBuilder();
        if ( part.getLocation() != null ) {
            sb.append( " at location \"" );
            sb.append( part.getLocation().getName() );
            sb.append( "\"" );
        }
        sb.append( "." );
        if ( part.isRepeating()
                || part.isSelfTerminating()
                || part.initiatesEvent()
                || part.isStartsWithSegment()
                || part.isTerminatesEventPhase() ) {
            sb.append( " The task" );
            StringBuilder sb1 = new StringBuilder();
            if ( part.isStartsWithSegment() ) {
                sb1.append( " starts with \"" );
                sb1.append( part.getSegment().getPhaseEventTitle().toLowerCase() );
                sb1.append( "\"" );
            }
            if ( part.isRepeating() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !part.initiatesEvent() && !part.isSelfTerminating() && !part.isTerminatesEventPhase() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " is repeated every " );
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
        return sb.toString();
    }

    private Part getPart() {
        return partModel.getObject();
    }
}
