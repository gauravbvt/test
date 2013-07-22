package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.OrganizationCapability;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/organizationcapabilitys")
@Controller
@RooWebScaffold(path = "lists/organizationcapabilitys", formBackingObject = OrganizationCapability.class)
public class OrganizationCapabilityController {
}
