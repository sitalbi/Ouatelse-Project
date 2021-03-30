package fr.s4e2.ouatelse.controllers;

import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.managers.EntityManagerCart;
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.ClientStock;
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
        this.loadYearChart();
    }

    /**
     * Handles the button click event for the yearly sales button
     * <p>
     * Loads the yearly sales chart.
     */
    public void onYearButtonClick() {
        this.loadYearChart();
    }

    /**
     * Handles the button click event for the monthly sales button
     * <p>
     * Loads the monthly sales chart.
     */
    public void onMonthButtonClick() {
        this.loadMonthChart();
    }

    /**
     * Handles the button click event for the weekly sales button
     * <p>
     * Loads the weekly sales chart.
     */
    public void onWeekButtonClick() {
        this.loadWeekChart();
    }

    /**
     * Loads the yearly sales chart.
     */
    private void loadYearChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();

        chart.getData().add(new XYChart.Data<>("JAN", getSalesForMonth(0)));
        chart.getData().add(new XYChart.Data<>("FÉV", getSalesForMonth(1)));
        chart.getData().add(new XYChart.Data<>("MAR", getSalesForMonth(2)));
        chart.getData().add(new XYChart.Data<>("AVR", getSalesForMonth(3)));
        chart.getData().add(new XYChart.Data<>("MAI", getSalesForMonth(4)));
        chart.getData().add(new XYChart.Data<>("JUN", getSalesForMonth(5)));
        chart.getData().add(new XYChart.Data<>("JUL", getSalesForMonth(6)));
        chart.getData().add(new XYChart.Data<>("AOÛ", getSalesForMonth(7)));
        chart.getData().add(new XYChart.Data<>("SEP", getSalesForMonth(8)));
        chart.getData().add(new XYChart.Data<>("OCT", getSalesForMonth(9)));
        chart.getData().add(new XYChart.Data<>("NOV", getSalesForMonth(10)));
        chart.getData().add(new XYChart.Data<>("DÉC", getSalesForMonth(11)));
        chart.setName("Ventes/Année");
        lineChart.getXAxis().setLabel("Cette Année");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }

    /**
     * Returns the sales value for the selected month
     *
     * @param i the month (0 to 11)
     * @return the sales value for the selected month
     */
    @SuppressWarnings("deprecation")
    private double getSalesForMonth(int i) {
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
     * Loads the monthly sales chart.
     */
    private void loadMonthChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        for (int i = 1; i < 32; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getSalesForDay(i)));
        }
        chart.setName("Ventes/Mois");
        lineChart.getXAxis().setLabel("Ce Mois");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }


    /**
     * Returns the sales value for the selected day
     *
     * @param i the day (1 to 31)
     * @return the sales value for the selected day
     */
    @SuppressWarnings("deprecation")
    private double getSalesForDay(int i) {
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
     * Loads the weekly sales chart.
     */
    private void loadWeekChart() {
        XYChart.Series<String, Double> chart = new XYChart.Series<>();
        // 0 = Sunday -> 6 = Saturday
        for (int i = 0; i < 7; ++i) {
            chart.getData().add(new XYChart.Data<>(String.valueOf(i), getSalesForDayOfTheWeek(i)));
        }
        chart.setName("Ventes/Semaine");
        lineChart.getXAxis().setLabel("Cette Semaine");
        lineChart.setData(FXCollections.observableArrayList(chart));
    }


    /**
     * Returns the sales value for the selected day of the week
     *
     * @param i the day of the week (0 to 6)
     * @return the sales value for the selected day of the week
     */
    @SuppressWarnings("deprecation")
    private double getSalesForDayOfTheWeek(int i) {
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
}
