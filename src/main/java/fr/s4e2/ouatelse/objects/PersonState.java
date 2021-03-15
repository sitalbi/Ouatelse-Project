package fr.s4e2.ouatelse.objects;

import org.apache.commons.lang3.text.WordUtils;

/**
 * The PersonState enumeration lists if a person is employed or not
 */
public enum PersonState {
    EMPLOYED,
    UNEMPLOYED;

    /**
<<<<<<< HEAD
     * Allows to capitalize the first letter of the first name and last name and replace "_" by blanks
=======
     * Allows to capitalize the Person State and replace "_" by blanks
>>>>>>> productsManagement
     *
     * @return the converted name
     */
    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().replace("_", " "));
    }
}
