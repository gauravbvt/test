package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Organization;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/organizations")
@Controller
@RooWebScaffold(path = "organizations", formBackingObject = Organization.class, delete = false, create = false)
public class OrganizationController {
}
