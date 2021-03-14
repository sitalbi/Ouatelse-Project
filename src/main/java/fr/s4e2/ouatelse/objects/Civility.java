package fr.s4e2.ouatelse.objects;

import org.apache.commons.lang3.text.WordUtils;

/**
 * The Civilility enumeration is used to determine the gender of a person
 */
public enum Civility {
    M,
    MME,
    AUTRE;

    /**
     * Allows to capitalize the Civility and replace "_" by blanks
     *
     * @return the Converted Name
     */
    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name()) + ".";
    }
}
