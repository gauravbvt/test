package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.CriticalTask;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/criticaltasks")
@Controller
@RooWebScaffold(path = "lists/criticaltasks", formBackingObject = CriticalTask.class)
public class CriticalTaskController {
}
