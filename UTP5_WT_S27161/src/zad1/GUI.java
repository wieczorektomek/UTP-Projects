package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public class GUI {
    private Controller controller;
    private JFrame frame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private final String dataDir = System.getProperty("user.home") + "/Modeling/data/";
    private final String scriptDir = System.getProperty("user.home") + "/Modeling/scripts/";

    /**
     * Initializes the GUI with main window and all components
     */
    public GUI() {
        initializeMainFrame();
        initializeSelectionPanel();
        initializeResultTable();
        initializeScriptPanel();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Sets up the main application window
     */
    private void initializeMainFrame() {
        frame = new JFrame("Modelling framework sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
    }

    /**
     * Creates and configures the model and data selection panel
     */
    private void initializeSelectionPanel() {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select model and data"));

        String[] models = {"Model1", "Model2", "Model3", "MultiAgentSim"};
        JList<String> modelList = new JList<>(models);
        selectionPanel.add(new JScrollPane(modelList), BorderLayout.WEST);

        String[] dataFiles = {"HS6_EU_all.txt", "data1.txt", "data2.txt", "data3.txt"};
        JList<String> dataList = new JList<>(dataFiles);
        selectionPanel.add(new JScrollPane(dataList), BorderLayout.CENTER);

        JButton runModelButton = new JButton("Run model");
        runModelButton.addActionListener(e -> runModel(modelList, dataList));
        selectionPanel.add(runModelButton, BorderLayout.SOUTH);

        frame.add(selectionPanel, BorderLayout.WEST);
    }

    /**
     * Initializes the results table
     */
    private void initializeResultTable() {
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        frame.add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }

    /**
     * Creates the script control panel
     */
    private void initializeScriptPanel() {
        JPanel scriptPanel = new JPanel();
        JButton runScriptButton = new JButton("Run script from file");
        JButton createAndRunScriptButton = new JButton("Create and run ad hoc script");

        runScriptButton.addActionListener(e -> runScriptFromFile());
        createAndRunScriptButton.addActionListener(e -> createAndRunAdHocScript());

        scriptPanel.add(runScriptButton);
        scriptPanel.add(createAndRunScriptButton);
        frame.add(scriptPanel, BorderLayout.SOUTH);
    }

    /**
     * Executes selected model with chosen data file
     */
    private void runModel(JList<String> modelList, JList<String> dataList) {
        String selectedModel = modelList.getSelectedValue();
        String selectedData = dataList.getSelectedValue();

        if (selectedModel == null || selectedData == null) {
            JOptionPane.showMessageDialog(frame, "Please select a model and a data file.");
            return;
        }

        try {
            controller = new Controller(selectedModel);
            controller.readDataFrom(dataDir + selectedData);
            controller.runModel();
            updateResultsTable(controller.getResultDataMap());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    /**
     * Executes script from selected file
     */
    private void runScriptFromFile() {
        if (controller == null) {
            JOptionPane.showMessageDialog(frame, "Please run a model first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser(scriptDir);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.runScriptFromFile(fileChooser.getSelectedFile().getAbsolutePath());
                updateResultsTable(controller.getResultDataMap());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        }
    }

    /**
     * Opens dialog for creating and running custom scripts
     */
    private void createAndRunAdHocScript() {
        if (controller == null) {
            JOptionPane.showMessageDialog(frame, "Please run a model first.");
            return;
        }

        JDialog scriptDialog = createScriptDialog();
        JTextArea scriptArea = new JTextArea(20, 40);
        configureScriptDialog(scriptDialog, scriptArea);
        scriptDialog.setVisible(true);
    }

    /**
     * Creates script dialog window
     */
    private JDialog createScriptDialog() {
        JDialog dialog = new JDialog(frame, "Create and Run Script", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    /**
     * Configures script dialog components
     */
    private void configureScriptDialog(JDialog dialog, JTextArea scriptArea) {
        scriptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scriptArea.setTabSize(2);
        dialog.add(new JScrollPane(scriptArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton runButton = new JButton("Run");
        JButton cancelButton = new JButton("Cancel");

        runButton.addActionListener(e -> executeScript(dialog, scriptArea));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(runButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Executes script from text area
     */
    private void executeScript(JDialog dialog, JTextArea scriptArea) {
        String script = scriptArea.getText().trim();
        if (!script.isEmpty()) {
            try {
                controller.runScript(script);
                updateResultsTable(controller.getResultDataMap());
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        }
    }

    /**
     * Updates results table with formatted data
     */
    private void updateResultsTable(Map<String, double[]> results) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (results.isEmpty()) return;

        setupTableColumns(results);
        populateTableData(results);
    }

    /**
     * Sets up table columns with years
     */
    private void setupTableColumns(Map<String, double[]> results) {
        String[] years = new String[results.values().iterator().next().length];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(2015 + i);
        }
        tableModel.addColumn("");
        for (String year : years) {
            tableModel.addColumn(year);
        }
    }

    /**
     * Populates table with formatted data
     */
    private void populateTableData(Map<String, double[]> results) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
        symbols.setGroupingSeparator(' ');
        DecimalFormat twoDecimal = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat oneDecimal = new DecimalFormat("#,##0.0", symbols);
        DecimalFormat zdeksFormat = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.GERMAN));

        for (Map.Entry<String, double[]> entry : results.entrySet()) {
            if (!entry.getKey().equals("LATA")) {
                addFormattedRow(entry, twoDecimal, oneDecimal, zdeksFormat);
            }
        }
    }

    /**
     * Adds a formatted row to the table
     */
    private void addFormattedRow(Map.Entry<String, double[]> entry, DecimalFormat twoDecimal,
                                 DecimalFormat oneDecimal, DecimalFormat zdeksFormat) {
        Object[] row = new Object[entry.getValue().length + 1];
        row[0] = entry.getKey();

        for (int i = 0; i < entry.getValue().length; i++) {
            row[i + 1] = formatValue(entry.getKey(), entry.getValue()[i],
                    twoDecimal, oneDecimal, zdeksFormat);
        }
        tableModel.addRow(row);
    }

    /**
     * Formats a single value based on its type
     */
    private String formatValue(String key, double value, DecimalFormat twoDecimal,
                               DecimalFormat oneDecimal, DecimalFormat zdeksFormat) {
        if (key.equals("ZDEKS")) {
            String formatted = zdeksFormat.format(value);
            while (formatted.contains(",") && formatted.endsWith("0")) {
                formatted = formatted.substring(0, formatted.length() - 1);
            }
            return formatted.endsWith(",") ? formatted.substring(0, formatted.length() - 1) : formatted;
        }
        return key.startsWith("tw") ? twoDecimal.format(value) : oneDecimal.format(value);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}