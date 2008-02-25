channels_NS("channels.modeler.finder");

channels.modeler.finder.initialize = function(scope) {
    jQuery("#" + scope).find("#finder_grid").jqGrid({
        datatype: "local",
        height: 250,
        colNames:['ID','Name','Description'],
        colModel:[
            {name:'id',index:'id', width:10, sorttype:"int"},
            {name:'name',index:'name', width:100},
            {name:'description',index:'description', width:200, align:"right",sorttype:"float"}
        ],
        imgpath: 'images'
    });

//    var mydata = [
//        {id:"1",invdate:"2007-10-01",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
//        {id:"2",invdate:"2007-10-02",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
//        {id:"3",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
//        {id:"4",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
//        {id:"5",invdate:"2007-10-05",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
//        {id:"6",invdate:"2007-09-06",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"},
//        {id:"7",invdate:"2007-10-04",name:"test",note:"note",amount:"200.00",tax:"10.00",total:"210.00"},
//        {id:"8",invdate:"2007-10-03",name:"test2",note:"note2",amount:"300.00",tax:"20.00",total:"320.00"},
//        {id:"9",invdate:"2007-09-01",name:"test3",note:"note3",amount:"400.00",tax:"30.00",total:"430.00"}
//    ];
//    for(var i=0;i<=mydata.length;i++)
//        jQuery("#" + scope).find("#finder_grid").addRowData(i+1,mydata[i]);
}