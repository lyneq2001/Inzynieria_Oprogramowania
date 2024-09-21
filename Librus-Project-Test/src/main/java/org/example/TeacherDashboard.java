import javax.swing.*;
import java.awt.*;

public class TeacherDashboard extends JFrame {
	private String username;

	public TeacherDashboard(String username) {
		super("Teacher Dashboard");
		this.username = username;
		setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Center the window on the screen
		initializeComponents();
		getContentPane().setBackground(new Color(240, 240, 240)); // Set background color for the content pane
		setVisible(true);
	}

	private void initializeComponents() {
		setLayout(new BorderLayout());

		// Create a label for the dashboard title
		JLabel titleLabel = new JLabel("Witaj, " + username, JLabel.CENTER);
		titleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
		titleLabel.setForeground(new Color(60, 60, 60)); // Darker text color for contrast
		add(titleLabel, BorderLayout.NORTH);

		// Create a panel for the dashboard buttons
		JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		buttonPanel.setBackground(new Color(70, 130, 180)); // Set background color to blue

		// Add buttons for various functionalities
		JButton viewGradesButton = new JButton("Zobacz oceny");
		JButton viewMessageButton = new JButton("Zobacz wiadomości");
		JButton calendarButton = new JButton("Kalendarz");
		JButton profileButton = new JButton("Profil"); // Button for profile management

		// Style buttons
		styleButton(viewGradesButton);
		styleButton(viewMessageButton);
		styleButton(calendarButton);
		styleButton(profileButton);

		buttonPanel.add(viewGradesButton);
		buttonPanel.add(viewMessageButton);
		buttonPanel.add(calendarButton);
		buttonPanel.add(profileButton); // Add profile button to the panel

		add(buttonPanel, BorderLayout.CENTER);

		// Add action listeners to buttons
		viewGradesButton.addActionListener(e -> new ViewGradesScreen(username, "teacher"));
		viewMessageButton.addActionListener(e -> new MessageScreen(username));
		calendarButton.addActionListener(e -> new CalendarDashboard(username, "teacher"));
		profileButton.addActionListener(e -> new TeacherProfile(username)); // Open profile screen for teacher
	}

	// Method to style buttons with a modern look
	private void styleButton(JButton button) {
		button.setFont(new Font("Roboto", Font.PLAIN, 16));
		button.setFocusPainted(false);
		button.setBackground(new Color(255, 255, 255)); // White background for buttons
		button.setForeground(new Color(70, 130, 180)); // Blue text color
		button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2)); // Blue border
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TeacherDashboard("Teacher"));
	}
}
