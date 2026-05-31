package club.kid7.bannermaker.gui.common;

public enum SlotType {
    LISTED_ELEMENT('b'),
    NEXT_PAGE('n'),
    PREVIOUS_PAGE('p'),
    ALPHA_NUMERIC('a'),
    CREATE('c');

    private final char slotLetter;

    SlotType(char slotLetter) {
        this.slotLetter = slotLetter;
    }

    public char getSlotLetter() {
        return slotLetter;
    }
}
