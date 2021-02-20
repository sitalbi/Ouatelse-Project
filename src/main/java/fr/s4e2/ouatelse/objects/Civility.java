package fr.s4e2.ouatelse.objects;

import org.apache.commons.lang3.text.WordUtils;

public enum Civility {
    M,
    MME,
    AUTRE;

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name()) + ".";
    }
}
