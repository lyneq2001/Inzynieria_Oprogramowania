import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentProfile extends JFrame {
	private String username;
	private JTextField nameField;
	private JTextField surnameField;
	private JTextField emailField;
	private Connection connection;

	public StudentProfile(String username) {
		super("Profil - " + username);
		this.username = username;
		initializeDatabaseConnection();
		initializeComponents();
		retrieveUserData(); // Retrieve user data from the database
		setVisible(true);
	}

	private void initializeDatabaseConnection() {
		try {
			String url = "jdbc:mysql://localhost:3306/edziennik";
			String dbUsername = "root";
			String dbPassword = "root";
			connection = DriverManager.getConnection(url, dbUsername, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
		}
	}

	private void initializeComponents() {
		setSize(500, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // Center the window

		// Main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

		// Title Panel
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(41, 87, 141)); // Blue background
		titlePanel.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel("Profil użytkownika: " + username, JLabel.CENTER);
		titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
		titleLabel.setForeground(Color.WHITE);

		titlePanel.add(titleLabel, BorderLayout.CENTER);
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		// Information Panel
		JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

		JLabel nameLabel = new JLabel("Imię:");
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(200, 25)); // Set preferred size
		JLabel surnameLabel = new JLabel("Nazwisko:");
		surnameField = new JTextField();
		surnameField.setPreferredSize(new Dimension(200, 25)); // Set preferred size
		JLabel emailLabel = new JLabel("Email:");
		emailField = new JTextField();

		infoPanel.add(nameLabel);
		infoPanel.add(nameField);
		infoPanel.add(surnameLabel);
		infoPanel.add(surnameField);
		infoPanel.add(emailLabel);
		infoPanel.add(emailField);

		mainPanel.add(infoPanel, BorderLayout.CENTER);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

		JButton editButton = createStyledButton("Edytuj profil");
		editButton.addActionListener(e -> enableEditing(true));

		JButton saveButton = createStyledButton("Zapisz zmiany");
		saveButton.addActionListener(e -> saveChanges());

		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);
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

	private void enableEditing(boolean enable) {
		nameField.setEditable(enable);
		surnameField.setEditable(enable);
		emailField.setEditable(enable);
	}

	private void saveChanges() {
		String newName = nameField.getText().trim();
		String newLastName = surnameField.getText().trim();
		String newEmail = emailField.getText().trim();

		if (newName.isEmpty() || newLastName.isEmpty() || newEmail.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Proszę uzupełnić wszystkie pola.");
			return;
		}

		try {
			boolean userExists = checkIfUserExists(username);

			if (userExists) {
				String updateQuery = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE username = ?";
				PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
				updateStatement.setString(1, newName);
				updateStatement.setString(2, newLastName);
				updateStatement.setString(3, newEmail);
				updateStatement.setString(4, username);

				int rowsAffected = updateStatement.executeUpdate();
				JOptionPane.showMessageDialog(this, rowsAffected > 0 ? "Zmiany zostały zapisane." : "Nie znaleziono użytkownika o nazwie: " + username);
			} else {
				String insertQuery = "INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?)";
				PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
				insertStatement.setString(1, username);
				insertStatement.setString(2, newName);
				insertStatement.setString(3, newLastName);
				insertStatement.setString(4, newEmail);

				int rowsAffected = insertStatement.executeUpdate();
				JOptionPane.showMessageDialog(this, rowsAffected > 0 ? "Nowy użytkownik został dodany." : "Nie udało się dodać nowego użytkownika.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Błąd podczas zapisu zmian: " + e.getMessage());
		}
	}

	private void retrieveUserData() {
		try {
			String query = "SELECT first_name, last_name, email FROM users WHERE username = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String email = resultSet.getString("email");

				nameField.setText(firstName);
				surnameField.setText(lastName);
				emailField.setText(email);
			} else {
				JOptionPane.showMessageDialog(this, "Nie znaleziono użytkownika o nazwie: " + username);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Błąd podczas pobierania danych użytkownika: " + e.getMessage());
		}
	}

	private boolean checkIfUserExists(String username) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet.next(); // true if user exists, false otherwise
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new StudentProfile("user_username"));
	}
}
