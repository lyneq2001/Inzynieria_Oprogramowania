import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SignupScreenTeacherTest {
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockInsertStatement;
    
    private SignupScreenTeacher signupScreenTeacher;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Initialize SignupScreenTeacher with the mocked objects
        signupScreenTeacher = new SignupScreenTeacher(mockConnection, mockInsertStatement);
    }
    
    @After
    public void tearDown() throws Exception {
        signupScreenTeacher.dispose();
    }
    
    @Test
    public void testRegisterNewUser_Success() throws SQLException {
        // Setup the mock behavior
        when(mockInsertStatement.executeUpdate()).thenReturn(1);
        
        // Call the method
        boolean result = signupScreenTeacher.registerNewUser("John", "Denver", "jdoe", "password");
        
        // Verify the result
        assertTrue(result);
        verify(mockInsertStatement).setString(1, "John");
        verify(mockInsertStatement).setString(2, "Denver");
        verify(mockInsertStatement).setString(3, "jdoe");
        verify(mockInsertStatement).setString(4, "password");
        verify(mockInsertStatement).executeUpdate();
        
        // Notify the user of success
        System.out.println("Test testRegisterNewUser_Success passed successfully!");
    }
    
    @Test
    public void testRegisterNewUser_Failure() throws SQLException {
        // Setup the mock behavior
        when(mockInsertStatement.executeUpdate()).thenReturn(0); // Simulate failure
        
        // Call the method
        boolean result = signupScreenTeacher.registerNewUser("John", "Denver", "jdoe", "password");
        
        // Verify the result
        assertFalse(result);
        verify(mockInsertStatement).setString(1, "John");
        verify(mockInsertStatement).setString(2, "Denver");
        verify(mockInsertStatement).setString(3, "jdoe");
        verify(mockInsertStatement).setString(4, "password");
        verify(mockInsertStatement).executeUpdate();
        
        // Notify the user of success
        System.out.println("Test testRegisterNewUser_Failure passed successfully!");
    }
}
