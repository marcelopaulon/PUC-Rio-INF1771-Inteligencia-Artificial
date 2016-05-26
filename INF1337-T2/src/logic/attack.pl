shoot(ShootingActionResult) :- 
			     curPosition(X, Y, _), adjacent(X, Y, XX, YY), hasEnemy(XX, YY),
			     ShootingActionResult = attack,
			     random_between(20, 50, EnemyDamage),
				 (
				 	(enemyCell(EnemyDamage, EnemyEnergy, XX, YY),
				 		EnemyEnergyAfter is EnemyEnergy - EnemyDamage,
				 		(
				 			(EnemyEnergyAfter < 1, retract(enemyCell(_,_, XX, YY)), assert(floorCell(XX, YY)), ShootingActionResult = kill);
				 			(retract(enemyCell(_,_, XX, YY)), assert(enemyCell(EnemyDamage, EnemyEnergyAfter, XX, YY)))
				 		)
				 	)
				 ),
				 decrementCost(10),
				 deployAmmo(), !.
