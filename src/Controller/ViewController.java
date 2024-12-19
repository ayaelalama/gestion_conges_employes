package Controller;

import View.EmployeeView;
import View.HolidayView;

public class ViewController {
    private final EmployeeView employeeView;
    private final HolidayView holidayView;

    public ViewController() {
        this.employeeView = new EmployeeView();
        this.holidayView = new HolidayView();

        // Connect views for navigation
        employeeView.switchViewButton.addActionListener(e -> showHolidayView());
        holidayView.switchViewButton.addActionListener(e -> showEmployeeView());
    }

    public void showEmployeeView() {
        holidayView.setVisible(false);
        employeeView.setVisible(true);
    }

    public void showHolidayView() {
        employeeView.setVisible(false);
        holidayView.setVisible(true);
    }

    public static void main(String[] args) {
        new ViewController().showEmployeeView();
    }
}
