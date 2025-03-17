package net.greenjab.fixedminecraft.registry.item.map_book;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;

public class MapStateData {
    public MapIdComponent id;
    public MapState mapState;
    public MapStateData(MapIdComponent id, MapState mapState){
        this.id = id;
        this.mapState = mapState;
    }
}
