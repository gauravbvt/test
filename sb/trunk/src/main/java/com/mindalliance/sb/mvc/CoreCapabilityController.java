package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.CoreCapability;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/corecapabilitys")
@Controller
@RooWebScaffold(path = "lists/corecapabilitys", formBackingObject = CoreCapability.class)
public class CoreCapabilityController {
}
