package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Document;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/documents")
@Controller
@RooWebScaffold(path = "lists/documents", formBackingObject = Document.class)
public class DocumentController {
}
