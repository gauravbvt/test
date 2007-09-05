(: All disciplines referenced in a taxonomy 			:)
(: Variables:	taxonomy -- the name of a taxonomy	:)

<list>
	{
		 let $cats := collection('__MODEL__')/category[@taxonomy = $taxonomy]
		 for 
		 		$id in distinct-values($cats/disciplines/categoryId),
		 		$d in collection('__MODEL__')/category[@taxonomy="discipline"]
		 where $d/id = $id  
		 order by $d/name
		 return
		 	<discipline>
		 		<id>{$d/id/text()}</id>
		 		<name>{$d/name/text()}</name>
		 	</discipline>
	}
</list>