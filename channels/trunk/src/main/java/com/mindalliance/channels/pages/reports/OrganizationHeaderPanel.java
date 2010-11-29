package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.ResourceSpec;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Organization header panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2010
 * Time: 7:50:11 PM
 */
public class OrganizationHeaderPanel extends Panel {
    private Organization organization;

    public OrganizationHeaderPanel(
            String id,
            Organization organization,
            final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        this.organization = organization;
        add( new Label( "org-title", getOrgTitle() ) );
        add( new Label( "name", organization.getName() ) );
        add( new WebMarkupContainer( "pic" )
                .add( new AttributeModifier(
                "src", new Model<String>( getPictureUrl( organization ) ) ),
                new AttributeModifier(
                        "alt", new Model<String>( organization.getName() ) ) ) );
        add( new Label( "mission", organization.getMission() ).setVisible( !organization.getMission().isEmpty() ) );
        add( new WebMarkupContainer( "tags-container" )
                .add( new ListView<ModelEntity>( "tags", organization.getTags() ) {
                    protected void populateItem( ListItem<ModelEntity> item ) {
                        ModelEntity tag = item.getModel().getObject();
                        item.add( new Label( "tag", tag.getName() ) );
                    }
                } )
                .setVisible( !organization.getTags().isEmpty() ) );
        WebMarkupContainer banner = new WebMarkupContainer( "banner" );
        banner.setVisible( !organization.isUnknown() );
        add( banner );
        banner.add( new ChannelsBannerPanel(
                "channels",
                new ResourceSpec( organization ),
                null,
                new ArrayList<Channel>() ) );
        add( new DocumentsReportPanel( "documents", new Model<ModelObject>( organization ) ) );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( organization ) )
                .setVisible( showingIssues )
        );
    }

    private String getOrgTitle() {
        return organization.isActual()
                ? "Organization"
                : "Organizations of type";
    }


    private String getPictureUrl( ModelObject modelObject ) {
        String url = modelObject.getImageUrl();
        url = url == null ? "images/organization.building.png" : url;
        return Attachment.addPrefixIfRelative( url, "../" );
    }

}
