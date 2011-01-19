// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Report L4.
 */
public class FlowReportPage extends AbstractReportPage {

    /** Short-hands for vcard compatible channel types. */
    public enum PhoneType {
        HOME, MSG, WORK, PREF, FAX, CELL, VIDEO, PAGER, BBS, MODEM, CAR, ISDN, PCS, VOICE
    }

    private Flow flow;

    @SpringBean
    private ImagingService imagingService;

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
                            new WebMarkupContainer( "details" ).add(
                                new WebMarkupContainer( "handling-div" )
                                    .add(
                                        new Label( "specialHandling", eoi.getSpecialHandling() ) )
                                    .setVisible( !eoi.getSpecialHandling().isEmpty() ),
                                new WebMarkupContainer( "desc-div" )
                                    .add(
                                        new Label( "description", eoi.getDescription() ) )
                                    .setVisible( !eoi.getDescription().isEmpty() )
                            ).setVisible( !eoi.getSpecialHandling().isEmpty()
                                          || !eoi.getDescription().isEmpty() ),
                            new Label( "content", eoi.getContent() )
                        );
                    }
                } )
                .setVisible( !getFlow().getEois().isEmpty() ),

            new Label( "commonContact" ),
            newVcards(),

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
                .setVisible( !getFlow().getDescription().isEmpty() ),

            new Label("year", "" + Calendar.getInstance().get(Calendar.YEAR) ),
            new Label("client", getService().getPlan().getClient() )
        );
    }

    private Component newVcards() {
        return new ListView<Assignment>( "vcards" ) {
            @Override
            protected void populateItem( ListItem<Assignment> item ) {
                Assignment assignment = item.getModelObject();
                ModelEntity entity = (ModelEntity) assignment.getSpecableActor();
                Employment employment = assignment.getEmployment();

                List<Info> infos = new ArrayList<Info>();
                for ( Info i : Arrays.asList(
                    new Info( "Description", entity.getDescription(), "note" ),
                    new Info( "Title", employment.getJob() == null ? null
                                       : employment.getJob().getTitle(), "title" ),
                    new Info( "Role", assignment.getRole() == null ? null
                                       : assignment.getRole().getName(), "role" ),
                    new Info( "Jurisdiction", assignment.getJurisdiction() == null ? null
                                       : assignment.getJurisdiction().toString(), null ),
                    new Info( "Organization", assignment.getOrganization() == null ? null
                                       : assignment.getOrganization().toString(), "org" ),
                    new Info( "Supervisor", employment.getSupervisor() == null ? null
                                       : employment.getSupervisor().getName(), null )
                ) )
                    if ( i.getValue() != null && !i.getValue().isEmpty() )
                        infos.add( i );


                item.add(
                    new Label( "fullName", entity.getName() ),
                    new GridView<Info>( "agent-infos", new ListDataProvider<Info>( infos ) ) {
                        @Override
                        protected void populateEmptyItem( Item<Info> item ) {
                            item.add(
                                new Label( "label", "" ),
                                new Label( "value", "" )
                            ).setRenderBodyOnly( true );
                        }

                        @Override
                        protected void populateItem( Item<Info> item ) {
                            Info i = item.getModelObject();
                            Label label = new Label( "value", i.getValue() );
                            item.add(
                                new Label( "label", i.getLabel() ),
                                label
                            ).setRenderBodyOnly( true );

                            if ( i.getHcardTag() != null )
                                label.add( new AttributeModifier( "class", true,
                                            new Model<String>( i.getHcardTag() ) ) );
                        }
                    }
                            .setColumns( 2 ),

                    new ChannelPanel( "channels", getService(), assignment.getSpecableActor() )
                        .setRenderBodyOnly( true ),
                    new WebMarkupContainer( "super-channels-wrapper" )
                        .add( new ChannelPanel( "super-channels",
                                      getService(), assignment.getEmployment().getSupervisor() )
                            )
                        .setVisible( employment.getSupervisor() != null )
                        .setRenderBodyOnly( true ),
                    new WebMarkupContainer( "org-channels-wrapper" )
                        .add( new ChannelPanel( "org-channels",
                                      getService(), assignment.getOrganization() ) )
                        .setVisible( assignment.getOrganization() != null )
                        .setRenderBodyOnly( true ),

                    new WebMarkupContainer( "pic" )
                        .add( new AttributeModifier(
                               "src", new Model<String>( getPictureUrl( entity ) ) ),
                              new AttributeModifier(
                               "alt", new Model<String>( entity.getName() ) ) )
                );
            }
        }.add( newCssClass( isSending() ? "to" : "from" ) );
    }

    private String getPictureUrl( ModelEntity entity ) {
        String s = imagingService.getSquareIconUrl( entity );

        if ( s == null ) {
            ResourceSpec spec = new ResourceSpec( (Specable) entity );
            return spec.isActor()        ? "images/actor.user.png"
                 : spec.isOrganization() ? "images/organization.building.png"
                 : spec.isRole()         ? "images/role.png"
                                         : "images/system.png";
        } else
            return s;
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
        Flow flow = getFlow();
        if ( flow.isSharing() && !flow.isProhibited() ) {
            Assignments assignments = getService().getAssignments();
            Place locale = assignments.getLocale();

            for ( Assignment committer : assignments.with( flow.getSource() ) ) {
                Specable committerActor = committer.getSpecableActor();
                for ( Assignment beneficiary : assignments.with( flow.getTarget() ) )
                    if ( !committerActor.equals( beneficiary.getSpecableActor() )
                            && flow.allowsCommitment( committer, beneficiary, locale, getService() )
                            )
                        return "Yes, covered by a sharing agreement";
            }
        }

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
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getOtherPart() ) )
            if ( !goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Goal> getGains() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getOtherPart() ) )
            if ( goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Assignment> getVcards() {
        return getService().getAssignments().assignedTo( getOtherPart() ).getAssignments();
    }

    private Part getOtherPart() {
        return isSending() ? (Part) getFlow().getTarget()
                           : (Part) getFlow().getSource();
    }

    private boolean isSending() {
        return getPart().equals( getFlow().getSource() );
    }

    public String getCommonContact() {
        Part otherPart = getOtherPart();
        return ( isSending() ? "To " : "From " )
             + getService().getAssignments().assignedTo( otherPart
                    ).getCommonSpec( otherPart ).getReportSource();
    }

    //============================================
    public static class Info implements Serializable {
        private String label;
        private String value;
        private String hcardTag;

        public Info( String label, String value, String hcardTag ) {
            this.hcardTag = hcardTag;
            this.label = label;
            this.value = value;
        }

        public String getHcardTag() {
            return hcardTag;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}
