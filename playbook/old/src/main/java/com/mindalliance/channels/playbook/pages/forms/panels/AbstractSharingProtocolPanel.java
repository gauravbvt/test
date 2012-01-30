package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 12:48:11 PM
 */
public abstract class AbstractSharingProtocolPanel
        extends AbstractComponentPanel {

    protected SharingProtocol sharingProtocol;
    protected DropDownChoice deliveryChoice;
    protected AgentSpecificationPanel contactsPanel;
    protected InformationDefinitionPanel infoSpecPanel;
    protected AjaxCheckBox anyMediumCheckBox;
    protected WebMarkupContainer mediaPreferrencesDiv;
    protected RefPreferencesPanel mediaPreferrences;
    private static final long serialVersionUID = 1784869652118671913L;

    protected AbstractSharingProtocolPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        sharingProtocol = (SharingProtocol) getComponent();
        deliveryChoice = new DropDownChoice<String>(
                "delivery",
                new RefPropertyModel(
                        getElement(), propPath + ".delivery" ),
                sharingProtocol.getDeliveryChoices() );
        deliveryChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            -2400075387537472885L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        setProperty(
                            "delivery",
                            deliveryChoice.getDefaultModelObjectAsString() );
                    }
                } );
        addReplaceable( deliveryChoice );
        contactsPanel = new AgentSpecificationPanel(
                "contacts", this, propPath + ".contacts" );
        addReplaceable( contactsPanel );
        infoSpecPanel = new InformationDefinitionPanel(
                "informationSpec", this, propPath + ".informationSpec" );
        addReplaceable( infoSpecPanel );

        anyMediumCheckBox = new AjaxCheckBox(
                "anyMedium", new Model<Boolean>(
                sharingProtocol.getPreferredMediumTypes().isEmpty() ) ) {
            private static final long serialVersionUID = -8106492632013539937L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean anyMedium = anyMediumCheckBox.getModelObject();
                if ( anyMedium ) {
                    setProperty( "preferredMediumTypes", new ArrayList<Ref>() );
                    mediaPreferrences = new RefPreferencesPanel(
                            "preferredMedia",
                            AbstractSharingProtocolPanel.this,
                            propPath + ".preferredMediumTypes",
                            new RefQueryModel(
                                    getScope(), new Query(
                                    "findAllTypes", "MediumType" ) ) );
                    addReplaceableTo( mediaPreferrences, mediaPreferrencesDiv );
                }
                setVisibility( mediaPreferrencesDiv, !anyMedium, target );
            }
        };
        addReplaceable( anyMediumCheckBox );
        mediaPreferrencesDiv = new WebMarkupContainer( "preferredMediaDiv" );
        setVisibility(
                mediaPreferrencesDiv,
                !sharingProtocol.getPreferredMediumTypes().isEmpty() );
        addReplaceable( mediaPreferrencesDiv );
        mediaPreferrences = new RefPreferencesPanel(
                "preferredMedia",
                this,
                propPath + ".preferredMediumTypes",
                new RefQueryModel(
                        getScope(), new Query(
                        "findAllTypes", "MediumType" ) ) );
        addReplaceableTo( mediaPreferrences, mediaPreferrencesDiv );
    }
}
