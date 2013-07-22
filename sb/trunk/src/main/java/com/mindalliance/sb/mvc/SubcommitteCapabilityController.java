package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.SubcommitteeCapability;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/subcommitteecapabilitys")
@Controller
@RooWebScaffold(path = "lists/subcommitteecapabilitys", formBackingObject = SubcommitteeCapability.class)
public class SubcommitteCapabilityController {
}
