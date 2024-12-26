package Controller;

import DAO.GenericDAO;
import DAO.EmployeeDAOImpl;
import Model.Employee;
import Model.Poste;
import Model.Role;
import View.EmployeeView;
import View.HolidayView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmployeeController {
    private final EmployeeView view;
    private final GenericDAO<Employee> dao;
    private final HolidayView holidayView;

    public EmployeeController(EmployeeView view, HolidayView holidayView) {
        this.view = view;
        this.dao = new EmployeeDAOImpl();
        this.holidayView = holidayView;

        // Écouteur pour le bouton Ajouter
        view.addButton.addActionListener(e -> addEmployee());

        // Écouteur pour le bouton Lister
        view.listButton.addActionListener(e -> listEmployees());

        // Écouteur pour le bouton Supprimer
        view.deleteButton.addActionListener(e -> deleteEmployee());

        // Écouteur pour le bouton Modifier
        view.modifyButton.addActionListener(e -> modifyEmployee());

        // ActionListener pour le bouton "Gérer les Congés"
        view.switchViewButton.addActionListener(e -> {
            view.setVisible(false); // Cacher la vue des employés
            holidayView.setVisible(true); // Afficher la vue des congés
        });

        // Ajouter un écouteur de sélection sur la table des employés
        view.employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Récupérer les données de la ligne sélectionnée
                    int id = (int) view.employeeTable.getValueAt(selectedRow, 0);
                    String nom = (String) view.employeeTable.getValueAt(selectedRow, 1);
                    String prenom = (String) view.employeeTable.getValueAt(selectedRow, 2);
                    String email = (String) view.employeeTable.getValueAt(selectedRow, 3);
                    String phone = (String) view.employeeTable.getValueAt(selectedRow, 4);
                    double salaire = (double) view.employeeTable.getValueAt(selectedRow, 5);
                    Role role = Role.valueOf(view.employeeTable.getValueAt(selectedRow, 6).toString());
                    Poste poste = Poste.valueOf(view.employeeTable.getValueAt(selectedRow, 7).toString());

                    // Remplir les champs de modification avec les valeurs de la ligne sélectionnée
                    view.nameField.setText(nom);
                    view.surnameField.setText(prenom);
                    view.emailField.setText(email);
                    view.phoneField.setText(phone);
                    view.salaryField.setText(String.valueOf(salaire));
                    view.roleCombo.setSelectedItem(role.toString());
                    view.posteCombo.setSelectedItem(poste.toString());

                    // Sauvegarder l'ID de l'employé dans le bouton de modification pour une mise à jour
                    view.modifyButton.setActionCommand(String.valueOf(id));
                }
            }
        });

        // Charger la liste des employés au démarrage
        listEmployees();
    }

    // Méthode pour ajouter un employé avec validation
    private void addEmployee() {
        try {
            String nom = view.nameField.getText().trim();
            String prenom = view.surnameField.getText().trim();
            String email = view.emailField.getText().trim();
            String phone = view.phoneField.getText().trim();
            String salaireText = view.salaryField.getText().trim();

            // Validation des champs
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || phone.isEmpty() || salaireText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tous les champs sont obligatoires.");
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(view, "Veuillez entrer une adresse email valide.");
                return;
            }
            double salaire = Double.parseDouble(salaireText);

            Role role = Role.valueOf(view.roleCombo.getSelectedItem().toString().toUpperCase());
            Poste poste = Poste.valueOf(view.posteCombo.getSelectedItem().toString().toUpperCase());

            Employee employee = new Employee(nom, prenom, email, phone, salaire, role, poste);
            dao.add(employee);
            JOptionPane.showMessageDialog(view, "Employé ajouté avec succès.");
            listEmployees(); // Rafraîchir la liste
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Salaire invalide.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    private void listEmployees() {
        List<Employee> employees = dao.listAll();
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Salaire", "Rôle", "Poste"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Employee emp : employees) {
            Object[] row = {emp.getId(), emp.getNom(), emp.getPrenom(), emp.getEmail(), emp.getPhone(), emp.getSalaire(), emp.getRole(), emp.getPoste()};
            model.addRow(row);
        }

        view.employeeTable.setModel(model);
    }

    private void deleteEmployee() {
        try {
            // Vérifier si une ligne est sélectionnée dans la table
            int selectedRow = view.employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un employé à supprimer.");
                return;
            }

            // Récupérer l'ID de l'employé à partir du modèle de table
            int id = (int) view.employeeTable.getValueAt(selectedRow, 0); // Supposons que la colonne 0 contient l'ID

            // Demander confirmation avant de supprimer
            int confirm = JOptionPane.showConfirmDialog(view, 
                    "Êtes-vous sûr de vouloir supprimer l'employé avec l'ID " + id + " ?", 
                    "Confirmation de suppression", 
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Supprimer l'employé via le DAO
                dao.delete(id);

                JOptionPane.showMessageDialog(view, "Employé supprimé avec succès.");
                listEmployees(); // Rafraîchir la liste des employés
            } else {
                JOptionPane.showMessageDialog(view, "Suppression annulée.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }
    private void modifyEmployee() {
        try {
            // Récupérer l'ID de l'employé à partir du bouton de modification
            String actionCommand = view.modifyButton.getActionCommand();
            if (actionCommand != null && !actionCommand.trim().isEmpty()) {
                int id = Integer.parseInt(actionCommand.trim());

                String nom = view.nameField.getText().trim();
                String prenom = view.surnameField.getText().trim();
                String email = view.emailField.getText().trim();
                String phone = view.phoneField.getText().trim();
                String salaireText = view.salaryField.getText().trim();

                // Validation des champs
                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || phone.isEmpty() || salaireText.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tous les champs sont obligatoires.");
                    return;
                }
                if (!email.contains("@")) {
                    JOptionPane.showMessageDialog(view, "Veuillez entrer une adresse email valide.");
                    return;
                }
                double salaire = Double.parseDouble(salaireText);

                Role role = Role.valueOf(view.roleCombo.getSelectedItem().toString().toUpperCase());
                Poste poste = Poste.valueOf(view.posteCombo.getSelectedItem().toString().toUpperCase());

                Employee updatedEmployee = new Employee(nom, prenom, email, phone, salaire, role, poste);
                dao.update(updatedEmployee, id);

                JOptionPane.showMessageDialog(view, "Employé mis à jour avec succès.");
                listEmployees(); // Rafraîchir la liste
            } else {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un employé à modifier.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Salaire invalide.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }
}
