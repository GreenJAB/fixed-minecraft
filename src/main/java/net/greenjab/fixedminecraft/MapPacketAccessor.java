package net.greenjab.fixedminecraft;

public interface MapPacketAccessor {
    void fixedminecraft$setX(int x);
    void fixedminecraft$setZ(int z);

    int fixedminecraft$readX();
    int fixedminecraft$readZ();
}
