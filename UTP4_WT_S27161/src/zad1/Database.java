package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Database extends JTable {

    private Connection connection;
    private TravelData travelData;
    private DefaultTableModel model;

    public Database(String url, TravelData travelData) {
        this.travelData = travelData;

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Nie udało się połączyć z bazą danych");
            e.printStackTrace();
        }
    }

    public void create() {
        String createTableSQL =
                "CREATE TABLE TRAVEL (" +
                        "Id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), " +
                        "Locale VARCHAR(20), " +
                        "Country VARCHAR(50), " +
                        "StartDate DATE, " +
                        "EndDate DATE, " +
                        "Place VARCHAR(20), " +
                        "Price VARCHAR(20), " +
                        "Currency VARCHAR(20))";

        try {
            Statement stmt = connection.createStatement();

            try {
                stmt.executeUpdate(createTableSQL);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) {
                    /*
                    Jest to specyficzny kod błędu (zwykle w Derby DB) oznaczający, że tabela już istnieje
                    Derby: X0Y32
                    PostgreSQL: 42P07
                    Oracle: ORA-00955
                    MySQL: 1050
                     */
                    throw e;
                }
            }


            String dateFormat = "yyyy-MM-dd";
            List<String> locales = Arrays.asList("pl_PL", "en_GB");

            for (String locale : locales) {
                List<String> offers = travelData.getOffersDescriptionsList(locale, dateFormat);

                for (String offer : offers) {
                    // Parsowanie oferty
                    String[] parts = offer.split("\\s+");

                    // Przygotowanie insertu
                    String insertSQL = "INSERT INTO TRAVEL (Locale, Country, StartDate, EndDate, Place, Price, Currency) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                        pstmt.setString(1, locale);


                        int countryEndIndex = 1;
                        while (!parts[countryEndIndex].matches("\\d{4}-\\d{2}-\\d{2}")) {
                            countryEndIndex++;
                        }
                        String country = String.join(" ", Arrays.copyOfRange(parts, 0, countryEndIndex));

                        pstmt.setString(2, country);                    // Country
                        pstmt.setDate(3, java.sql.Date.valueOf(parts[countryEndIndex]));     // StartDate
                        pstmt.setDate(4, java.sql.Date.valueOf(parts[countryEndIndex + 1])); // EndDate
                        pstmt.setString(5, parts[countryEndIndex + 2]); // Place
                        pstmt.setString(6, parts[countryEndIndex + 3]); // Price
                        pstmt.setString(7, parts[parts.length - 1]);    // Currency

                        pstmt.executeUpdate();
                    }
                }
            }

            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas tworzenia lub wypełniania bazy danych: " + e.getMessage(), e);
        }
    }

    public void showGui() {
        JFrame frame = new JFrame("Travel Offers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Model tabeli
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Dodawanie nazw kolumn
        model.addColumn("Id");
        model.addColumn("Locale");
        model.addColumn("Country");
        model.addColumn("Start Date");
        model.addColumn("End Date");
        model.addColumn("Place");
        model.addColumn("Price");
        model.addColumn("Currency");

        JTable table = new JTable(model);

        // Dodatkowe formatowanie tabeli
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false); // blokuje przemieszczanie kolumn
        table.setRowHeight(25); // zwiększa wysokość wierszy

        // Ustawianie szerokości kolumn
        table.getColumnModel().getColumn(0).setPreferredWidth(30);  // Id
        table.getColumnModel().getColumn(1).setPreferredWidth(50);  // Locale
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Country
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Date
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // End Date
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Place
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Price
        table.getColumnModel().getColumn(7).setPreferredWidth(60);  // Currency

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        // Dodanie przycisku odświeżania
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshTable());
        frame.add(refreshButton, "South");

        // Inicjalne wypełnienie tabeli
        refreshTable();

        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Metoda do odświeżania danych w tabeli
    private void refreshTable() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TRAVEL");

            // Czyszczenie tabeli
            model.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("Id"),
                        rs.getString("Locale"),
                        rs.getString("Country"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Place"),
                        rs.getString("Price"),
                        rs.getString("Currency")
                };
                model.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Błąd podczas odświeżania danych: " + e.getMessage(),
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}