package BLL;

import mainscript.quanlytiemnet.MainController;

public class MainControllerStatusManagement {
    private static MainController instance;

    public static void setMainController(MainController controller) {
        instance = controller;
    }

    public static MainController getMainController() {
        return instance;
    }
}
