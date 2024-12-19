package Model;

public class Holiday {
    private int id; // Identifiant du congé
    private int employeeId; // ID de l'employé
    private String employeeName; // Nom complet de l'employé
    private String startDate; // Date de début
    private String endDate;   // Date de fin
    private Type type;        // Type de congé (enum)

    // Constructeur avec employeeName pour listAll()
    public Holiday(int id, String employeeName, String startDate, String endDate, Type type) {
        this.id = id;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }
    public Holiday(String employeeName, String startDate, String endDate, Type type) {
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    // Constructeur pour add() et update() (sans employeeName)
    public Holiday(int employeeId, String startDate, String endDate, Type type) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName; // Retourne le nom complet
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Type getType() {
        return type;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
