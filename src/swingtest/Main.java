package swingtest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import com.mysql.jdbc.StringUtils;

import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

public class Main {
    private JFrame frame;
    
	private final String userName = "root";
	
	private final String password = "";

	private final String serverName = "localhost";
	
	private final int portNumber = 3306;

	private final String dbName = "test";
	
	private final String tableName = "USER";
	
    JButton mAdd,mClear,mExecute,mPrevious,mNext;
    
    JComboBox<String> mExecuteDropdown,mUpdateDropdown,mGender;
    
    JTextField mFirstName,mSSN,mSurname,mSalary,mDOB,mSearchField,mUpdateField; 
    
    JLabel mErrorText,mFirstNameLabel,mSSNLabel,mSurnameLabel,mSalaryLabel,mGenderLabel,mDOBLabel,mSearchFieldLabel,mUpdateFieldLabel;
    

    public static void main(String[] args) {
        try {
        	Main window = new Main();
            window.frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main() {
        initialize();
    }
    
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.serverName + ":" + this.portNumber + "/" + this.dbName,
				connectionProps);

		return conn;
	}
	
	public JLabel addUser(JLabel mErrorText, JTextField mFirstName, JTextField mSurname, JTextField mSSN, JTextField mDOB, JTextField mSalary, JComboBox<String> gender) {
		Connection connection;
		Statement s;
		try {
			connection = getConnection();
			s = connection.createStatement();
		
			final String SQL_INSERT = "INSERT INTO USER (FIRSTNAME,SURNAME, SALARY,GENDER,SSN,DOB) VALUES ('" + mFirstName.getText() +"','" + mSurname.getText() + "','"+ mSalary.getText() +"','"+ gender.getSelectedItem().toString() +"','"+ mSSN.getText() +"','" + mDOB.getText() + "')";
			this.executeUpdate(connection,SQL_INSERT);
			System.out.println("Inserted new User");
			s.close();
		} catch (SQLException e) {
			if(e.getMessage().contains("Duplicate entry")) {
				mErrorText.setText(e.getMessage());
			}else if(e.getMessage().contains("Incorrect integer value")){
				mErrorText.setText("SSN can only contain Integers");
			}else if(e.getMessage().contains("Incorrect date value:")) {
				mErrorText.setText("Format only avilable like YYYY-MM-DD");
			}
			else {
				e.printStackTrace();
				mErrorText.setText("Unknown Error");
			}
		}
		return mErrorText;
	}
	
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	
	public void ListDataNext(ResultSet rs) throws SQLException {
			rs.next();
			String firstname = rs.getString("FIRSTNAME");
			String surname = rs.getString("SURNAME");
			String salary = rs.getString("SALARY");
			String gender = rs.getString("GENDER");
			String ssn = rs.getString("SSN");
			String dob = rs.getString("DOB");
			
			mFirstName.setText(firstname);
    		mSurname.setText(surname);
    		mSalary.setText(salary);
    		mGender.setSelectedItem(gender);
    		mSSN.setText(ssn);
    		mDOB.setText(dob);
	}
	
	public void ListDataPrevious(ResultSet rs) throws SQLException {
			rs.previous();
			String firstname = rs.getString("FIRSTNAME");
			String surname = rs.getString("SURNAME");
			String salary = rs.getString("SALARY");
			String gender = rs.getString("GENDER");
			String ssn = rs.getString("SSN");
			String dob = rs.getString("DOB");
			
			mFirstName.setText(firstname);
    		mSurname.setText(surname);
    		mSalary.setText(salary);
    		mGender.setSelectedItem(gender);
    		mSSN.setText(ssn);
    		mDOB.setText(dob);
	}
	
    public void ExecuteCommands() {
    	String selectedModel = mExecuteDropdown.getSelectedItem().toString();
		String selectedUpdate = mUpdateDropdown.getSelectedItem().toString();
		String SQL_COMMAND = null;
		if(selectedModel.toUpperCase().equals("UPDATE")) {
		SQL_COMMAND = "UPDATE user SET "+ selectedUpdate +"=" + mUpdateField.getText() + " WHERE SURNAME='"+ mSearchField.getText() +"'";
		System.out.println("Updating a user");
		}else if(selectedModel.toUpperCase().equals("DELETE")) {
		SQL_COMMAND = "DELETE FROM USER WHERE SURNAME='"+ mSearchField.getText() +"'";
		System.out.println("Deleting a user");
		}else if(selectedModel.toUpperCase().equals("SEARCH")) {
		SQL_COMMAND = "SELECT * FROM USER WHERE SURNAME='"+ mSearchField.getText() +"'";
		System.out.println("Search a user");
		}
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getConnection().createStatement();
			if(selectedModel.toUpperCase().equals("SEARCH")) {
				ListData(rs,stmt,SQL_COMMAND);
			}
			else {
				System.out.println("Updating rows");
				this.executeUpdate(getConnection(),SQL_COMMAND);
				if(selectedModel.toUpperCase().equals("DELETE")) {
					mErrorText.setText("Deleted a user");
				}else {
					mErrorText.setText("Updated a user");
				}
			}
		}catch(Exception e) {
			mErrorText.setText("Unknown Error");
			e.printStackTrace();
		}
    }

    private void ListData(ResultSet rs, Statement stmt, final String SQL_COMMAND) throws SQLException {
    	rs = stmt.executeQuery(SQL_COMMAND);
    	while(rs.next()) {
    	String firstname = rs.getString("FIRSTNAME");
		String surname = rs.getString("SURNAME");
		String salary = rs.getString("SALARY");
		String gender = rs.getString("GENDER");
		String ssn = rs.getString("SSN");
		String dob = rs.getString("DOB");
		
		mFirstName.setText(firstname);
		mSurname.setText(surname);
		mSalary.setText(salary);
		mGender.setSelectedItem(gender);
		mSSN.setText(ssn);
		mDOB.setText(dob);
		
		System.out.println("Changed search values");
    	}
	}

	private void initialize() {
        frame = new JFrame();
        
        mErrorText = new JLabel();
        mErrorText.setBounds(260,50, 500,80);
        
        mFirstNameLabel = new JLabel("First Name");
        mFirstNameLabel.setBounds(50,20, 200,30);
        mFirstName=new JTextField();  
        mFirstName.setBounds(50,50, 200,30);  
        
        mSurnameLabel = new JLabel("Surname");
        mSurnameLabel.setBounds(50,70, 200,30);
        mSurname=new JTextField();  
        mSurname.setBounds(50,100, 200,30); 
        
        mSSNLabel = new JLabel("Social Security Number");
        mSSNLabel.setBounds(50,120, 200,30);
        mSSN=new JTextField();  
        mSSN.setBounds(50,150, 200,30); 
        
        mSalaryLabel = new JLabel("Salary");
        mSalaryLabel.setBounds(50,170, 200,30);
        mSalary=new JTextField();  
        mSalary.setBounds(50,200, 200,30); 
        
        mGenderLabel = new JLabel("Gender");
        mGenderLabel.setBounds(50,220, 200,30);
        String[] gender = {  "NONE", "MALE", "FEMALE", "OTHER"};
        
        mGender = new JComboBox<String>(gender);  
        mGender.setBounds(50,250, 200,30); 

        
        mDOBLabel = new JLabel("Date of Birth");
        mDOBLabel.setBounds(50,270, 200,30);
        mDOB=new JTextField(); 
        mDOB.setBounds(50,300, 200,30);
       
        
        mAdd= new JButton("Add");
        mAdd.setBounds(260,150, 100,30);
        
        mClear = new JButton("Clear");
        mClear.setBounds(260,300,100,30);
        
        mSearchFieldLabel = new JLabel("Search");
        mSearchFieldLabel.setBounds(160,320, 200,30);
        mSearchField=new JTextField();  
        mSearchField.setBounds(160,350, 200,30); 
        
        mExecute = new JButton("Execute");
        mExecute.setBounds(370,350, 100,30);
        
        String[] commands = { "NONE", "DELETE", "UPDATE", "SEARCH" };
        mExecuteDropdown = new JComboBox<String>(commands);
        mExecuteDropdown.setBounds(50,350, 100,30);
        
        String[] columnNames = { "FIRSTNAME", "SURNAME", "SSN", "DOB", "SALARY", "GENDER" };
        
        mUpdateDropdown = new JComboBox<String>(columnNames);
        mUpdateDropdown.setBounds(50,400, 100,30);
        
        mUpdateFieldLabel = new JLabel("Update");
        mUpdateFieldLabel.setBounds(160,370, 200,30);
        mUpdateField = new JTextField();
        mUpdateField.setBounds(160,400, 200,30);
        
        mPrevious = new JButton("Previous");
        mPrevious.setBounds(260,200, 100,30);
        
        mNext = new JButton("Next");
        mNext.setBounds(260,250, 100,30);
        
        Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SELECT * FROM USER");
		}catch(Exception e) {
			
		}
        
        mExecute.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
        	  ExecuteCommands();
          }
        });
        
        mAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		if(!StringUtils.isNullOrEmpty(mFirstName.getText()) && !StringUtils.isNullOrEmpty(mSurname.getText()) 
        				&& !StringUtils.isNullOrEmpty(mSalary.getText()) && mGender.getSelectedItem() != "NONE"
        				&& !StringUtils.isNullOrEmpty(mSSN.getText()) && !StringUtils.isNullOrEmpty(mDOB.getText())) {
            	addUser(mErrorText,mFirstName,mSurname,mSSN,mDOB,mSalary,mGender);
        		}else {
        			mErrorText.setText("Please fill in the text fields");
        		}
            }
        });
        
        mNext.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionevent)
            {
            	Statement stmt = null;
        		ResultSet rs = null;
            	try {
        			stmt = getConnection().createStatement();
        			rs = stmt.executeQuery("SELECT * FROM user");
        			ListDataNext(rs);
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
            }
        });
        
        mPrevious.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionevent)
            {
            	Statement stmt = null;
        		ResultSet rs = null;
            	try {
        			stmt = getConnection().createStatement();
        			rs = stmt.executeQuery("SELECT * FROM user");
        			ListDataPrevious(rs);
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
            }
        });
        
        mClear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		mFirstName.setText("");
        		mSurname.setText("");
        		mSalary.setText("");
        		mGender.setSelectedItem("NONE");
        		mSSN.setText("");
        		mDOB.setText("");
            }
          });
      
        
        frame.add(mFirstName); 
        frame.add(mSurname);  
        frame.add(mSSN);
        frame.add(mDOB);
        frame.add(mSalary);
        frame.add(mGender);
        frame.add(mSearchField);
        frame.add(mAdd);
        frame.add(mClear);
        frame.add(mExecute);
        frame.add(mExecuteDropdown);
        frame.add(mUpdateDropdown);
        frame.add(mErrorText);
        frame.add(mUpdateField);
        frame.add(mPrevious);
        frame.add(mNext);
        frame.add(mFirstNameLabel);
        frame.add(mSurnameLabel);
        frame.add(mGenderLabel);
        frame.add(mDOBLabel);
        frame.add(mSSNLabel);
        frame.add(mSalaryLabel);
        frame.add(mUpdateFieldLabel);
        frame.add(mSearchFieldLabel);
        
        frame.setLayout(null);  
        frame.setVisible(true);  
        frame.setBounds(50, 50, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
