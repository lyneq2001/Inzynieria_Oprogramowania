import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpeningScreen extends JFrame {

	public OpeningScreen() {
		super("OpeningScreen"); // Set the title of the JFrame
		setSize(400, 500);     // Set the size of the JFrame
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Set default close operation
		setLocationRelativeTo(null); // Center the frame on the screen
		addGuiComponents();    // Add GUI components to the JFrame
		getContentPane().setBackground(new Color(240, 240, 240)); // Set background color
		setVisible(true);      // Make the JFrame visible
	}

	private void addGuiComponents() {
		setLayout(null); // Set layout to null for AbsoluteLayout

		// Create JLabel with Roboto font size 18 and center alignment
		JLabel helloLabel = new JLabel("Witaj w e-dzienniku", JLabel.CENTER);
		helloLabel.setBounds(50, 80, 300, 50); // Adjusted width to span the frame with padding
		helloLabel.setFont(new Font("Roboto", Font.BOLD, 18));
		helloLabel.setForeground(new Color(60, 60, 60)); // Set text color
		add(helloLabel); // Add the JLabel to the JFrame

		// Create JButton for teacher login with custom styling
		JButton loginButtonTeacher = new JButton("Nauczyciel");
		styleButton(loginButtonTeacher); // Apply custom styling to the button
		loginButtonTeacher.setBounds(100, 200, 200, 50); // Set bounds with centered position
		loginButtonTeacher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Redirect to the LoginScreenTeacher class
				dispose(); // Close the current JFrame
				new LoginScreenTeacher(); // Create and display the LoginScreen for teacher
			}
		});
		add(loginButtonTeacher); // Add the JButton to the JFrame

		// Create JButton for student login with custom styling
		JButton loginButtonStudent = new JButton("Uczeń");
		styleButton(loginButtonStudent); // Apply custom styling to the button
		loginButtonStudent.setBounds(100, 300, 200, 50); // Set bounds with centered position
		loginButtonStudent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Redirect to the LoginScreenStudent class
				dispose(); // Close the current JFrame
				new LoginScreenStudent(); // Create and display the LoginScreen for student
			}
		});
		add(loginButtonStudent); // Add the JButton to the JFrame


	}

	// Method to style buttons with a modern look
	private void styleButton(JButton button) {
		button.setFont(new Font("Roboto", Font.PLAIN, 18));
		button.setFocusPainted(false); // Remove focus painting
		button.setBackground(new Color(70, 130, 180)); // Set button background color
		button.setForeground(Color.WHITE); // Set button text color
		button.setBorder(BorderFactory.createEmptyBorder()); // Remove border
		button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
		button.setOpaque(true); // Make the button opaque
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new OpeningScreen());
	}
}
