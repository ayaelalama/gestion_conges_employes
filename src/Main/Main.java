package Main;

import Controller.EmployeeController;
import Controller.HolidayController;
import View.EmployeeView;
import View.HolidayView;

public class Main {
    public static void main(String[] args) {
       
        EmployeeView employeeView = new EmployeeView();
        HolidayView holidayView = new HolidayView();

       
        new EmployeeController(employeeView, holidayView);
        new HolidayController(holidayView);

        employeeView.setVisible(true);

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
