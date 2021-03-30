package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerCart;
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.ClientStock;
import fr.s4e2.ouatelse.objects.Store;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the {@link fr.s4e2.ouatelse.screens.ManagementSalesScreen}
 */
public class StatisticsSalesController extends BaseController {
    @FXML
    private LineChart<String, Double> lineChart;

    private final EntityManagerCart entityManagerCart = Main.getDatabaseManager().getEntityManagerCart();
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private List<Cart> closedCarts;

    /**
     * Initializes the controller
     *
     * @param location  The location used to resolve relative paths for the root object,
     *                  or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or null if the location is not known.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        try {
            this.closedCarts = this.entityManagerCart.executeQuery(entityManagerCart.getQueryBuilder()
                    .where().eq("closed", true)
                    .prepare()
            );
        } catch (SQLException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
            this.closedCarts = new ArrayList<>();
        }

        // default load year chart
        this.loadGlobalYearChart();
    }

    /**
     * Handles the button click event for the global yearly sales button
     * <p>
     * Loads the global yearly sales chart.
     */
    public void onYearButtonClick() {
        this.loadGlobalYearChart();
    }

    /**
     * Handles the button click event for the global monthly sales button
     * <p>
     * Loads the global monthly sales chart.
     */
    public void onMonthButtonClick() {
        this.loadGlobalMonthChart();
    }

    /**
     * Handles the button click event for the global weekly sales button
     * <p>
     * Loads the global weekly sales chart.
     */
    public void onWeekButtonClick() {
        this.loadGlobalWeekChart();
    }

    /**
     * Handles the button click event for the local yearly sales button
     * <p>
     * Loads the local yearly sales chart.
     */
    public void onYearLocalButtonClick() {
        this.loadLocalYearChart();
    }

    /**
     * Handles the button click event for the local monthly sales button
     * <p>
     * Loads the local monthly sales chart.
     */
    public void onMonthLocalButtonClick() {
        this.loadLocalMonthChart();
    }

    /**
     * Handles the button click event for the local weekly sales button
     * <p>
     * Loads the local weekly sales chart.
     */
    public void onWeekLocalButtonClick() {
        this.loadLocalWeekChart();
    }

    /**
     * Loads the global yearly sales chart.
     */
    private void loadGlobalYearChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();

        chart.getData().add(new XYChart.Data<>("JAN", getGlobalSalesForMonth(0)));
        chart.getData().add(new XYChart.Data<>("FÉV", getGlobalSalesForMonth(1)));
        chart.getData().add(new XYChart.Data<>("MAR", getGlobalSalesForMonth(2)));
        chart.getData().add(new XYChart.Data<>("AVR", getGlobalSalesForMonth(3)));
        chart.getData().add(new XYChart.Data<>("MAI", getGlobalSalesForMonth(4)));
        chart.getData().add(new XYChart.Data<>("JUN", getGlobalSalesForMonth(5)));
        chart.getData().add(new XYChart.Data<>("JUL", getGlobalSalesForMonth(6)));
        chart.getData().add(new XYChart.Data<>("AOÛ", getGlobalSalesForMonth(7)));
        chart.getData().add(new XYChart.Data<>("SEP", getGlobalSalesForMonth(8)));
        chart.getData().add(new XYChart.Data<>("OCT", getGlobalSalesForMonth(9)));
        chart.getData().add(new XYChart.Data<>("NOV", getGlobalSalesForMonth(10)));
        chart.getData().add(new XYChart.Data<>("DÉC", getGlobalSalesForMonth(11)));
        chart.setName("National Ventes/Année");
        lineChart.getXAxis().setLabel("Cette Année (National)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Loads the global monthly sales chart.
     */
    private void loadGlobalMonthChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        for (int i = 1; i < 32; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getGlobalSalesForDay(i)));
        }
        chart.setName("National Ventes/Mois");
        lineChart.getXAxis().setLabel("Ce Mois (National)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Loads the global weekly sales chart.
     */
    private void loadGlobalWeekChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        // 0 = Sunday -> 6 = Saturday
        for (int i = 0; i < 7; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getGlobalSalesForDayOfTheWeek(i)));
        }
        chart.setName("National Ventes/Semaine");
        lineChart.getXAxis().setLabel("Cette Semaine (National)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Loads the local yearly sales chart.
     */
    private void loadLocalYearChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();

        chart.getData().add(new XYChart.Data<>("JAN", getLocalSalesForMonth(0)));
        chart.getData().add(new XYChart.Data<>("FÉV", getLocalSalesForMonth(1)));
        chart.getData().add(new XYChart.Data<>("MAR", getLocalSalesForMonth(2)));
        chart.getData().add(new XYChart.Data<>("AVR", getLocalSalesForMonth(3)));
        chart.getData().add(new XYChart.Data<>("MAI", getLocalSalesForMonth(4)));
        chart.getData().add(new XYChart.Data<>("JUN", getLocalSalesForMonth(5)));
        chart.getData().add(new XYChart.Data<>("JUL", getLocalSalesForMonth(6)));
        chart.getData().add(new XYChart.Data<>("AOÛ", getLocalSalesForMonth(7)));
        chart.getData().add(new XYChart.Data<>("SEP", getLocalSalesForMonth(8)));
        chart.getData().add(new XYChart.Data<>("OCT", getLocalSalesForMonth(9)));
        chart.getData().add(new XYChart.Data<>("NOV", getLocalSalesForMonth(10)));
        chart.getData().add(new XYChart.Data<>("DÉC", getLocalSalesForMonth(11)));
        chart.setName("Magasin Ventes/Année");
        lineChart.getXAxis().setLabel("Cette Année (Magasin)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Loads the local monthly sales chart.
     */
    private void loadLocalMonthChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        for (int i = 1; i < 32; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getLocalSalesForDay(i)));
        }
        chart.setName("Magasin Ventes/Mois");
        lineChart.getXAxis().setLabel("Ce Mois (Magasin)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Loads the local weekly sales chart.
     */
    private void loadLocalWeekChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        // 0 = Sunday -> 6 = Saturday
        for (int i = 0; i < 7; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getLocalSalesForDayOfTheWeek(i)));
        }
        chart.setName("Magasin Ventes/Semaine");
        lineChart.getXAxis().setLabel("Cette Semaine (Magasin)");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Returns the global sales value for the selected month
     *
     * @param i the month (0 to 11)
     * @return the global sales value for the selected month
     */
    @SuppressWarnings("deprecation")
    private double getGlobalSalesForMonth(int i) {
        int currentYear = new Date().getYear();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (cart.getDate().getYear() == currentYear && cart.getDate().getMonth() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                }
            }
        }
        return sales;
    }

    /**
     * Returns the global sales value for the selected day
     *
     * @param i the day (1 to 31)
     * @return the global sales value for the selected day
     */
    @SuppressWarnings("deprecation")
    private double getGlobalSalesForDay(int i) {
        int currentYear = new Date().getYear();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (cart.getDate().getYear() == currentYear && cart.getDate().getDate() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                }
            }
        }
        return sales;
    }

    /**
     * Returns the global sales value for the selected day of the week
     *
     * @param i the day of the week (0 to 6)
     * @return the global sales value for the selected day of the week
     */
    @SuppressWarnings("deprecation")
    private double getGlobalSalesForDayOfTheWeek(int i) {
        int currentYear = new Date().getYear();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (currentYear == cart.getDate().getYear() && cart.getDate().getDay() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                }
            }
        }
        return sales;
    }

    /**
     * Returns the local sales value for the selected month
     *
     * @param i the month (0 to 11)
     * @return the local sales value for the selected month
     */
    @SuppressWarnings("deprecation")
    private double getLocalSalesForMonth(int i) {
        int currentYear = new Date().getYear();
        Store currentStore = this.getAuthentificationStore();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (cart.getDate().getYear() == currentYear && cart.getDate().getMonth() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    if (clientStock.getProduct().getStore().getId().equals(currentStore.getId())) {
                        sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                    }
                }
            }
        }
        return sales;
    }

    /**
     * Returns the local sales value for the selected day
     *
     * @param i the day (1 to 31)
     * @return the local sales value for the selected day
     */
    @SuppressWarnings("deprecation")
    private double getLocalSalesForDay(int i) {
        int currentYear = new Date().getYear();
        Store currentStore = this.getAuthentificationStore();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (cart.getDate().getYear() == currentYear && cart.getDate().getDate() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    if (clientStock.getProduct().getStore().getId().equals(currentStore.getId())) {
                        sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                    }
                }
            }
        }
        return sales;
    }

    /**
     * Returns the local sales value for the selected day of the week
     *
     * @param i the day of the week (0 to 6)
     * @return the local sales value for the selected day of the week
     */
    @SuppressWarnings("deprecation")
    private double getLocalSalesForDayOfTheWeek(int i) {
        int currentYear = new Date().getYear();
        Store currentStore = this.getAuthentificationStore();
        double sales = 0;
        for (Cart cart : closedCarts) {
            if (currentYear == cart.getDate().getYear() && cart.getDate().getDay() == i
                    && cart.getClientStocks() != null && !cart.getClientStocks().isEmpty()) {
                for (ClientStock clientStock : cart.getClientStocks()) {
                    if (clientStock.getProduct().getStore().getId().equals(currentStore.getId())) {
                        sales += clientStock.getProduct().getSellingPrice() * clientStock.getQuantity();
                    }
                }
            }
        }
        return sales;
    }
}
