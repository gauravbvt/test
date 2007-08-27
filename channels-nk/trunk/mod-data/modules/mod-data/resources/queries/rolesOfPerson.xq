(: All roles of a given person. 							:)

(: Variables:															  :)
(: 			personId -- the id of a person	:)

<list>
	{
	 for
		$p in collection('__MODEL__')/person
	 where 
	 	$p/id = $personId
	 return
	 	{
	 		for 
	 			$r in collection('__MODEL__')/role,
	 			$pr in $p/roles/roleId
	 		where
	 			$r/id = $pr
	 		return
			 	<role>
			 		<role>
			 			<id>{$r/id/text()}</id>
			 			<name>{$r/name/text()}</name>
			 		</role>
			 	</role>
		 	}
	}
</list>