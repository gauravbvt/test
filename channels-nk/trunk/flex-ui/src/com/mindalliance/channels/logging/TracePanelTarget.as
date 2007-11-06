package com.mindalliance.channels.logging
{

import mx.core.mx_internal;
import mx.controls.TextArea;
import mx.logging.targets.LineFormattedTarget;

use namespace mx_internal;

public class TracePanelTarget extends LineFormattedTarget 
{

    public function TracePanelTarget(console:TextArea) 
    {
    	super();
    	this.console = console;
    }
    
    private var console:TextArea;
 
	override mx_internal function internalLog(message:String):void
	{
		console.text += message + "\n";
	}
}
}