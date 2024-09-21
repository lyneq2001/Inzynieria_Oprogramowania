import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MessageScreen extends JFrame {
	private String currentUsername;
	private Connection connection;
	private JTextArea messageArea;
	private JTextField messageField;
	private JTextField recipientField;

	public MessageScreen(String currentUsername) {
		super("Wiadomości - " + currentUsername);
		this.currentUsername = currentUsername;
		setSize(800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // Center the window
		initializeDatabaseConnection();
		initializeComponents();
		updateMessages(); // Retrieve all messages from the database
		setVisible(true);
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
		setLayout(new BorderLayout());

		// Title Panel
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(41, 87, 141)); // Blue background
		titlePanel.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel("Wiadomości", JLabel.CENTER);
		titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
		titleLabel.setForeground(Color.WHITE);

		titlePanel.add(titleLabel, BorderLayout.CENTER);
		add(titlePanel, BorderLayout.NORTH);

		// Message Display Panel
		JPanel messagesPanel = new JPanel(new BorderLayout());
		messagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messageArea.setFont(new Font("Roboto", Font.PLAIN, 16));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		JScrollPane messageScrollPane = new JScrollPane(messageArea);
		messagesPanel.add(messageScrollPane, BorderLayout.CENTER);

		add(messagesPanel, BorderLayout.CENTER);

		// Send Panel
		JPanel sendPanel = new JPanel(new BorderLayout());
		sendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

		JPanel inputPanel = new JPanel(new BorderLayout());

		recipientField = new JTextField();
		recipientField.setFont(new Font("Roboto", Font.PLAIN, 16));
		recipientField.setBorder(BorderFactory.createTitledBorder("Recipient"));

		messageField = new JTextField();
		messageField.setFont(new Font("Roboto", Font.PLAIN, 16));
		messageField.setBorder(BorderFactory.createTitledBorder("Message"));

		JButton sendButton = createStyledButton("Wyślij");

		inputPanel.add(recipientField, BorderLayout.NORTH);
		inputPanel.add(messageField, BorderLayout.CENTER);
		sendPanel.add(inputPanel, BorderLayout.CENTER);
		sendPanel.add(sendButton, BorderLayout.EAST);

		add(sendPanel, BorderLayout.SOUTH);

		sendButton.addActionListener(e -> {
			String recipient = recipientField.getText().trim();
			String message = messageField.getText().trim();
			if (!recipient.isEmpty() && !message.isEmpty()) {
				sendMessage(currentUsername, recipient, message);
				messageArea.append("\nYou to " + recipient + ": " + message);
				messageField.setText("");
				recipientField.setText("");
			} else {
				JOptionPane.showMessageDialog(this, "Wprowadź treść wiadomości i odbiorcę.");
			}
		});
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

	private void sendMessage(String sender, String recipient, String message) {
		try {
			String query = "INSERT INTO messages (sender, recipient, message_content) VALUES (?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, sender);
			preparedStatement.setString(2, recipient);
			preparedStatement.setString(3, message);
			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Message inserted into database.");
			} else {
				System.out.println("Failed to insert message into database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to insert message into database: " + e.getMessage());
		}
	}

	private void updateMessages() {
		messageArea.setText(""); // Clear the message area before updating
		try {
			String query = "SELECT sender, recipient, message_content FROM messages WHERE sender = ? OR recipient = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, currentUsername);
			preparedStatement.setString(2, currentUsername);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String sender = resultSet.getString("sender");
				String recipient = resultSet.getString("recipient");
				String message = resultSet.getString("message_content");
				messageArea.append("\n" + sender + " to " + recipient + ": " + message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to retrieve messages from database: " + e.getMessage());
		}
	}

	public void refreshMessages() {
		updateMessages(); // Method to refresh messages
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// Replace with appropriate username
			new MessageScreen("user_username");
		});
	}
}
