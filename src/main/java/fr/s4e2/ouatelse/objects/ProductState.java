package fr.s4e2.ouatelse.objects;

import org.apache.commons.lang3.text.WordUtils;

public enum ProductState {
    IN_STOCK,
    OUT_OF_STOCK,
    WAITING_DELIVERY;

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().replace("_", " "));
    }
}
