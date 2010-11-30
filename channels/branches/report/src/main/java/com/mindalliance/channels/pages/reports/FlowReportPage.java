// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.NotFoundException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Report L4.
 */
public class FlowReportPage extends AbstractReportPage {

    private Flow flow;

    public FlowReportPage( PageParameters parameters ) {
        super( parameters );

        add(
            SOPsReportPage.newFeedbackWidget( getPlanManager(), getService().getPlan() ),
            new Label( "pageTitle" ),
            new Label( "reportTitle" ),

            new BookmarkablePageLink<SOPsReportPage>( "top-link",
                                                      SOPsReportPage.class, getTopParameters() ),
            new Label( "assignment.part.segment.event.name" ),
            new Label( "assignment.part.segment.phase.name" ),
            new Label( "type" ),
            new Label( "assignment.part.task" ),
            new Label( "flow.name" ),
            new Label( "flowTitle" ),
            new Label( "flow.maxDelay" ),
            new Label( "agreement" ),

            new WebMarkupContainer( "eoi-div" )
                .add( new ListView<ElementOfInformation>( "flow.eois" ) {
                    @Override
                    protected void populateItem( ListItem<ElementOfInformation> item ) {
                        ElementOfInformation eoi = item.getModelObject();
                        item.add(
                            new ListView<Classification>( "classifications", eoi.getClassifications() ) {
                                @Override
                                protected void populateItem( ListItem<Classification> item2 ) {
                                    Classification classification = item2.getModelObject();
                                    item2.add(
                                        new Label( "classification", classification.getLabel() )
                                    );
                                }
                            },
                            new Label( "specialHandling", eoi.getSpecialHandling() ),
                            new Label( "description", eoi.getDescription() ),
                            new Label( "content", eoi.getContent() )
                        );
                    }
                } )
                .setVisible( !getFlow().getEois().isEmpty() ),

            new ListView<Attachment>( "documentation" ) {
                @Override
                protected void populateItem( ListItem<Attachment> item ) {
                    Attachment attachment = item.getModelObject();
                    item.add(
                        new ExternalLink( "doc-link", attachment.getUrl() )
                            .add( new Label( "doc-title", attachment.getLabel() ) )
                    );
                }
            },
            new WebMarkupContainer( "no-docs" ).setVisible( getDocumentation().isEmpty() ),

            new ListView<Attachment>( "policies" ) {
                @Override
                protected void populateItem( ListItem<Attachment> item ) {
                    Attachment attachment = item.getModelObject();
                    item.add(
                        new ExternalLink( "policy-link", attachment.getUrl() )
                            .add( new Label( "policy-title", attachment.getLabel() ) )
                    );

                    item.add( newCssClass( attachment.getType().toString().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-policies" ).setVisible( getPolicies().isEmpty() ),
            new ListView<Goal>( "risks" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item.add( new Label( "risk-description", goal.getFullTitle() ) );
                    item.add( newCssClass( goal.getLevel().getName().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-risk" ).setVisible( getRisks().isEmpty() ),

            new ListView<Goal>( "gains" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item
                        .add( new Label( "gain-description", goal.getFullTitle() ) )
                        .add( newCssClass( goal.getLevel().getName().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-gain" ).setVisible( getGains().isEmpty() ),

            new WebMarkupContainer( "instructions" )
                .add( new Label( "flow.description" ) )
                .setVisible( !getFlow().getDescription().isEmpty() )
        );
    }

    public Flow getFlow() {
        if ( flow == null ) {
            try {
                flow = getService().find( Flow.class, getPageParameters().getAsLong( FLOW_PARM ) );

            } catch ( NumberFormatException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

            } catch ( NotFoundException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
            }
        }

        return flow;
    }

    public String getFlowTitle() {
        Flow f = getFlow();
        return getPart().equals( f.getSource() ) ? f.getSendTitle()
                                                 : f.getReceiveTitle();
    }

    public String getAgreement() {
        return "Not covered by a sharing agreement";
    }

    public List<Attachment> getDocumentation() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : getAttachmentManager().getMediaReferences( getFlow() ) )
            if ( Attachment.Type.Reference.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Attachment> getPolicies() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : getAttachmentManager().getMediaReferences( getFlow() ) )
            if ( !Attachment.Type.Reference.equals( a.getType() )
                 && !Attachment.Type.Image.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Goal> getRisks() {
        return new ArrayList<Goal>();
    }

    public List<Goal> getGains() {
        return new ArrayList<Goal>();
    }

}
