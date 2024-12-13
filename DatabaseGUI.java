import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class DatabaseGUI extends JFrame {
    private final Database database;

    public DatabaseGUI() throws IOException, ClassNotFoundException {
        database = new Database("database.db");

        setTitle("Database GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton addButton = new JButton("Add Record");
        JButton deleteButton = new JButton("Delete Record");
        JButton searchButton = new JButton("Search Records");
        JButton viewButton = new JButton("View All Records");
        JButton backupButton = new JButton("Backup Database");
        JButton restoreButton = new JButton("Restore Database");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(backupButton);
        buttonPanel.add(restoreButton);

        add(buttonPanel, BorderLayout.NORTH);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(_ -> showAddRecordDialog(outputArea));
        deleteButton.addActionListener(_ -> showDeleteRecordDialog(outputArea));
        searchButton.addActionListener(_ -> showSearchRecordDialog(outputArea));
        viewButton.addActionListener(_ -> showAllRecords(outputArea));
        backupButton.addActionListener(_ -> showBackupDialog(outputArea));
        restoreButton.addActionListener(_ -> showRestoreDialog(outputArea));
    }

    private void splitLine(JTextArea outputArea) {
        outputArea.append("___________________________________________________________________________________________\n");
    }

    private void showAddRecordDialog(JTextArea outputArea) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField addressField = new JTextField();

        Object[] message = {
                "ID:", idField,
                "Name:", nameField,
                "Age:", ageField,
                "Address:", addressField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Record", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            int age;
            try {
                age = Integer.parseInt(ageField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String address = addressField.getText();

            try {
                Record record = new Record(id, name, age, address);
                database.addRecord(record);
                outputArea.append("Record added: " + record + "\n");

                splitLine(outputArea);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving the record!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Record with this ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteRecordDialog(JTextArea outputArea) {
        String field = JOptionPane.showInputDialog(this, "Enter field to delete by (id, name, age, address):");
        String value = JOptionPane.showInputDialog(this, "Enter value to delete for:");

        if (field != null && !value.isEmpty()) {
            try {
                var results = database.searchByField(field, value);
                for (Record record : results) {
                    outputArea.append(record + "\n");
                }
                if (results.isEmpty()) {
                    outputArea.append("No records found for " + field + " = " + value + "\n");
                }
                outputArea.append("Records deleted.\n");
                database.deleteRecordsByField(field, value);

                splitLine(outputArea);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting the record!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSearchRecordDialog(JTextArea outputArea) {
        String field = JOptionPane.showInputDialog(this, "Enter field to search by (id, name, age, address):");
        String value = JOptionPane.showInputDialog(this, "Enter value to search for:");

        if (field != null && value != null) {
            var results = database.searchByField(field, value);
            outputArea.append("Search results:\n");
            for (Record record : results) {
                outputArea.append(record + "\n");
            }
            if (results.isEmpty()) {
                outputArea.append("No records found for " + field + " = " + value + "\n");
            }
            splitLine(outputArea);
        }
    }

    private void showAllRecords(JTextArea outputArea) {
        outputArea.append("All Records:\n");
        try {
            var records = database.getAllRecordsFromFile();
            for (Record record : records) {
                outputArea.append(record + "\n");
            }
            if (records.isEmpty()) {
                outputArea.append("No records available.\n");
            }
            splitLine(outputArea);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error reading records!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBackupDialog(JTextArea outputArea) {
        String backupFileName = JOptionPane.showInputDialog(this, "Enter backup file name:");

        if (backupFileName != null && !backupFileName.isEmpty()) {
            try {
                database.backup(backupFileName);
                outputArea.append("Database backed up to " + backupFileName + "\n");

                splitLine(outputArea);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating backup!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRestoreDialog(JTextArea outputArea) {
        String backupFileName = JOptionPane.showInputDialog(this, "Enter backup file name to restore from:");

        if (backupFileName != null && !backupFileName.isEmpty()) {
            try {
                database.restoreFromBackup(backupFileName);
                outputArea.append("Database restored from " + backupFileName + "\n");

                splitLine(outputArea);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error restoring database!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new DatabaseGUI().setVisible(true);
            } catch (IOException | ClassNotFoundException ex) {}
        });
    }
}
