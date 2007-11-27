$.taconite.debug = true;

// To create a namespace
function analyst_NS(ns) { // HT to http://weblogs.asp.net/mschwarz/archive/2005/08/26/423699.aspx
 var nsParts = ns.split(".");
 var root = window;
 for(var i=0; i<nsParts.length; i++) {
  if(typeof root[nsParts[i]] == "undefined") {
   root[nsParts[i]] = new Object();
  }
  root = root[nsParts[i]];
 }
}

analyst_NS("analyst");

analyst.command = function(command, args) {
	uri = "/analyst/command/" + command;
	addedArg = false;
	// Build command
	for (arg in args) {
		if (addedArg) uri += "&"; else uri += "?";
		uri += arg + "=" + args[arg];
		addedArg = true;
	}
	// returns taconite command
	$.get(uri); 
}