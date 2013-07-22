package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Eoc;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/eocs")
@Controller
@RooWebScaffold(path = "lists/eocs", formBackingObject = Eoc.class)
public class EocController {
}
