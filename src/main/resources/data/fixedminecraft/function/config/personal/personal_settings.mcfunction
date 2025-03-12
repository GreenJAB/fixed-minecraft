scoreboard players reset @s betterr.config
advancement revoke @s only betterr:triggers/config_reset

playsound minecraft:ui.button.click master @s ~ ~ ~ 10

tellraw @s ["",{"text":"\nBetter Recipes Personal Config","bold":true,"color":"gold"},{"text":"\n"},{"text":"--------------------------------------","color":"gold"}]

execute if score @s betterr.config.advancements matches 1 run tellraw @s ["",{"text":"Enable Advancements to view recipes? ","color": "gray"},{"text":"[\u274c]","color":"dark_red","clickEvent":{"action":"run_command","value":"/function betterr:config/personal/toggle_advancements_on"},"hoverEvent": {"action": "show_text","contents": ["",{"text":"Enabling this will allow you to view all the recipes installed by pressing ["},{"keybind":"key.advancements"},{"text": "]."}]}}]
execute if score @s betterr.config.advancements matches 2 run tellraw @s ["",{"text":"Enable Advancements to view recipes? ","color": "gray"},{"text":"[\u2714]","color":"dark_green","clickEvent":{"action":"run_command","value":"/function betterr:config/personal/toggle_advancements_off"},"hoverEvent": {"action": "show_text","contents": ["",{"text":"Enabling this will allow you to view all the recipes installed by pressing ["},{"keybind":"key.advancements"},{"text": "]."}]}}]

execute if score @s betterr.config.recipes matches 1 run tellraw @s ["",{"text":"Give all recipes? ","color": "gray"},{"text":"[\u274c]","color":"dark_red","clickEvent":{"action":"run_command","value":"/function betterr:config/personal/toggle_give_all_recipes_on"},"hoverEvent": {"action": "show_text","contents": {"text":"Enabling this give you access to all the recipes in your recipe book. NOTE: Disabling this will remove ALL recipes you have unlocked, meaning you will have no recipes in your recipe book at all. Be careful!"}}}]
execute if score @s betterr.config.recipes matches 2 run tellraw @s ["",{"text":"Give all recipes? ","color": "gray"},{"text":"[\u2714]","color":"dark_green","clickEvent":{"action":"run_command","value":"/function betterr:config/personal/toggle_give_all_recipes_off"},"hoverEvent": {"action": "show_text","contents": {"text":"Enabling this give you access to all the recipes in your recipe book. NOTE: Disabling this will remove ALL recipes you have unlocked, meaning you will have no recipes in your recipe book at all. Be careful!"}}}]

tellraw @s {"text":"--------------------------------------","color":"gold"}