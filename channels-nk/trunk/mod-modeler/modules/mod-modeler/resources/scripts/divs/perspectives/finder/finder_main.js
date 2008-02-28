(function($) {

    $.channels.modeler.finder = function(perspective) {

        var finder = $('#' + perspective + ' .finder');
        finder.grid = {
            setup : function() {

                var grid = jQuery("#" + perspective + "_finder_grid")
                grid.jqGrid({
                    datatype: "local",
                    colNames:['Inv No','Date', 'Client', 'Amount','Tax','Total','Notes'],
                    colModel:[
                        {name:'id',index:'id', width:60, sorttype:"int"},
                        {name:'invdate',index:'invdate', width:90, sorttype:"date"},
                        {name:'name',index:'name', width:100},
                        {name:'amount',index:'amount', width:80, align:"right",sorttype:"float"},
                        {name:'tax',index:'tax', width:80, align:"right",sorttype:"float"},
                        {name:'total',index:'total', width:80,align:"right",sorttype:"float"},
                        {name:'note',index:'note', width:100, sortable:false}
                    ],
                    imgpath: 'support/scripts/libs/jqGrid'
                });

                var mydata = [
                {id:"1",invdate:"2007-10-01",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
                {id:"2",invdate:"2007-10-02",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
                {id:"3",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
                {id:"4",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
                {id:"5",invdate:"2007-10-05",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
                {id:"6",invdate:"2007-09-06",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
                {id:"7",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
                {id:"8",invdate:"2007-10-03",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
                {id:"9",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"}
                ];
                for(var i=0;i<=mydata.length;i++)
                    grid.addRowData(perspective+"_row_"+(i+1),mydata[i]);

                // Big hack:  manual fixup of generated table...
                $( "tbody:gt(0)", grid ).remove();
                $( "tbody tr:first", grid ).remove();
                $( "tbody tr:first", grid ).removeAttr( "style" );
                $( "#" + perspective + " div.finder_main > div > div " ).removeAttr( "style" );

            }

        }
        return finder;
    }
})(jQuery);