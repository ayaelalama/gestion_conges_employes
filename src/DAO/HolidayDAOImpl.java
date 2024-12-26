package DAO;

import Model.Holiday;
import Model.Type;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAOImpl implements GenericDAO<Holiday> {

    private static final String INSERT_HOLIDAY_SQL = "INSERT INTO holiday (employeeId, startDate, endDate, type) VALUES (?, ?, ?, ?)";
    private static final String DELETE_HOLIDAY_SQL = "DELETE FROM holiday WHERE id = ?";
    private static final String SELECT_ALL_HOLIDAY_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM holiday h JOIN employe e ON h.employeeId = e.id";
    private static final String SELECT_HOLIDAY_BY_ID_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM holiday h JOIN employe e ON h.employeeId = e.id WHERE h.id = ?";
    private static final String SELECT_EMPLOYEE_ID_BY_NAME_SQL = "SELECT id FROM employe WHERE CONCAT(nom, ' ', prenom) = ?";

    @Override
    public void add(Holiday holiday) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_HOLIDAY_SQL)) {
            int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
            if (employeeId == -1) return;
            stmt.setInt(1, employeeId);
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_HOLIDAY_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Holiday> listAll() {
        List<Holiday> holidays = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_HOLIDAY_SQL); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                holidays.add(new Holiday(
                        rs.getInt("id"),
                        rs.getString("employeeName"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }

    @Override
    public Holiday findById(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_HOLIDAY_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Holiday(
                        rs.getInt("id"),
                        rs.getString("employeeName"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Holiday holiday, int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE holiday SET employeeId = ?, startDate = ?, endDate = ?, type = ? WHERE id = ?")) {
            int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
            if (employeeId == -1) return;
            stmt.setInt(1, employeeId);
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getEmployeeIdByName(String employeeName) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_ID_BY_NAME_SQL)) {
            stmt.setString(1, employeeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int getTotalDaysTakenThisYear(String employeeName, int year) {
        int totalDays = 0;
        String query = """
            SELECT SUM(DATEDIFF(endDate, startDate) + 1) AS totalDays 
            FROM holiday 
            WHERE employeeId = ? 
            AND YEAR(startDate) = ?
            """;

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int employeeId = getEmployeeIdByName(employeeName);
            if (employeeId == -1) return 0;

            stmt.setInt(1, employeeId);
            stmt.setInt(2, year);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalDays = rs.getInt("totalDays");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalDays;
    }


    public List<String> getAllEmployeeNames() {
        List<String> employeeNames = new ArrayList<>();
        String query = "SELECT CONCAT(nom, ' ', prenom) AS fullName FROM employe";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) employeeNames.add(rs.getString("fullName"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeNames;
    }

    public List<Type> getAllHolidayTypes() {
        List<Type> holidayTypes = new ArrayList<>();
        for (Type type : Type.values()) holidayTypes.add(type);
        return holidayTypes;
    }
    public boolean hasOverlappingHoliday(String employeeName, String startDate, String endDate) {
        String query = """
            SELECT COUNT(*) AS overlapCount 
            FROM holiday 
            WHERE employeeId = ? 
            AND (
                (startDate <= ? AND endDate >= ?) OR 
                (startDate <= ? AND endDate >= ?) OR 
                (startDate >= ? AND endDate <= ?)
            )
        """;

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int employeeId = getEmployeeIdByName(employeeName);
            if (employeeId == -1) return false;

            stmt.setInt(1, employeeId);
            stmt.setString(2, startDate);
            stmt.setString(3, startDate);
            stmt.setString(4, endDate);
            stmt.setString(5, endDate);
            stmt.setString(6, startDate);
            stmt.setString(7, endDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("overlapCount") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}