package Main;

import Controller.EmployeeController;
import Controller.HolidayController;
import View.EmployeeView;
import View.HolidayView;

public class Main {
    public static void main(String[] args) {
        // Créer les vues
        EmployeeView employeeView = new EmployeeView();
        HolidayView holidayView = new HolidayView();

        // Créer les contrôleurs pour chaque vue
        new EmployeeController(employeeView, holidayView);
        new HolidayController(holidayView);

        // Définir quelle vue sera affichée par défaut (exemple : vue des employés)
        employeeView.setVisible(true);

        // Ajouter un gestionnaire pour passer de l'une à l'autre
        employeeView.switchViewButton.addActionListener(e -> {
            employeeView.setVisible(false);
            holidayView.setVisible(true);
        });

        holidayView.switchViewButton.addActionListener(e -> {
            holidayView.setVisible(false);
            employeeView.setVisible(true);
        });
    }
}
