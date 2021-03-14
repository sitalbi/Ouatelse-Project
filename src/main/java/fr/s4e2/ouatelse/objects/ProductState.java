package fr.s4e2.ouatelse.objects;

import org.apache.commons.lang3.text.WordUtils;

/**
 * The ProductState enumeration lists the possible states of a product
 */

public enum ProductState {
    IN_STOCK,
    OUT_OF_STOCK;

    /**
     * Allows to capitalize the first letter of the first name and last name and replace "_" by blanks
     *
     * @return the converted name
     */
    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().replace("_", " "));
    }
}
