import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupScreenStudent extends JFrame {

	private Connection connection;
	private PreparedStatement loginStatement;
	private PreparedStatement insertStatement;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField usernameField;
	private JPasswordField passwordField;

	public SignupScreenStudent() {
		super("Login or Register to E-Dziennik");
		setSize(400, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Center the window on the screen
		initializeDatabase();
		addComponents();
		getContentPane().setBackground(new Color(240, 240, 240)); // Set background color
		setVisible(true);
	}

	private void initializeDatabase() {
		try {
			String url = "jdbc:mysql://localhost:3306/edziennik";
			String username = "root";
			String password = "root";

			connection = DriverManager.getConnection(url, username, password);

			String loginSql = "SELECT * FROM teachers WHERE username = ? AND password = ?";
			loginStatement = connection.prepareStatement(loginSql);

			String insertSql = "INSERT INTO teachers (imie, nazwisko, username, password) VALUES (?, ?, ?, ?)";
			insertStatement = connection.prepareStatement(insertSql);

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Database connection failed!");
			System.exit(1);
		}
	}

	private void addComponents() {
		setLayout(new GridBagLayout()); // Use GridBagLayout for consistency with LoginScreenTeacher
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

		// Title label
		JLabel titleLabel = new JLabel("Zarejestruj się w panelu Uczeń", JLabel.CENTER);
		titleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
		titleLabel.setForeground(new Color(60, 60, 60));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(titleLabel, gbc);

		// First Name label
		JLabel firstNameLabel = new JLabel("Imię:");
		firstNameLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(firstNameLabel, gbc);

		// First Name text field
		firstNameField = new JTextField(15);
		gbc.gridx = 1;
		add(firstNameField, gbc);

		// Last Name label
		JLabel lastNameLabel = new JLabel("Nazwisko:");
		lastNameLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridy = 2;
		gbc.gridx = 0;
		add(lastNameLabel, gbc);

		// Last Name text field
		lastNameField = new JTextField(15);
		gbc.gridx = 1;
		add(lastNameField, gbc);

		// Username label
		JLabel usernameLabel = new JLabel("Użytkownik:");
		usernameLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridy = 3;
		gbc.gridx = 0;
		add(usernameLabel, gbc);

		// Username text field
		usernameField = new JTextField(15);
		gbc.gridx = 1;
		add(usernameField, gbc);

		// Password label
		JLabel passwordLabel = new JLabel("Hasło:");
		passwordLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridy = 4;
		gbc.gridx = 0;
		add(passwordLabel, gbc);

		// Password field
		passwordField = new JPasswordField(15);
		gbc.gridx = 1;
		add(passwordField, gbc);

		// Register button
		JButton registerButton = new JButton("Zarejestruj się");
		styleButton(registerButton);
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		registerButton.setPreferredSize(new Dimension(200, 40));
		add(registerButton, gbc);

		// Sign up instruction label
		JLabel signupInstructionLabel = new JLabel("Już masz konto? Zaloguj się", JLabel.CENTER);
		signupInstructionLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
		signupInstructionLabel.setForeground(new Color(60, 60, 60));
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		add(signupInstructionLabel, gbc);

		// Go Back button
		JButton goBackButton = new JButton("Powrót");
		styleButton(goBackButton);
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		goBackButton.setPreferredSize(new Dimension(200, 40));
		goBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // Close the current registration screen
				new LoginScreenStudent(); // Open the LoginScreenTeacher
			}
		});
		add(goBackButton, gbc);

		// Action listeners
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String firstName = firstNameField.getText().trim();
				String lastName = lastNameField.getText().trim();
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword()).trim();

				if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Proszę uzupełnić wszystkie pola!");
					return;
				}

				if (registerNewUser(firstName, lastName, username, password)) {
					JOptionPane.showMessageDialog(null, "Rejestracja udana!");
					clearFields();
					showTeacherDashboard(username);
					dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Błąd podczas rejestracji!");
				}
			}
		});
	}

	private boolean registerNewUser(String firstName, String lastName, String username, String password) {
		try {
			insertStatement.setString(1, firstName);
			insertStatement.setString(2, lastName);
			insertStatement.setString(3, username);
			insertStatement.setString(4, password);

			int rowsInserted = insertStatement.executeUpdate();
			return rowsInserted > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Błąd podczas rejestracji użytkownika: " + ex.getMessage());
			return false;
		}
	}

	private void clearFields() {
		firstNameField.setText("");
		lastNameField.setText("");
		usernameField.setText("");
		passwordField.setText("");
	}

	private void showTeacherDashboard(String username) {
		SwingUtilities.invokeLater(() -> new TeacherDashboard(username));
	}

	// Method to style buttons with a modern look
	private void styleButton(JButton button) {
		button.setFont(new Font("Roboto", Font.PLAIN, 16));
		button.setFocusPainted(false);
		button.setBackground(new Color(70, 130, 180));
		button.setForeground(Color.WHITE);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SignupScreenStudent());
	}
}
