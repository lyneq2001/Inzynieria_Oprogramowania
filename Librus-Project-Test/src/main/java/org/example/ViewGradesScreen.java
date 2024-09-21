import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewGradesScreen extends JFrame {
    private String username;
    private String role;
    private Connection connection;
    private DefaultTableModel tableModel;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> studentComboBox; // New JComboBox for students

    public ViewGradesScreen(String username, String role) {
        super("Zobacz Oceny");
        this.username = username;
        this.role = role;
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        initializeDatabase();
        initializeComponents();
        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/edziennik";
            String dbUsername = "root";
            String dbPassword = "root";
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            System.exit(1);
        }
    }

    private void initializeComponents() {
        // Main Layout
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 87, 141)); // Blue background
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Oceny", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Table
        JTable gradesTable = new JTable();
        tableModel = new DefaultTableModel();
        gradesTable.setModel(tableModel);

        tableModel.addColumn("Przedmiot");
        tableModel.addColumn("Uczeń");
        tableModel.addColumn("Ocena");
        tableModel.addColumn("Data");

        fetchGrades(null); // Fetch all grades initially

        JScrollPane tableScrollPane = new JScrollPane(gradesTable);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Filter Panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridBagLayout());
        filterPanel.setOpaque(false); // Transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        subjectComboBox = new JComboBox<>(new String[]{"", "matematyka", "logika", "analiza", "programowanie"});
        subjectComboBox.setFont(new Font("Roboto", Font.PLAIN, 16));
        subjectComboBox.addActionListener(e -> fetchGrades((String) subjectComboBox.getSelectedItem()));

        JLabel subjectLabel = new JLabel("Przedmiot:");
        subjectLabel.setFont(new Font("Roboto", Font.PLAIN, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(subjectLabel, gbc);

        gbc.gridx = 1;
        filterPanel.add(subjectComboBox, gbc);

        contentPanel.add(filterPanel, BorderLayout.NORTH);

        // Control Panel for Teachers
        if (role.equals("teacher")) {
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            controlPanel.setOpaque(false); // Transparent

            JButton addGradeButton = createStyledButton("Dodaj ocenę");
            JButton deleteGradeButton = createStyledButton("Usuń ocenę");
            JButton updateGradeButton = createStyledButton("Aktualizuj ocenę");

            controlPanel.add(addGradeButton);
            controlPanel.add(deleteGradeButton);
            controlPanel.add(updateGradeButton);

            contentPanel.add(controlPanel, BorderLayout.SOUTH);

            addGradeButton.addActionListener(e -> addGradeDialog());
            deleteGradeButton.addActionListener(e -> deleteGrade(gradesTable));
            updateGradeButton.addActionListener(e -> updateGrade(gradesTable));
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.PLAIN, 16));
        button.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void fetchGrades(String selectedSubject) {
        try {
            String query = "";

            if (role.equals("student")) {
                if (selectedSubject == null || selectedSubject.isEmpty()) {
                    query = "SELECT subject_name, grade, date FROM grades WHERE student_username = ?";
                } else {
                    query = "SELECT subject_name, grade, date FROM grades WHERE student_username = ? AND subject_name = ?";
                }
            } else if (role.equals("teacher")) {
                if (selectedSubject == null || selectedSubject.isEmpty()) {
                    query = "SELECT subject_name, student_username, grade, date FROM grades WHERE teacher_username = ?";
                } else {
                    query = "SELECT subject_name, student_username, grade, date FROM grades WHERE teacher_username = ? AND subject_name = ?";
                }
            }

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            if (selectedSubject != null && !selectedSubject.isEmpty()) {
                statement.setString(2, selectedSubject);
            }

            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String subject = resultSet.getString("subject_name");
                String studentUsername = role.equals("teacher") ? resultSet.getString("student_username") : username;
                String grade = resultSet.getString("grade");
                String date = resultSet.getString("date");

                tableModel.addRow(new Object[]{subject, studentUsername, grade, date});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch grades! Error: " + e.getMessage());
        }
    }

    private void addGradeDialog() {
        List<String> studentUsernames = fetchStudentUsernames();
        studentComboBox = new JComboBox<>(studentUsernames.toArray(new String[0]));

        JTextField gradeField = new JTextField(5);
        JDateChooser dateChooser = new JDateChooser();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel studentLabel = new JLabel("Uczeń:");
        JLabel gradeLabel = new JLabel("Ocena:");
        JLabel dateLabel = new JLabel("Data:");
        JLabel subjectLabel = new JLabel("Przedmiot:");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(studentLabel, gbc);
        gbc.gridx = 1;
        panel.add(studentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(gradeLabel, gbc);
        gbc.gridx = 1;
        panel.add(gradeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(subjectLabel, gbc);
        gbc.gridx = 1;
        panel.add(subjectComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Dodaj ocenę", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String subject = (String) subjectComboBox.getSelectedItem();
            String student = (String) studentComboBox.getSelectedItem();
            String grade = gradeField.getText();
            Date date = new Date(dateChooser.getDate().getTime());

            try {
                String query = "INSERT INTO grades (subject_name, student_username, teacher_username, grade, date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, subject);
                preparedStatement.setString(2, student);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, grade);
                preparedStatement.setDate(5, date);
                preparedStatement.executeUpdate();
                tableModel.addRow(new Object[]{subject, student, grade, date});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add grade! Error: " + e.getMessage());
            }
        }
    }

    private List<String> fetchStudentUsernames() {
        List<String> studentUsernames = new ArrayList<>();
        try {
            String query = "SELECT username FROM students";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String studentUsername = resultSet.getString("username");
                studentUsernames.add(studentUsername);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch student usernames! Error: " + e.getMessage());
        }
        return studentUsernames;
    }

    private void deleteGrade(JTable gradesTable) {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to delete.");
            return;
        }

        String student = (String) tableModel.getValueAt(selectedRow, 1);
        String subject = (String) tableModel.getValueAt(selectedRow, 0);
        String grade = (String) tableModel.getValueAt(selectedRow, 2);
        String date = (String) tableModel.getValueAt(selectedRow, 3);

        try {
            String query = "DELETE FROM grades WHERE student_username = ? AND subject_name = ? AND grade = ? AND date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, student);
            preparedStatement.setString(2, subject);
            preparedStatement.setString(3, grade);
            preparedStatement.setString(4, date);
            preparedStatement.executeUpdate();
            tableModel.removeRow(selectedRow);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete grade! Error: " + e.getMessage());
        }
    }

    private void updateGrade(JTable gradesTable) {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to update.");
            return;
        }

        String student = (String) tableModel.getValueAt(selectedRow, 1);
        String subject = (String) tableModel.getValueAt(selectedRow, 0);
        String oldGrade = (String) tableModel.getValueAt(selectedRow, 2);
        String oldDate = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField newGradeField = new JTextField(oldGrade, 5);
        JDateChooser newDateChooser = new JDateChooser();
        newDateChooser.setDate(Date.valueOf(oldDate)); // Set initial value to current date

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        panel.add(new JLabel("Nowa Ocena:"));
        panel.add(newGradeField);
        panel.add(new JLabel("Nowa Data:"));
        panel.add(newDateChooser);

        int result = JOptionPane.showConfirmDialog(null, panel, "Aktualizuj ocenę", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newGrade = newGradeField.getText();
            Date newDate = new Date(newDateChooser.getDate().getTime());

            try {
                String query = "UPDATE grades SET grade = ?, date = ? WHERE student_username = ? AND subject_name = ? AND grade = ? AND date = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newGrade);
                preparedStatement.setDate(2, newDate);
                preparedStatement.setString(3, student);
                preparedStatement.setString(4, subject);
                preparedStatement.setString(5, oldGrade);
                preparedStatement.setString(6, oldDate);
                preparedStatement.executeUpdate();
                tableModel.setValueAt(newGrade, selectedRow, 2);
                tableModel.setValueAt(newDate, selectedRow, 3);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update grade! Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Replace "teacher" and "student" with appropriate roles
            new ViewGradesScreen("teacher_username", "teacher");
        });
    }
}
