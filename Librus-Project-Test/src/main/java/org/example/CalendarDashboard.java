import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarDashboard extends JFrame {
	private DefaultTableModel tableModel;
	private Connection connection;
	private Calendar calendar;
	private JLabel monthLabel;
	private JPanel calendarPanel;
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;

	public CalendarDashboard(String username, String role) {
		super("Calendar Dashboard");
		setSize(800, 800);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		calendar = new GregorianCalendar();
		initializeDatabaseConnection();
		initializeComponents();
		setVisible(true);

		if (role.equals("teacher")) {
			addButton.setEnabled(true);
			editButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else if (role.equals("student")) {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}

		fetchDataFromDatabase();
	}

	private void initializeDatabaseConnection() {
		try {
			String url = "jdbc:mysql://localhost:3306/edziennik";
			String dbUsername = "root";
			String dbPassword = "root";
			connection = DriverManager.getConnection(url, dbUsername, dbPassword);
			System.out.println("Connected to database.");
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
		}
	}

	private void initializeComponents() {
		// Set overall theme and layout
		setLayout(new BorderLayout());
		setBackground(new Color(240, 240, 240));

		// Month panel with customized look
		JPanel monthPanel = new JPanel(new BorderLayout());
		monthPanel.setBackground(new Color(60, 120, 180));
		monthLabel = new JLabel("", JLabel.CENTER);
		monthLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		monthLabel.setForeground(Color.WHITE);
		updateMonthLabel();
		monthPanel.add(monthLabel, BorderLayout.CENTER);

		JPanel navigationPanel = new JPanel();
		navigationPanel.setBackground(new Color(60, 120, 180));
		JButton prevButton = new JButton("◀ Poprzedni");
		JButton nextButton = new JButton("Następny ▶");
		styleButton(prevButton);
		styleButton(nextButton);
		navigationPanel.add(prevButton);
		navigationPanel.add(nextButton);

		monthPanel.add(navigationPanel, BorderLayout.NORTH);
		add(monthPanel, BorderLayout.NORTH);

		// Calendar panel with improved layout
		calendarPanel = new JPanel(new GridLayout(7, 7));
		calendarPanel.setBackground(new Color(255, 255, 255));
		updateCalendar();
		add(calendarPanel, BorderLayout.CENTER);

		// Event panel with table and control buttons
		JPanel eventPanel = new JPanel(new BorderLayout());
		eventPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		eventPanel.setBackground(new Color(240, 240, 240));

		tableModel = new DefaultTableModel(new Object[]{"Data", "Opis"}, 0);
		JTable eventTable = new JTable(tableModel);
		eventTable.setRowHeight(25);
		eventTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(eventTable);
		eventPanel.add(scrollPane, BorderLayout.CENTER);

		// Control panel with styled buttons
		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(new Color(240, 240, 240));
		addButton = new JButton("Dodaj wydarzenie");
		editButton = new JButton("Edytuj wydarzenie");
		deleteButton = new JButton("Usuń wydarzenie");
		styleButton(addButton);
		styleButton(editButton);
		styleButton(deleteButton);

		controlPanel.add(addButton);
		controlPanel.add(editButton);
		controlPanel.add(deleteButton);

		eventPanel.add(controlPanel, BorderLayout.SOUTH);
		add(eventPanel, BorderLayout.SOUTH);

		// Add action listeners for buttons
		addButton.addActionListener(e -> addEvent());
		editButton.addActionListener(e -> editEvent(eventTable));
		deleteButton.addActionListener(e -> deleteEvent(eventTable));

		// Add action listeners for navigation buttons
		prevButton.addActionListener(e -> {
			calendar.add(Calendar.MONTH, -1);
			updateCalendar();
			updateMonthLabel();
		});

		nextButton.addActionListener(e -> {
			calendar.add(Calendar.MONTH, 1);
			updateCalendar();
			updateMonthLabel();
		});
	}

	private void styleButton(JButton button) {
		button.setBackground(new Color(60, 120, 180));
		button.setForeground(Color.WHITE);
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setToolTipText(button.getText());
	}

	private void updateMonthLabel() {
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		String[] monthNames = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};
		monthLabel.setText(monthNames[month] + " " + year);
	}

	private void updateCalendar() {
		calendarPanel.removeAll();
		calendarPanel.setLayout(new GridLayout(7, 7));

		String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

		for (String day : days) {
			JLabel dayLabel = new JLabel(day, JLabel.CENTER);
			dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
			dayLabel.setForeground(new Color(60, 120, 180));
			calendarPanel.add(dayLabel);
		}

		Calendar tempCal = (Calendar) calendar.clone();
		tempCal.set(Calendar.DAY_OF_MONTH, 1);
		int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
		int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 1; i < firstDayOfWeek; i++) {
			calendarPanel.add(new JLabel(""));
		}

		for (int day = 1; day <= daysInMonth; day++) {
			JLabel dayLabel = new JLabel(String.valueOf(day), JLabel.CENTER);
			dayLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
			calendarPanel.add(dayLabel);
		}

		calendarPanel.revalidate();
		calendarPanel.repaint();
	}

	private void fetchDataFromDatabase() {
		try {
			String query = "SELECT date, description FROM events";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				String date = rs.getString("date");
				String description = rs.getString("description");
				tableModel.addRow(new Object[]{date, description});
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to fetch events from database: " + e.getMessage());
		}
	}

	private void addEvent() {
		JTextField dateField = new JTextField(10);
		JTextField descriptionField = new JTextField(20);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Data (YYYY-MM-DD):"));
		panel.add(dateField);
		panel.add(new JLabel("Opis:"));
		panel.add(descriptionField);

		int result = JOptionPane.showConfirmDialog(null, panel, "Dodaj wydarzenie", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String date = dateField.getText();
			String description = descriptionField.getText();

			try {
				String query = "INSERT INTO events (date, description) VALUES (?, ?)";
				PreparedStatement pstmt = connection.prepareStatement(query);
				pstmt.setString(1, date);
				pstmt.setString(2, description);
				pstmt.executeUpdate();

				tableModel.addRow(new Object[]{date, description});
				System.out.println("Event added to database.");
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to add event: " + e.getMessage());
			}
		}
	}

	private void editEvent(JTable eventTable) {
		int selectedRow = eventTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Wybierz wydarzenie do edycji.");
			return;
		}

		String oldDate = (String) tableModel.getValueAt(selectedRow, 0);
		String oldDescription = (String) tableModel.getValueAt(selectedRow, 1);

		JTextField dateField = new JTextField(oldDate, 10);
		JTextField descriptionField = new JTextField(oldDescription, 20);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Nowa Data (YYYY-MM-DD):"));
		panel.add(dateField);
		panel.add(new JLabel("Nowy Opis:"));
		panel.add(descriptionField);

		int result = JOptionPane.showConfirmDialog(null, panel, "Edytuj wydarzenie", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String newDate = dateField.getText();
			String newDescription = descriptionField.getText();

			try {
				String query = "UPDATE events SET date = ?, description = ? WHERE date = ? AND description = ?";
				PreparedStatement pstmt = connection.prepareStatement(query);
				pstmt.setString(1, newDate);
				pstmt.setString(2, newDescription);
				pstmt.setString(3, oldDate);
				pstmt.setString(4, oldDescription);
				pstmt.executeUpdate();

				tableModel.setValueAt(newDate, selectedRow, 0);
				tableModel.setValueAt(newDescription, selectedRow, 1);
				System.out.println("Event updated in database.");
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to update event: " + e.getMessage());
			}
		}
	}

	private void deleteEvent(JTable eventTable) {
		int selectedRow = eventTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Wybierz wydarzenie do usunięcia.");
			return;
		}

		String dateToDelete = (String) tableModel.getValueAt(selectedRow, 0);
		String descriptionToDelete = (String) tableModel.getValueAt(selectedRow, 1);

		try {
			String query = "DELETE FROM events WHERE date = ? AND description = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, dateToDelete);
			pstmt.setString(2, descriptionToDelete);
			pstmt.executeUpdate();

			tableModel.removeRow(selectedRow);
			System.out.println("Event deleted from database.");
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to delete event: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new CalendarDashboard("teacher", "teacher");
		});
	}
}
