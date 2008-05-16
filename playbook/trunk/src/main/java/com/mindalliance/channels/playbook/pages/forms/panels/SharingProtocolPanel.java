package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 12:48:11 PM
 */
public class SharingProtocolPanel extends AbstractComponentPanel {

    protected DropDownChoice deliveryChoice;
    protected RefPreferencesPanel mediaPreferrences;
    protected TimingPanel maxDelayPanel;
    protected InformationTemplatePanel infoTemplatePanel;

    public SharingProtocolPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        deliveryChoice = new DropDownChoice("delivery", new RefPropertyModel(getElement(), propPath + "delivery", SharingAgreement.getDeliveries()));
        deliveryChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String chosenDelivery = deliveryChoice.getModelObjectAsString();
                RefUtils.set(getElement(), propPath + "delivery", chosenDelivery);
            }
        });
        addReplaceable(deliveryChoice);
        mediaPreferrences = new RefPreferencesPanel("preferredMedia", this, propPath + "preferredMedia", isReadOnly(), feedback,
                                                   new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType")));
        addReplaceable(mediaPreferrences);
        infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this, propPath+"informationTemplate",
                                                          isReadOnly(), feedback);
        addReplaceable(infoTemplatePanel);
    }
}
