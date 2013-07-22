package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.PlanFile;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/planfiles")
@Controller
@RooWebScaffold(path = "lists/planfiles", formBackingObject = PlanFile.class)
public class PlanFileController {
}
