(: All categories in a taxonomy and a discipline 			:)
(: Variables:			taxonomy -- the name of a taxonomy	:)
(:							  disciplineId -- the id of a discipline :)

<list>
	{
		 for $c in collection('__MODEL__')/category[@taxonomy = $taxonomy]
		 where $c/disciplines/categoryId = $disciplineId
		 order by $c/name
		 return
		 	<category>
		 		<id>{$c/id/text()}</id>
		 		<name>{$c/name/text()}</name>
		 	</category>
	}
</list>