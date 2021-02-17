package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.screens.ManagementRoleScreen;
import fr.s4e2.ouatelse.screens.ManagementStoreScreen;

public class MainController {

    public void onRolesMenuClick() {
        new ManagementRoleScreen().open();
    }

    public void onStoresMenuClick() {
       new ManagementStoreScreen().open();
    }
}
