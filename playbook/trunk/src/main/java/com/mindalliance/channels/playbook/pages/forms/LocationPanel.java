package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

import java.util.*;

import com.mindalliance.channels.playbook.ifm.Location;
import com.mindalliance.channels.playbook.geo.GeoService;
import com.mindalliance.channels.playbook.geo.Area;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2008
 * Time: 3:26:36 PM
 */
public class LocationPanel extends Panel {

    Ref located;
    String propName;
    WebMarkupContainer div;
    AutoCompleteTextField countryField;
    AutoCompleteTextField stateField;
    AutoCompleteTextField countyField;
    AutoCompleteTextField cityField;
    TextField streetField;
    TextField codeField;
    FeedbackPanel feedback;

    public LocationPanel(String id, Ref located, String propName) {
        super(id);    //To change body of overridden methods use File | Settings | File Templates.
        this.located = located;
        this.propName = propName;
        load();
    }

    private void load() {
        feedback = new FeedbackPanel("locationFeedback", IFeedbackMessageFilter.ALL);
        div = new WebMarkupContainer("location");
        add(div);
        // Fields
        countryField = new AutoCompleteTextField("country", new RefPropertyModel(located, propName +".country")) {
            protected Iterator getChoices(String input) {
                return countryIterator(input, 10);
            }
        };
        stateField = new AutoCompleteTextField("state", new RefPropertyModel(located, propName +".state")) {
            protected Iterator getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                String countryName = (String)RefUtils.get(located,  propName +".country");
                return stateIterator(input, countryName,  10);
            }

        };
        countyField = new AutoCompleteTextField("county", new RefPropertyModel(located, propName +".county")) {
            protected Iterator getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                // String stateName = (String)stateField.getModel().getObject();
                String countryName = (String)RefUtils.get(located,  propName +".country");
                String stateName = (String)RefUtils.get(located,  propName +".state");
                return countyIterator(input, countryName, stateName,  10);
            }

        };
        cityField = new AutoCompleteTextField("city", new RefPropertyModel(located, propName +".city")) {
            protected Iterator getChoices(String input) {
                String countryName = (String)countryField.getModel().getObject();
                String stateName = (String)stateField.getModel().getObject();
                String countyName = (String)countyField.getModel().getObject();
                return cityIterator(input, countryName, stateName, countyName, 10);
            }
        };
        streetField = new TextField("street", new RefPropertyModel(located, propName +".street"));
        streetField.setPersistent(false);
        codeField = new TextField("code", new RefPropertyModel(located, propName +".code"));
        codeField.setPersistent(false);
       // Ajax
       // Country field
       countryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countryName = (String)countryField.getModel().getObject();
                // System.out.println("Updating country to " + countryName);
                RefUtils.set(located,propName + ".state", "");
                RefUtils.set(located,propName + ".county", "");
                RefUtils.set(located,propName + ".city", "");
                RefUtils.set(located,propName + ".street", "");
                RefUtils.set(located,propName + ".code", "");
                located.changed(propName);
                // System.out.println("Location = " + located.deref(propName).toString());
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
        div.add(countryField);
        // State field
        stateField.setOutputMarkupId(true);
        stateField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String stateName = stateField..getModel().getObject();
                // System.out.println("Updating state to " + stateName);
                RefUtils.set(located,propName + ".county", "");
                RefUtils.set(located,propName + ".city", "");
                RefUtils.set(located,propName + ".street", "");
                RefUtils.set(located,propName + ".code", "");
                located.changed(propName);
                // System.out.println("Location = " + located.deref(propName).toString());
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        stateField.add(GeoValidator.stateExists());
        div.add(stateField);
        // County field
        countyField.setOutputMarkupId(true);
        countyField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countyName = countyField..getModel().getObject();
                // System.out.println("Updating county to " + countyName);
                RefUtils.set(located,propName + ".city", "");
                RefUtils.set(located,propName + ".street", "");
                RefUtils.set(located,propName + ".code", "");
                located.changed(propName);
                // System.out.println("Location = " + located.deref(propName).toString());
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(countyField);
        // City field
        cityField.setOutputMarkupId(true);
        cityField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String cityName = cityField.getModel().getObject();
                // System.out.println("Updating city to " + cityName);
                RefUtils.set(located,propName + ".street", "");
                RefUtils.set(located,propName + ".code", "");
                located.changed(propName);
                // System.out.println("Location = " + located.deref(propName).toString());
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(cityField);
        // Street field
        streetField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                // System.out.println("Updating street to " + streetName);
                located.changed(propName);
                System.out.println("Location = " + located.deref(propName).toString());
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(streetField);
        // Code field
        codeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                 // System.out.println("Updating street to " + streetName);
                located.changed(propName);
                // System.out.println("Location = " + located.deref(propName).toString());
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(codeField);
        // Feedback
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new AjaxLink("verify", new RefPropertyModel(located, propName)) {
            public void onClick(AjaxRequestTarget target) {
                try {
                   Location loc = (Location)getModelObject();
                   Area area = loc.getArea(false);  // don't cache it (won't serialize)
                   if (area.isUnknown()) {
                       error("Unknown location");
                   }
                    else if (area.isAmbiguous()) {
                       error("Ambiguous location");
                   }
                    else {
                       info("Known location");
                       if (isValidCode(loc)) {
                          info("Code is valid");
                       }
                       else {
                          error("Code not validated");
                       }
                   }
                }
                catch (Exception e) {
                    System.err.println(e); // TODO Log this
                    error("Could not verify location due to system error");
                }
                target.addComponent(feedback);
            }
        });
    }


    public void onDetach() {
        Location location = (Location)RefUtils.get(located, propName);
        location.detach();
        super.onDetach();
    }

    public void refresh(AjaxRequestTarget target) {
        target.addComponent(countryField);
        target.addComponent(stateField);
        target.addComponent(countyField);
        target.addComponent(cityField);
        target.addComponent(streetField);
        target.addComponent(codeField);
        feedback.setModel(new FeedbackMessagesModel(feedback));
        target.addComponent(feedback);
    }

    private boolean isValidCode(Location location) {
        String code = location.getCode();
        if (code != null && code.length() > 0) {
           return GeoService.validateCode(code, location.getCountry(), location.getState(), location.getCounty(), location.getCity());
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
