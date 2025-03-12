tellraw @a {"text":"Better Crafting Recipes Has Loaded Succesfully!","bold":true,"color":"gold","type":"text"}
scoreboard objectives add betterr.config trigger
scoreboard objectives add betterr.config.advancements dummy
scoreboard objectives add betterr.config.recipes dummy
execute unless score $Advancements betterr.config matches 1.. run scoreboard players set $Advancements betterr.config 1
execute unless score $Recipes betterr.config matches 1.. run scoreboard players set $Recipes betterr.config 1