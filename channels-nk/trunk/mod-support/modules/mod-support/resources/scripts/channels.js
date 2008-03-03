   // ******* NAMESPACE *********

    // To create a namespace
    function channels_NS(ns) { // HT to http://weblogs.asp.net/mschwarz/archive/2005/08/26/423699.aspx
     var nsParts = ns.split(".");
     var root = window;
     for(var i=0; i<nsParts.length; i++) {
      if(typeof root[nsParts[i]] == "undefined") {
       root[nsParts[i]] = new Object();
      }
      root = root[nsParts[i]];
     }
    }

    // ********** channels.taconite ************

    channels_NS("channels.taconite");
(function($) {

    // ***** GLOBALS ******

    $.taconite.debug = true;



    // Obtain and interpret Taconite command
    channels.taconite.command = function(uri, args) {
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
    
    $.channels = {};
})(jQuery);