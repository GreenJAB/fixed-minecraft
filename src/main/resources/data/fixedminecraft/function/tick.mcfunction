execute as @a unless score @s betterr.config.advancements matches 1.. run scoreboard players set @s betterr.config.advancements 1
execute as @a unless score @s betterr.config.recipes matches 1.. run scoreboard players set @s betterr.config.recipes 1
execute as @a[scores={betterr.config=1..}] run function betterr:config/personal/personal_settings
execute as @a[scores={betterr.config.advancements=1},advancements={betterr:info/root=true}] run advancement revoke @s through betterr:info/root
execute as @a[scores={betterr.config.advancements=2},advancements={betterr:info/root=false}] run advancement grant @s through betterr:info/root
execute as @a[scores={betterr.config.recipes=1}] run advancement grant @s only betterr:triggers/take_recipes
execute as @a[scores={betterr.config.recipes=2}] run advancement grant @s only betterr:triggers/give_recipes