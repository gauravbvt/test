package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.IncidentCapability;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/incidentcapabilitys")
@Controller
@RooWebScaffold(path = "lists/incidentcapabilitys", formBackingObject = IncidentCapability.class)
public class IncidentCapabilityController {
}
