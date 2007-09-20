(: Categories of an element 							:)

(: Variables:													  :)
(: 			elementId -- the id of the categorized element	:)

<list>
	{
		 let $e := collection('__MODEL__')
		 for 
		 	$c  in collection('__MODEL__')/category
		 where 
		 	$e/*/id = $elementId and
		 	$c/id = $e/*/categories/categoryId
		 	order by
		 		$c/name
		 return
			 	<category>
			 		<id>{$c/id/text()}</id>
			 		<name>{$c/name/text()}</name>
			 	</category>
	}
</list>