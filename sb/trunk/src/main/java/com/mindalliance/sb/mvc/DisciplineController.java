package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Discipline;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/disciplines")
@Controller
@RooWebScaffold(path = "lists/disciplines", formBackingObject = Discipline.class)
public class DisciplineController {
}
