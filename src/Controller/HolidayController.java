package Controller;

import DAO.HolidayDAOImpl;
import Model.Holiday;
import Model.Type;
import View.HolidayView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HolidayController {
    private final HolidayView view;
    private final HolidayDAOImpl dao;

    public HolidayController(HolidayView view) {
        this.view = view;
        this.dao = new HolidayDAOImpl();

        loadEmployeeNames();
        refreshHolidayTable();

        view.addButton.addActionListener(e -> addHoliday());
        view.deleteButton.addActionListener(e -> deleteHoliday());
        view.modifyButton.addActionListener(e -> modifyHoliday());

        // Ajout d'un écouteur de sélection sur la table
        view.holidayTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Vérifie si la sélection est terminée
                int selectedRow = view.holidayTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Récupérer l'ID et les autres informations de la ligne sélectionnée
                    int id = (int) view.holidayTable.getValueAt(selectedRow, 0); // Récupère l'ID depuis la colonne 0
                    String employeeName = (String) view.holidayTable.getValueAt(selectedRow, 1);
                    String startDate = (String) view.holidayTable.getValueAt(selectedRow, 2);
                    String endDate = (String) view.holidayTable.getValueAt(selectedRow, 3);
                    Type type = Type.valueOf(view.holidayTable.getValueAt(selectedRow, 4).toString());

                    // Remplir les champs de modification avec ces valeurs
                    view.employeeNameComboBox.setSelectedItem(employeeName);
                    view.startDateField.setText(startDate);
                    view.endDateField.setText(endDate);
                    view.typeCombo.setSelectedItem(type.toString());

                    // Modifier l'action du bouton de modification pour utiliser l'ID de la ligne sélectionnée
                    view.modifyButton.setActionCommand(String.valueOf(id));
                }
            }
        });
    }

    private void loadEmployeeNames() {
        view.employeeNameComboBox.removeAllItems();
        List<String> names = dao.getAllEmployeeNames();

        if (names.isEmpty()) {
            System.out.println("Aucun employé trouvé.");
        } else {
            System.out.println("Noms des employés chargés : " + names);
            for (String name : names) {
                view.employeeNameComboBox.addItem(name);
            }
        }
    }

    private void refreshHolidayTable() {
        List<Holiday> holidays = dao.listAll();
        String[] columnNames = {"ID", "Employé", "Date Début", "Date Fin", "Type"};
        Object[][] data = new Object[holidays.size()][5];

        for (int i = 0; i < holidays.size(); i++) {
            Holiday h = holidays.get(i);
            data[i] = new Object[]{h.getId(), h.getEmployeeName(), h.getStartDate(), h.getEndDate(), h.getType()};
        }

        view.holidayTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private boolean isValidDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate.parse(date, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        return end.isAfter(start);
    }

    private void addHoliday() {
        try {
            String employeeName = (String) view.employeeNameComboBox.getSelectedItem();
            String startDate = view.startDateField.getText();
            String endDate = view.endDateField.getText();
            Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase());

            if (!isValidDate(startDate) || !isValidDate(endDate)) {
                throw new IllegalArgumentException("Les dates doivent être au format YYYY-MM-DD.");
            }

            if (!isEndDateAfterStartDate(startDate, endDate)) {
                throw new IllegalArgumentException("La date de fin doit être supérieure à la date de début.");
            }

            Holiday holiday = new Holiday(employeeName, startDate, endDate, type);
            dao.add(holiday);
            loadEmployeeNames(); 
            refreshHolidayTable();
            JOptionPane.showMessageDialog(view, "Congé ajouté avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }

    private void modifyHoliday() {
        try {
            // Récupérer l'ID du congé à partir de l'action du bouton
            String actionCommand = view.modifyButton.getActionCommand();
            if (actionCommand != null && !actionCommand.trim().isEmpty()) {
                int id = Integer.parseInt(actionCommand.trim());

                String employeeName = (String) view.employeeNameComboBox.getSelectedItem();
                String startDate = view.startDateField.getText();
                String endDate = view.endDateField.getText();
                Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase());

                if (!isValidDate(startDate) || !isValidDate(endDate)) {
                    throw new IllegalArgumentException("Les dates doivent être au format YYYY-MM-DD.");
                }

                if (!isEndDateAfterStartDate(startDate, endDate)) {
                    throw new IllegalArgumentException("La date de fin doit être supérieure à la date de début.");
                }

                Holiday holiday = new Holiday(employeeName, startDate, endDate, type);
                dao.update(holiday, id);
                loadEmployeeNames(); 
                refreshHolidayTable();
                JOptionPane.showMessageDialog(view, "Congé modifié avec succès.");
            } else {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un congé à modifier.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }

    private void deleteHoliday() {
        try {
            // Afficher un message pour entrer un ID de congé
            String input = JOptionPane.showInputDialog(view, "Veuillez entrer l'ID du congé à supprimer:");
            if (input != null && !input.trim().isEmpty()) {
                int id = Integer.parseInt(input.trim());
    
                // Demander confirmation avant de supprimer
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Êtes-vous sûr de vouloir supprimer le congé avec l'ID " + id + " ?", 
                        "Confirmation de suppression", 
                        JOptionPane.YES_NO_OPTION);
    
                if (confirm == JOptionPane.YES_OPTION) {
                    dao.delete(id); // Supprimer le congé
                    loadEmployeeNames();
                    refreshHolidayTable();
                    JOptionPane.showMessageDialog(view, "Congé supprimé avec succès.");
                } else {
                    JOptionPane.showMessageDialog(view, "Suppression annulée.");
                }
            } else {
                JOptionPane.showMessageDialog(view, "ID de congé non valide ou action annulée.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "ID invalide. Veuillez entrer un nombre valide.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }
    
}
