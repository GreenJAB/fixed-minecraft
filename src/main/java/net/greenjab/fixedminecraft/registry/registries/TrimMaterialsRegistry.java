package net.greenjab.fixedminecraft.registry.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.ArmorType;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.ramixin.mixson.Mixson;
import net.ramixin.mixson.enums.ErrorPolicy;
import net.ramixin.mixson.enums.Lifetime;
import net.ramixin.mixson.util.Index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrimMaterialsRegistry {

    public static final ResourceKey<TrimMaterial> COAL = registryKey("coal");

    public static final MaterialAssetGroup COAL_ASSET = create("coal");
    public static MaterialAssetGroup create(final String base) {
        return new MaterialAssetGroup(new MaterialAssetGroup.AssetInfo(base), Map.of());
    }

    private static ResourceKey<TrimMaterial> registryKey(final String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, FixedMinecraft.id(id));
    }
    public static void registerTrimMaterials() {
        System.out.println("register TrimMaterials");
    }




    private static final List<Identifier> ARMOR_MATERIALS = List.of(
            Identifier.withDefaultNamespace("leather"),
            Identifier.withDefaultNamespace("chainmail"),
            Identifier.withDefaultNamespace("iron"),
            Identifier.withDefaultNamespace("golden"),
            Identifier.withDefaultNamespace("diamond"),
            Identifier.withDefaultNamespace("netherite"),
            Identifier.withDefaultNamespace("copper")
    );

    private static final List<Identifier> DAMAGABLE_ARMOR_MATERIALS = List.of(
            Identifier.withDefaultNamespace("netherite"),
            Identifier.withDefaultNamespace("copper")
    );

    static {
        Arrays.stream(ArmorType.values()).filter(type -> type != ArmorType.BODY)
                .forEach(type -> ARMOR_MATERIALS
                        .forEach(material -> registerItemModelTrimModification(type.getName(), material)));

        registerItemModelTrimModification("helmet", Identifier.withDefaultNamespace("turtle"));

        registerTrimMaterialsToAtlas("armor_trims");
        registerTrimMaterialsToAtlas("items");

        Arrays.stream(ArmorType.values()).filter(type -> type != ArmorType.BODY)
                .forEach(type -> DAMAGABLE_ARMOR_MATERIALS
                        .forEach(material -> registerItemModelDamageModification(type.getName(), material)));


    }

    private static void registerItemModelTrimModification(String armorPieceType, Identifier armorMaterial) {
        Mixson.registerEvent(
                1,
                Lifetime.PERSISTENT,
                ErrorPolicy.LOG,
                FixedMinecraft.id("add_trims_to_" + armorMaterial.getPath() + "_" + armorPieceType).toString(),
                index -> index.idEquals(new Index(Identifier.fromNamespaceAndPath(armorMaterial.getNamespace(), "items/" + armorMaterial.getPath() + "_" + armorPieceType))),
                context -> {
                    JsonObject root = context.getFile().getAsJsonObject();
                    if (root == null || !root.has("model")) return;

                    JsonObject model = root.getAsJsonObject("model");
                    if (model == null) return;

                    if (!model.has("type") || !model.get("type").getAsString().equals("minecraft:select")) return;

                    JsonArray cases = model.getAsJsonArray("cases");
                    if (cases == null || cases.isEmpty()) return;

                    JsonObject newCase = new JsonObject();

                    JsonObject caseModel = new JsonObject();
                    caseModel.addProperty("type", "minecraft:model");
                    caseModel.addProperty("model", FixedMinecraft.id("item/" + armorMaterial.getPath() + "_" + armorPieceType + "_" + TrimMaterialsRegistry.COAL.identifier().getPath() + "_trim").toString());
                    if (armorMaterial.toString().toLowerCase().contains("leather")) {
                        JsonArray c = new JsonArray();
                        JsonObject tint = new JsonObject();
                        tint.addProperty("type", "minecraft:dye");
                        tint.addProperty("default",  -6265536);
                        c.add(tint);
                        caseModel.add("tints", c);
                    }
                    newCase.add("model", caseModel);
                    newCase.addProperty("when", TrimMaterialsRegistry.COAL.identifier().toString());
                    cases.add(newCase);
                }
        );
    }

    private static void registerTrimMaterialsToAtlas(String atlasName) {
        Mixson.registerEvent(
                1,
                Lifetime.PERSISTENT,
                ErrorPolicy.LOG,
                FixedMinecraft.id("add_trim_materials_to_" + atlasName + "_atlas").toString(),
                index -> index.idEquals(new Index( Identifier.withDefaultNamespace("atlases/" + atlasName))),
                context -> {
                    JsonObject root = context.getFile().getAsJsonObject();
                    if (root == null || !root.has("sources")) return;

                    JsonArray sources = root.getAsJsonArray("sources");
                    if (sources == null || sources.isEmpty()) return;

                    for (JsonElement element : sources) {
                        if (!element.isJsonObject()) continue;

                        JsonObject object = element.getAsJsonObject();
                        if (object == null || !object.has("type")) return;

                        String type = object.get("type").getAsString();

                        if ("paletted_permutations".equals(type) || "minecraft:paletted_permutations".equals(type)) {
                            JsonObject permutations = object.getAsJsonObject("permutations");
                            if (permutations == null || permutations.isEmpty()) return;

                            String trimName = TrimMaterialsRegistry.COAL.identifier().getPath();
                            permutations.addProperty(trimName, FixedMinecraft.id("trims/color_palettes/" + trimName).toString());

                            break;
                        }
                    }
                }
        );
    }

    private static void registerItemModelDamageModification(String armorPieceType, Identifier armorMaterial) {
        Mixson.registerEvent(
                1000,
                Lifetime.PERSISTENT,
                ErrorPolicy.LOG,
                FixedMinecraft.id("add_damage_variant_to_" + armorMaterial.getPath() + "_" + armorPieceType).toString(),
                index -> index.idEquals(new Index(Identifier.fromNamespaceAndPath(armorMaterial.getNamespace(), "items/" + armorMaterial.getPath() + "_" + armorPieceType))),
                context -> {
                    JsonObject root = context.getFile().getAsJsonObject();
                    if (root == null || !root.has("model")) return;

                    JsonObject model = root.getAsJsonObject("model");
                    if (model == null) return;

                    if (!model.has("type") || !model.get("type").getAsString().equals("minecraft:select")) return;

                    JsonArray cases = model.getAsJsonArray("cases");
                    if (cases == null || cases.isEmpty()) return;

                    Map<String, List<Pair<Float, String>>> damages = Map.of("netherite",List.of(Pair.of(0f, ""),Pair.of(0.997f, "_broken")),
                            "copper",List.of(Pair.of(0f, ""),Pair.of(0.25f, "_exposed"),Pair.of(0.5f, "_weathered"),Pair.of(0.75f, "_oxidized")));

                    for (JsonElement e : cases.asList()) {
                        JsonObject e2 = e.getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : e2.entrySet()) {
                            if (entry.getValue().isJsonObject()) {
                                JsonObject A = entry.getValue().getAsJsonObject();
                                if (A != null && A.has("type") || A.get("type").getAsString().equals("minecraft:model")) {
                                    String B = A.get("model").getAsString();
                                    A.remove("type");
                                    A.remove("model");
                                    A.addProperty("type", "minecraft:range_dispatch");
                                    A.addProperty("property", "minecraft:damage");
                                    JsonArray array = new JsonArray();
                                    for (Pair<Float, String> damage : damages.get(armorMaterial.getPath())) {
                                        JsonObject threshold = new JsonObject();
                                        threshold.addProperty("threshold", damage.getFirst());
                                        JsonObject damageState = new JsonObject();
                                        damageState.addProperty("type", "minecraft:model");
                                        damageState.addProperty("model", B + damage.getSecond());
                                        threshold.add("model", damageState);
                                        array.add(threshold);
                                    }
                                    A.add("entries", array);
                                }
                            }
                        }
                    }

                    JsonObject fallback = model.getAsJsonObject("fallback");
                    if (fallback != null && !fallback.isEmpty()) {
                        if (fallback.has("type") || fallback.get("type").getAsString().equals("minecraft:model")) {
                            String B = fallback.get("model").getAsString();
                            fallback.remove("type");
                            fallback.remove("model");
                            fallback.addProperty("type", "minecraft:range_dispatch");
                            fallback.addProperty("property", "minecraft:damage");
                            JsonArray array = new JsonArray();
                            for (Pair<Float, String> damage : damages.get(armorMaterial.getPath())) {
                                JsonObject threshold = new JsonObject();
                                threshold.addProperty("threshold", damage.getFirst());
                                JsonObject damageState = new JsonObject();
                                damageState.addProperty("type", "minecraft:model");
                                damageState.addProperty("model", B + damage.getSecond());
                                threshold.add("model", damageState);
                                array.add(threshold);
                            }
                            fallback.add("entries", array);
                        }
                    }
                }
        );
    }

}
