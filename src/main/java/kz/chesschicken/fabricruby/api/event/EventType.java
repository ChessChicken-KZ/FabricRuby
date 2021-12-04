package kz.chesschicken.fabricruby.api.event;

public enum EventType {
    PREINIT,
    INIT,
    POSTINIT,
    BLOCKINIT,
    ITEMINIT;

    public static EventType getByName(String s) {
        return EventType.valueOf(s.toUpperCase().replace("@event", ""));
    }
}
