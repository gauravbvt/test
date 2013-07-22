package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.OrganizationIncident;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/organizationincidents")
@Controller
@RooWebScaffold(path = "lists/organizationincidents", formBackingObject = OrganizationIncident.class)
public class OrganizationIncidentController {
}
