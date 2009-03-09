package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ProfileLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 20, 2009
 * Time: 4:21:58 PM
 */
public class ResourceSpecPanel extends Panel {
    /**
     * ResourceSpec shown in panel
     */
    private ResourceSpec resourceSpec;
    /**
     * Textfield
     */
    private TextField actorNameField;
    /**
     * Textfield
     */
    private TextField roleNameField;
    /**
     * Textfield
     */
    private TextField orgNameField;
    /**
     * Textfield
     */
    private TextField jurNameField;
    /**
     * Channel list panel
     */
    private ChannelListPanel channelsPanel;

    public ResourceSpecPanel( String s, IModel<ResourceSpec> model ) {
        super( s, model );
        resourceSpec = model.getObject();
        init();
    }

    private void init() {
        Form resourceSpecForm = new Form( "resourceSpec-form" ) {
            protected void onSubmit() {
                ResourceSpec newResourceSpec = new ResourceSpec();
                newResourceSpec.setActor(
                        Project.service().findOrCreate(
                                Actor.class,
                                actorNameField.getDefaultModelObjectAsString().trim() ) );
                newResourceSpec.setRole(
                        Project.service().findOrCreate(
                                Role.class, roleNameField.getDefaultModelObjectAsString().trim() ) );
                newResourceSpec.setOrganization(
                        Project.service().findOrCreate(
                                Organization.class,
                                orgNameField.getDefaultModelObjectAsString().trim() ) );
                newResourceSpec.setJurisdiction(
                        Project.service().findOrCreate(
                                Place.class, jurNameField.getDefaultModelObjectAsString().trim() ) );
                newResourceSpec.setChannels( ( (Channelable) channelsPanel.getDefaultModelObject() ).getEffectiveChannels() );
                if ( !newResourceSpec.isAnyone() ) {
                    getService().addOrUpdate( newResourceSpec );
                    setResponsePage(
                            new RedirectPage( ProfileLink.linkFor( newResourceSpec ) ) );
                }/* else {
                    setResponsePage(
                            new RedirectPage( ProfileLink.linkFor( resourceSpec ) ) );
                }*/
            }
        };
        add( resourceSpecForm );
        String actorName = "";
        if ( !resourceSpec.isAnyActor() ) actorName = resourceSpec.getActor().getName();
        resourceSpecForm.add( new ModelObjectLink( "actor-link",
                new PropertyModel<Actor>( resourceSpec, "actor" ) ) );
        actorNameField = new TextField<String>( "actor-name", new Model<String>( actorName ) );
        resourceSpecForm.add( actorNameField );
        resourceSpecForm.add( new ModelObjectLink( "role-link",
                new PropertyModel<Role>( resourceSpec, "role" ) ) );
        String roleName = "";
        if ( !resourceSpec.isAnyRole() ) roleName = resourceSpec.getRole().getName();
        roleNameField = new TextField<String>( "role-name", new Model<String>( roleName ) );
        resourceSpecForm.add( roleNameField );
        resourceSpecForm.add( new ModelObjectLink( "org-link",
                new PropertyModel<Organization>( resourceSpec, "organization" ) ) );
        String orgName = "";
        if ( !resourceSpec.isAnyOrganization() ) orgName = resourceSpec.getOrganization().getName();
        orgNameField = new TextField<String>( "org-name", new Model<String>( orgName ) );
        resourceSpecForm.add( orgNameField );
        resourceSpecForm.add( new ModelObjectLink( "jur-link",
                new PropertyModel<Place>( resourceSpec, "jurisdiction" ) ) );
        String jurName = "";
        if ( !resourceSpec.isAnyJurisdiction() ) jurName = resourceSpec.getJurisdiction().getName();
        jurNameField = new TextField<String>( "jur-name",
                new Model<String>( jurName ) );
        resourceSpecForm.add( jurNameField );
        channelsPanel = new ChannelListPanel( "channels", new Model<Channelable>( resourceSpec ) );
        resourceSpecForm.add( channelsPanel );
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }


}
