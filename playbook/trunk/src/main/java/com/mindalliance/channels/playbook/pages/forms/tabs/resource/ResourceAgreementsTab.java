package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 12:45:52 PM
 */
public class ResourceAgreementsTab extends AbstractFormTab {

    protected RefreshingView whereSourceView;
    protected RefreshingView whereRecipientView;

    public ResourceAgreementsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        whereSourceView = new RefreshingView("agreementsWhereSource",
                                              new RefQueryModel(getScope(),
                                                                new Query("findAgreementsWhereSource", getElement()))) {
            protected Iterator getItemModels() {
                List<Ref> agreements = (List<Ref>) whereSourceView.getModelObject();
                return new ModelIteratorAdapter(agreements.iterator()) {
                     protected IModel model(Object agreement) {
                         return new RefModel(agreement);
                     }
                 };
            }

            protected void populateItem(Item item) {
                final Ref agreement = (Ref)item.getModelObject();
                AjaxLink whereSourceLink = new AjaxLink("whereSourceLink") {
                    public void onClick(AjaxRequestTarget target) {
                         edit(agreement, target);
                    }
                };
                Label whereSourceString = new Label("whereSourceString", (String)RefUtils.get(agreement, "name"));
                whereSourceLink.add(whereSourceString);
                item.add(whereSourceLink);
            }
        };
        addReplaceable(whereSourceView);
        whereRecipientView = new RefreshingView("agreementsWhereRecipient",
                                               new RefQueryModel(getScope(),
                                                                 new Query("findAgreementsWhereRecipient", getElement()))) {
             protected Iterator getItemModels() {
                 List<Ref> agreements = (List<Ref>) whereRecipientView.getModelObject();
                 return new ModelIteratorAdapter(agreements.iterator()) {
                      protected IModel model(Object agreement) {
                          return new RefModel(agreement);
                      }
                  };
             }

             protected void populateItem(Item item) {
                 final Ref agreement = (Ref)item.getModelObject();
                 AjaxLink whereRecipientLink = new AjaxLink("whereRecipientLink") {
                     public void onClick(AjaxRequestTarget target) {
                          edit(agreement, target);
                     }
                 };
                 Label whereRecipientString = new Label("whereRecipientString", (String)RefUtils.get(agreement, "name"));
                 whereRecipientLink.add(whereRecipientString);
                 item.add(whereRecipientLink);
             }
         };
         addReplaceable(whereRecipientView);
    }

}
