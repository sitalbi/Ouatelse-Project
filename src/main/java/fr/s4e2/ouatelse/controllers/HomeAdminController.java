package fr.s4e2.ouatelse.controllers;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeAdminController extends HomeController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void onUserManagementButtonClick() {
        //todo : open user management screen

        System.out.println("Open user management screen");
    }

    public void onRoleManagementClick() {
        //todo : open role management screen

        System.out.println("Open role management screen");
    }

    public void onParametersClick() {
        //todo : open parameters screen

        System.out.println("Open parameters screen");
    }
}
