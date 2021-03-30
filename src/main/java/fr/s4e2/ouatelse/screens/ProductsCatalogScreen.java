package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.Store;

public class ProductsCatalogScreen extends BaseScreen {
    public ProductsCatalogScreen(Store authentificationStore, Cart currentCart) {
        super("products_catalog.fxml", "Catalogue des produits", authentificationStore, currentCart);
    }
}
