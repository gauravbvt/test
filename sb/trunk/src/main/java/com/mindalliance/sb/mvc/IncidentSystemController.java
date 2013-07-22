package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.IncidentSystem;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/incidentsystems")
@Controller
@RooWebScaffold(path = "lists/incidentsystems", formBackingObject = IncidentSystem.class)
public class IncidentSystemController {
}
