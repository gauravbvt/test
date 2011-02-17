package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.query.Assignments;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Commitment report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/15/11
 * Time: 11:10 PM
 */
public class CommitmentReportPanel extends AbstractUpdatablePanel {

    /**
     * Short-hands for vcard compatible channel types.
     */
    public enum PhoneType {
        HOME, MSG, WORK, PREF, FAX, CELL, VIDEO, PAGER, BBS, MODEM, CAR, ISDN, PCS, VOICE
    }


    private final ReportHelper reportHelper;

    public CommitmentReportPanel( String id, ReportHelper reportHelper ) {
        super( id );
        this.reportHelper = reportHelper;
        init();
    }

    private void init() {
        reportHelper.setAsDefaultModel( this );
        add(
                new Label(
                        "flowTitle",
                        new PropertyModel<String>( this, "flowTitle" ) ),
                new Label( "flow.name" ),
                new Label( "flow.maxDelay" ),
                new Label(
                        "agreement",
                        new PropertyModel<String>( this, "agreement" ) ),

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
                        .setVisible( !reportHelper.getFlow().getEois().isEmpty() ),

                new Label( "commonContact", new PropertyModel<String>( this, "commonContact" ) )
                        .add( reportHelper.newCssClass( isSending() ? "sending" : "receiving" ) ),
                newVcards(),

                new ListView<Attachment>(
                        "documentation",
                        new PropertyModel<List<Attachment>>( this, "documentation" ) ) {
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

                new ListView<Attachment>(
                        "policies",
                        new PropertyModel<List<Attachment>>( this, "policies" ) ) {
                    @Override
                    protected void populateItem( ListItem<Attachment> item ) {
                        Attachment attachment = item.getModelObject();
                        item.add(
                                new ExternalLink( "policy-link", attachment.getUrl() )
                                        .add( new Label( "policy-title", attachment.getLabel() ) )
                        );

                        item.add( reportHelper.newCssClass( attachment.getType().toString().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-policies" ).setVisible( getPolicies().isEmpty() ),
                new ListView<Goal>(
                        "risks",
                        new PropertyModel<List<Goal>>( this, "risks" ) ) {
                    @Override
                    protected void populateItem( ListItem<Goal> item ) {
                        Goal goal = item.getModelObject();
                        item.add( new Label( "risk-description", goal.getFullTitle() ) );
                        item.add( reportHelper.newCssClass( goal.getLevel().getName().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-risk" ).setVisible( getRisks().isEmpty() ),

                new ListView<Goal>(
                        "gains",
                        new PropertyModel<List<Goal>>( this, "gains" ) ) {
                    @Override
                    protected void populateItem( ListItem<Goal> item ) {
                        Goal goal = item.getModelObject();
                        item
                                .add( new Label( "gain-description", goal.getFullTitle() ) )
                                .add( reportHelper.newCssClass( goal.getLevel().getName().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-gain" ).setVisible( getGains().isEmpty() ),

                new WebMarkupContainer( "instructions" )
                        .add( new Label( "flow.description" ) )
                        .setVisible( !reportHelper.getFlow().getDescription().isEmpty() )

        );
    }

    private Component newVcards() {
        return new ListView<Assignment>(
                "vcards",
                new PropertyModel<List<Assignment>>( this, "vcards" ) ) {
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

                        new ChannelPanel( "channels", reportHelper.getPlanService(), assignment.getSpecableActor() )
                                .setRenderBodyOnly( true ),
                        new WebMarkupContainer( "super-channels-wrapper" )
                                .add( new ChannelPanel( "super-channels",
                                        reportHelper.getPlanService(), assignment.getEmployment().getSupervisor() )
                                )
                                .setVisible( employment.getSupervisor() != null )
                                .setRenderBodyOnly( true ),
                        new WebMarkupContainer( "org-channels-wrapper" )
                                .add( new ChannelPanel( "org-channels",
                                        reportHelper.getPlanService(), assignment.getOrganization() ) )
                                .setVisible( assignment.getOrganization() != null )
                                .setRenderBodyOnly( true ),

                        new WebMarkupContainer( "pic" )
                                .add( new AttributeModifier(
                                        "src", new Model<String>( getPictureUrl( entity ) ) ),
                                        new AttributeModifier(
                                                "alt", new Model<String>( entity.getName() ) ) )
                );
            }
        }.add( reportHelper.newCssClass( isSending() ? "to" : "from" ) );
    }

    private String getPictureUrl( ModelEntity entity ) {
        String s = reportHelper.getImagingService().getSquareIconUrl( entity );

        if ( s == null ) {
            ResourceSpec spec = new ResourceSpec( (Specable) entity );
            return spec.isActor() ? "images/actor.user.png"
                    : spec.isOrganization() ? "images/organization.building.png"
                    : spec.isRole() ? "images/role.png"
                    : "images/system.png";
        } else
            return s;
    }

    public String getFlowTitle() {
        Flow f = reportHelper.getFlow();
        return reportHelper.getPart().equals( f.getSource() ) ? f.getSendTitle()
                : f.getReceiveTitle();
    }

    public String getAgreement() {
        Flow flow = reportHelper.getFlow();
        if ( flow.isSharing() && !flow.isProhibited() ) {
            Assignments assignments = reportHelper.getPlanService().getAssignments();
            Place locale = assignments.getLocale();

            for ( Assignment committer : assignments.with( flow.getSource() ) ) {
                Specable committerActor = committer.getSpecableActor();
                for ( Assignment beneficiary : assignments.with( flow.getTarget() ) )
                    if ( !committerActor.equals( beneficiary.getSpecableActor() )
                            && flow.allowsCommitment( committer, beneficiary, locale, reportHelper.getPlanService() )
                            )
                        return "Yes, covered by a sharing agreement";
            }
        }

        return "Not covered by a sharing agreement";
    }

    public List<Attachment> getDocumentation() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : reportHelper.getAttachmentManager().getMediaReferences( reportHelper.getFlow() ) )
            if ( Attachment.Type.Reference.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Attachment> getPolicies() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : reportHelper.getAttachmentManager().getMediaReferences( reportHelper.getFlow() ) )
            if ( !Attachment.Type.Reference.equals( a.getType() )
                    && !Attachment.Type.Image.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Goal> getRisks() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : reportHelper.getPlanService().findAllGoalsImpactedByFailure( getOtherPart() ) )
            if ( !goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Goal> getGains() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : reportHelper.getPlanService().findAllGoalsImpactedByFailure( getOtherPart() ) )
            if ( goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Assignment> getVcards() {
        return reportHelper.getPlanService().getAssignments().assignedTo( getOtherPart() ).getAssignments();
    }

    public String getCommonContact() {
        Part otherPart = getOtherPart();
        return ( isSending() ? "To " : "From " )
                + reportHelper.getPlanService().getAssignments().assignedTo( otherPart
        ).getCommonSpec( otherPart ).getReportSource();
    }

    private Part getOtherPart() {
        return isSending() ? (Part) reportHelper.getFlow().getTarget()
                : (Part) reportHelper.getFlow().getSource();
    }

    private boolean isSending() {
        return reportHelper.isSending();
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
