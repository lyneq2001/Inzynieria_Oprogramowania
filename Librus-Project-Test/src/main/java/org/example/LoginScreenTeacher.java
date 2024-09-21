import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreenTeacher extends JFrame {

	private Connection connection;
	private PreparedStatement signupStatement;
	private PreparedStatement loginStatement;
	private int loginAttempts = 0; // Track login attempts

	public LoginScreenTeacher() {
		super("Login to E-Dziennik");
		setSize(400, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Center the frame on the screen
		initializeDatabase();
		addLoginComponents();
		getContentPane().setBackground(new Color(240, 240, 240)); // Set background color
		setVisible(true);
	}

	public void initializeDatabase() {
		try {
			String url = "jdbc:mysql://localhost:3306/edziennik";
			String username = "root";
			String password = "root";
			connection = DriverManager.getConnection(url, username, password);

			String signupSql = "INSERT INTO teachers (username, password) VALUES (?, ?)";
			signupStatement = connection.prepareStatement(signupSql);

			String loginSql = "SELECT * FROM teachers WHERE username = ? AND password = ?";
			loginStatement = connection.prepareStatement(loginSql);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Database connection failed!");
			System.exit(1);
		}
	}

	public void addLoginComponents() {
		setLayout(new GridBagLayout()); // Use GridBagLayout for flexibility
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

		// Title label
		JLabel titleLabel = new JLabel("Zaloguj się w panelu Nauczyciel", JLabel.CENTER);
		titleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
		titleLabel.setForeground(new Color(60, 60, 60));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(titleLabel, gbc);

		// Username label
		JLabel usernameLabel = new JLabel("Użytkownik:");
		usernameLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(usernameLabel, gbc);

		// Username text field
		JTextField usernameField = new JTextField(15);
		gbc.gridx = 1;
		add(usernameField, gbc);

		// Password label
		JLabel passwordLabel = new JLabel("Hasło:");
		passwordLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(passwordLabel, gbc);

		// Password field
		JPasswordField passwordField = new JPasswordField(15);
		gbc.gridx = 1;
		add(passwordField, gbc);

		// Login button
		JButton loginButton = new JButton("Zaloguj się");
		styleButton(loginButton);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		loginButton.setPreferredSize(new Dimension(200, 40)); // Set the preferred size of the login button
		add(loginButton, gbc);

		// Sign up instruction label
		JLabel signupInstructionLabel = new JLabel("Jeśli nie ma cię w systemie, zarejestruj się", JLabel.CENTER);
		signupInstructionLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
		signupInstructionLabel.setForeground(new Color(60, 60, 60));
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(signupInstructionLabel, gbc);

		// Sign up button
		JButton signupButton = new JButton("Zarejestruj się");
		styleButton(signupButton);
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		signupButton.setPreferredSize(new Dimension(200, 40)); // Set the preferred size of the signup button
		add(signupButton, gbc);

		// Adding the "Go Back" button
		JButton goBackButton = new JButton("Powrót");
		styleButton(goBackButton);
		gbc.gridy = 6; // Place it after the signup button
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		goBackButton.setPreferredSize(new Dimension(200, 40)); // Set the preferred size of the go back button
		goBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // Close the current registration screen
				new OpeningScreen(); // Open the OpeningScreen
			}
		});
		add(goBackButton, gbc);

		// Action listeners
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());

				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Proszę wypełnić wszystkie pola!");
					return;
				}

				if (loginUser(username, password)) {
					JOptionPane.showMessageDialog(null, "Zalogowano pomyślnie!");
					showTeacherDashboard(username);
					dispose();
				} else {
					loginAttempts++;
					if (loginAttempts >= 5) {
						int choice = JOptionPane.showConfirmDialog(null,
								"Błędne hasło lub nazwa użytkownika!\nCzy chciałbyś się zarejestrować?",
								"Limit logowań przekroczony", JOptionPane.YES_NO_OPTION);
						if (choice == JOptionPane.YES_OPTION) {
							new SignupScreenTeacher(connection, signupStatement);
							dispose();
						}
						loginAttempts = 0; // Reset login attempts after user choice
					} else {
						JOptionPane.showMessageDialog(null, "Błędne hasło lub nazwa użytkownika!");
					}
				}
			}
		});

		signupButton.addActionListener(e -> {
			// Assuming you have the connection and prepared statements in LoginScreenTeacher
			new SignupScreenTeacher(connection, signupStatement);
			dispose();
		});
	}

	private boolean loginUser(String username, String password) {
		try {
			loginStatement.setString(1, username);
			loginStatement.setString(2, password);

			ResultSet resultSet = loginStatement.executeQuery();

			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
		}
		return false;
	}

	private boolean signupUser(String username, String password) {
		try {
			signupStatement.setString(1, username);
			signupStatement.setString(2, password);

			int rowsAffected = signupStatement.executeUpdate();

			if (rowsAffected > 0) {
				return true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
		}
		return false;
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
		SwingUtilities.invokeLater(() -> new LoginScreenTeacher());
	}
}