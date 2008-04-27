package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.Location;
import com.mindalliance.channels.playbook.ifm.info.AreaInfo;
import com.mindalliance.channels.playbook.geo.Area;
import com.mindalliance.channels.playbook.geo.GeoService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.string.Strings;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 8:55:01 PM
 */
public class AreaInfoPanel extends AbstractComponentPanel {

    public AreaInfoPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    AreaInfo areaInfo;
    WebMarkupContainer editableDiv;
    WebMarkupContainer readOnlyDiv;
    AutoCompleteTextField countryField;
    AutoCompleteTextField stateField;
    AutoCompleteTextField countyField;
    AutoCompleteTextField cityField;
    TextField streetField;
    TextField codeField;
    FeedbackPanel feedback;

    protected void init() {
        super.init();
        // Do amything else?
    }

    protected void load() {
        super.load();
        areaInfo = (AreaInfo)RefUtils.get(element, propPath);
        editableDiv = new WebMarkupContainer("editable");
        addReplaceable(editableDiv);
        readOnlyDiv = new WebMarkupContainer("readOnly");
        addReplaceable(readOnlyDiv);
        loadReadOnly();
        loadEditable();
        if (isReadOnly()) {
            editableDiv.setVisible(false);
        }
        else {
            readOnlyDiv.setVisible(false);
        }
    }

    private void loadReadOnly() {
        Label areaInfoLabel = new Label("areaInfoString", areaInfo.toString());
        readOnlyDiv.add(areaInfoLabel);
    }

    private void loadEditable() {
        feedback = new FeedbackPanel("areaInfoFeedback", IFeedbackMessageFilter.ALL);
        // Fields
        countryField = new AutoCompleteTextField("country", new RefPropertyModel(element, propPath +".country")) {
            protected Iterator getChoices(String input) {
                return countryIterator(input, 10);
            }
        };
        stateField = new AutoCompleteTextField("state", new RefPropertyModel(element, propPath +".state")) {
            protected Iterator getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                String countryName = (String) RefUtils.get(element,  propPath +".country");
                return stateIterator(input, countryName,  10);
            }

        };
        countyField = new AutoCompleteTextField("county", new RefPropertyModel(element, propPath +".county")) {
            protected Iterator getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                // String stateName = (String)stateField.getModel().getObject();
                String countryName = (String)RefUtils.get(element,  propPath +".country");
                String stateName = (String)RefUtils.get(element,  propPath +".state");
                return countyIterator(input, countryName, stateName,  10);
            }

        };
        cityField = new AutoCompleteTextField("city", new RefPropertyModel(element, propPath +".city")) {
            protected Iterator getChoices(String input) {
                String countryName = (String)countryField.getModel().getObject();
                String stateName = (String)stateField.getModel().getObject();
                String countyName = (String)countyField.getModel().getObject();
                return cityIterator(input, countryName, stateName, countyName, 10);
            }
        };
        streetField = new TextField("street", new RefPropertyModel(element, propPath +".street"));
        streetField.setPersistent(false);
        codeField = new TextField("code", new RefPropertyModel(element, propPath +".code"));
        codeField.setPersistent(false);
       // Ajax
       // Country field
       countryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countryName = (String)countryField.getModel().getObject();
                // System.out.println("Updating country to " + countryName);
                RefUtils.set(element,propPath + ".state", "");
                RefUtils.set(element,propPath + ".county", "");
                RefUtils.set(element,propPath + ".city", "");
                RefUtils.set(element,propPath + ".street", "");
                RefUtils.set(element,propPath + ".code", "");
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(stateField);
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        // countryField.add(GeoValidator.countryExists());
        editableDiv.add(countryField);
        // State field
        stateField.setOutputMarkupId(true);
        stateField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String stateName = stateField..getModel().getObject();
                // System.out.println("Updating state to " + stateName);
                RefUtils.set(element,propPath + ".county", "");
                RefUtils.set(element,propPath + ".city", "");
                RefUtils.set(element,propPath + ".street", "");
                RefUtils.set(element,propPath + ".code", "");
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        stateField.add(GeoValidator.stateExists());
        editableDiv.add(stateField);
        // County field
        countyField.setOutputMarkupId(true);
        countyField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countyName = countyField..getModel().getObject();
                // System.out.println("Updating county to " + countyName);
                RefUtils.set(element,propPath + ".city", "");
                RefUtils.set(element,propPath + ".street", "");
                RefUtils.set(element,propPath + ".code", "");
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(countyField);
        // City field
        cityField.setOutputMarkupId(true);
        cityField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String cityName = cityField.getModel().getObject();
                // System.out.println("Updating city to " + cityName);
                RefUtils.set(element,propPath + ".street", "");
                RefUtils.set(element,propPath + ".code", "");
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(cityField);
        // Street field
        streetField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                // System.out.println("Updating street to " + streetName);
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(streetField);
        // Code field
        codeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                 // System.out.println("Updating street to " + streetName);
                elementChanged();
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(codeField);
        // Feedback
        feedback.setOutputMarkupId(true);
        editableDiv.add(feedback);
        editableDiv.add(new AjaxLink("verify", new RefPropertyModel(element, propPath)) {
            public void onClick(AjaxRequestTarget target) {
                try {
                   AreaInfo areaInfo = (AreaInfo)getModelObject();
                   Area area = areaInfo.getArea(false);  // don't cache it (won't serialize)
                   if (area.isUnknown()) {
                       error("Unknown location");
                   }
                    else if (area.isAmbiguous()) {
                       error("Ambiguous location");
                   }
                    else {
                       info("Known location");
                       if (isValidCode(areaInfo)) {
                          info("Code is valid");
                       }
                       else {
                          error("Code not validated");
                       }
                   }
                }
                catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).warn("Could not verify location due to system error", e);
                    error("Could not verify location due to system error");
                }
                target.addComponent(feedback);
            }
        });
    }

    private boolean isValidCode(AreaInfo areaInfo) {
        String code = areaInfo.getCode();
        if (code != null && code.length() > 0) {
           return GeoService.validateCode(code, areaInfo.getCountry(), areaInfo.getState(), areaInfo.getCounty(), areaInfo.getCity());
        }
        else {
            return true;
        }
    }

    private Iterator countryIterator(String input, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCountryNames(input, max);
        return choices.iterator();
    }

    private Iterator stateIterator(String input, String countryName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateStateNames(input, countryName, max);
        return choices.iterator();
    }

    private Iterator countyIterator(String input, String countryName, String stateName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCountyNames(input, countryName, stateName, max);
        return choices.iterator();
    }

    private Iterator cityIterator(String input, String countryName, String stateName, String countyName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCityNames(input, countryName, stateName, countyName, max);
        return choices.iterator();
    }
}
