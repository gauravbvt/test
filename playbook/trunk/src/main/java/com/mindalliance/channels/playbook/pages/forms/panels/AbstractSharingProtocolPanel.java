package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement;
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 12:48:11 PM
 */
abstract public class AbstractSharingProtocolPanel extends AbstractComponentPanel {

    protected SharingProtocol sharingProtocol;
    protected DropDownChoice deliveryChoice;
    protected AgentSpecificationPanel contactsPanel;
    protected InformationDefinitionPanel infoSpecPanel;
    protected AjaxCheckBox anyMediumCheckBox;
    protected WebMarkupContainer mediaPreferrencesDiv;
    protected RefPreferencesPanel mediaPreferrences;

    public AbstractSharingProtocolPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        sharingProtocol = (SharingProtocol)getComponent();
        deliveryChoice = new DropDownChoice("delivery", new RefPropertyModel(getElement(), propPath + ".delivery"), sharingProtocol.getDeliveryChoices());
        deliveryChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String chosenDelivery = deliveryChoice.getModelObjectAsString();
                RefUtils.set(getElement(), propPath + ".delivery", chosenDelivery);
            }
        });
        addReplaceable(deliveryChoice);
        contactsPanel = new AgentSpecificationPanel("contacts", this, propPath+".contacts", isReadOnly(), feedback);
        addReplaceable(contactsPanel);
        infoSpecPanel = new InformationDefinitionPanel("informationSpec", this, propPath+".informationSpec",isReadOnly(), feedback);
        addReplaceable(infoSpecPanel);

        anyMediumCheckBox = new AjaxCheckBox("anyMedium", new Model((Boolean)sharingProtocol.getPreferredMediumTypes().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyMedium = (Boolean)anyMediumCheckBox.getModelObject();
                if (anyMedium) {
                    setProperty("preferredMediumTypes", new ArrayList<Ref>());
                    mediaPreferrences = new RefPreferencesPanel("preferredMedia", AbstractSharingProtocolPanel.this, propPath + ".preferredMediumTypes", isReadOnly(), feedback,
                                                   new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType")));
                    addReplaceableTo(mediaPreferrences, mediaPreferrencesDiv);
                }
                setVisibility(mediaPreferrencesDiv, !anyMedium, target);
            }
        };
        addReplaceable(anyMediumCheckBox);
        mediaPreferrencesDiv = new WebMarkupContainer("preferredMediaDiv");
        setVisibility(mediaPreferrencesDiv, !sharingProtocol.getPreferredMediumTypes().isEmpty());
        addReplaceable(mediaPreferrencesDiv);
        mediaPreferrences = new RefPreferencesPanel("preferredMedia", this, propPath + ".preferredMediumTypes", isReadOnly(), feedback,
                                                   new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType")));
        addReplaceableTo(mediaPreferrences, mediaPreferrencesDiv);
    }
}
