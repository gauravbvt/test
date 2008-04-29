package com.mindalliance.channels.playbook.pages.forms.tabs.person;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 3:26:52 PM
 */
public class PersonResponsibilitiesTab  extends ResourceResponsibilitiesTab {

    RefreshingView positionsView;
    Label positionLabel;

    public PersonResponsibilitiesTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
       /* positionLabel = new Label("positionDescription", new Model(""));
        positionsView = new RefreshingView("positions", new RefPropertyModel(element, "positions")) {
            protected Iterator getItemModels() {
                List<Ref> positions = (List<Ref>) getModelObject();
                return new ModelIteratorAdapter(positions.iterator()) {
                    protected IModel model(Object position) {
                        return new RefModel(position);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref position = (Ref) item.getModelObject();
                final Label positionNameLabel = new Label("positionName", new RefPropertyModel(position, "name"));
                AjaxLink positionLink = new AjaxLink("positionLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        String description = (String) RefUtils.get(position, "description");
                        positionLabel.setModelObject(description);
                        target.addComponent(positionLabel);
                    }
                };
                positionLink.add(positionNameLabel);
                item.add(positionLink);
            }
        };
        addReplaceable(positionsView);
        addReplaceable(positionLabel);*/
    }
}
