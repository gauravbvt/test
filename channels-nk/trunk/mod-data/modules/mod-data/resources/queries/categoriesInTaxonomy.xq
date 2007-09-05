(: All categories in a taxonomy 							:)
(: Variables:			taxonomy -- the name of a taxonomy	:)

<list>
	{
		 for 
		 	$c in collection('__MODEL__')/category
		 where 
		 	$c/@taxonomy = $taxonomy
		 order by $c/name
		 return
		 	<category>
		 		<id>{$c/id/text()}</id>
		 		<name>{$c/name/text()}</name>
		 	</category>
	}
</list>