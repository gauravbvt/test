(: Categories of an element 							:)

(: Variables:													  :)
(: 			elementId -- the id of the categorized element	:)

<list>
	{
		 for 
		 	$doc in collection('__MODEL__')
		 where 
		 	$doc/*/id = $elementId
		 let 
		 	$e := $doc/*
		 return
		 	{
			 	for 
			 		$ecid in $/categories/categoryId,
			 		$c in collection('__MODEL__')/category
			 	where
			 		$c/id = $ecid
			 	order by
			 		$c/name
			 	return
				 	<category>
				 		<id>{$c/id/text()}</id>
				 		<name>{$c/name/text()}</name>
				 	</category>
			}
	}
</list>