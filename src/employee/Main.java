package employee;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    JComboBox<String> mExecuteDropdown,mUpdateDropdown,mGender,mSearchDropdown;
    
    JTextField mFirstName,mSSN,mSurname,mSalary,mDOB,mSearchField,mUpdateField; 
    
    JLabel mErrorText,mFirstNameLabel,mSSNLabel,mSurnameLabel,mSalaryLabel,mGenderLabel,mDOBLabel,mSearchFieldLabel,mUpdateFieldLabel;
    
    ResultSet resultset = null;
    
//  Main
    public static void main(String[] args) {
        try {
        	Main window = new Main();
            window.frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
// 	Initialise
    public Main() {
        initialize();
    }
    
//  Connect to MySql Database 
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
	
//	Add User to Database 
	public JLabel addUser(JLabel mErrorText, JTextField mFirstName, JTextField mSurname, JTextField mSSN, JTextField mDOB, JTextField mSalary, JComboBox<String> gender) {
		Connection connection;
		Statement s;
		try {
			connection = getConnection();
			s = connection.createStatement();
			PreparedStatement preparedStmt = connection.prepareStatement("INSERT INTO USER (FIRSTNAME,SURNAME, SALARY,GENDER,SSN,DOB) VALUES (?,?,?,?,?,?)");
			preparedStmt.setString(1, mFirstName.getText());
			preparedStmt.setString(2, mSurname.getText());
			preparedStmt.setString(3, mSalary.getText());
			preparedStmt.setString(4, gender.getSelectedItem().toString());
			preparedStmt.setString(5, mSSN.getText());
			preparedStmt.setString(6, mDOB.getText());

			preparedStmt.executeUpdate();
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
	
//	List the Next Value in list
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
	
//	List the previous value in the list
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
	
//	Execute UPDATE,DELETE,SEARCH
    public void ExecuteCommands() throws SQLException {
    	String selectedModel = mExecuteDropdown.getSelectedItem().toString();
		String selectedUpdate = mUpdateDropdown.getSelectedItem().toString();
		String replacedstring =  mSearchDropdown.getSelectedItem().toString();
		System.out.println(replacedstring);
		Connection conn = getConnection();
		PreparedStatement preparedStmt = null;
		if(selectedModel.toUpperCase().equals("UPDATE")) {
			
			preparedStmt = conn.prepareStatement("UPDATE user SET " + selectedUpdate + "=? WHERE "+replacedstring+"=?");
			preparedStmt.setString(1, mUpdateField.getText());
			preparedStmt.setString(2, mSearchField.getText());
			System.out.println("Updating a user");
		
		}else if(selectedModel.toUpperCase().equals("DELETE")) {
			
			preparedStmt = conn.prepareStatement("DELETE FROM user WHERE "+replacedstring+"=?");
			preparedStmt.setString(1,mSearchField.getText());
			System.out.println("Deleting a user");
			
		}else if(selectedModel.toUpperCase().equals("SEARCH")) {

			preparedStmt = conn.prepareStatement("SELECT * FROM user WHERE "+ replacedstring+"=?");
			preparedStmt.setString(1, mSearchField.getText());
			
		}
			if(selectedModel.toUpperCase().equals("SEARCH")) {
				ListData(preparedStmt);
				mErrorText.setText("Search a user");
			}
			else {
				System.out.println("Execute update rows");
				preparedStmt.executeUpdate();
			if(selectedModel.toUpperCase().equals("DELETE")) {
				mErrorText.setText("Deleted a user");
			}else {
				mErrorText.setText("Updated a user");
			}
		}
    }

    private void ListData(PreparedStatement preparedStmt) throws SQLException {
    	resultset = preparedStmt.executeQuery();
    	while(resultset.next()) {
    	String firstname = resultset.getString("FIRSTNAME");
		String surname = resultset.getString("SURNAME");
		String salary = resultset.getString("SALARY");
		String gender = resultset.getString("GENDER");
		String ssn = resultset.getString("SSN");
		String dob = resultset.getString("DOB");
		
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
        mSearchFieldLabel.setBounds(270,325, 200,30);
        mSearchField=new JTextField();  
        mSearchField.setBounds(270,350, 200,30); 
        
        mExecute = new JButton("Execute");
        mExecute.setBounds(470,350, 100,30);
        
        String[] commands = { "NONE", "DELETE", "UPDATE", "SEARCH" };
        mExecuteDropdown = new JComboBox<String>(commands);
        mExecuteDropdown.setBounds(50,350, 100,30);
        
        String[] columnNames = { "FIRSTNAME", "SURNAME", "SSN", "DOB", "SALARY", "GENDER" };
        
        mUpdateDropdown = new JComboBox<String>(columnNames);
        mUpdateDropdown.setBounds(50,400, 100,30);
        
        mSearchDropdown = new JComboBox<String>(columnNames);
        mSearchDropdown.setBounds(160,350, 100,30);
        
        mUpdateFieldLabel = new JLabel("Update");
        mUpdateFieldLabel.setBounds(160,375, 200,30);
        mUpdateField = new JTextField();
        mUpdateField.setBounds(160,400, 200,30);
        
        mPrevious = new JButton("Previous");
        mPrevious.setBounds(260,200, 100,30);
        
        mNext = new JButton("Next");
        mNext.setBounds(260,250, 100,30);
        
        Statement stmt = null;
		try {
			stmt = getConnection().createStatement();
			resultset = stmt.executeQuery("SELECT * FROM USER");
		}catch(Exception e) {
			e.printStackTrace();
		}
        
//		Execute command
        mExecute.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
        	  try {
				ExecuteCommands();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
          }
        });
        
//      Add User to Database
        mAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		if(!StringUtils.isNullOrEmpty(mFirstName.getText()) && !StringUtils.isNullOrEmpty(mSurname.getText()) 
        				&& !StringUtils.isNullOrEmpty(mSalary.getText()) && mGender.getSelectedItem() != "NONE"
        				&& !StringUtils.isNullOrEmpty(mSSN.getText()) && !StringUtils.isNullOrEmpty(mDOB.getText())) {
        			if(mDOB.getText().matches("\\d{4}-\\d{2}-\\d{2}") && mSSN.getText().matches("[0-9]+")) {
        				addUser(mErrorText,mFirstName,mSurname,mSSN,mDOB,mSalary,mGender);
        				mErrorText.setText("");
        			}else {
        				if(!mSSN.getText().matches("[0-9]+")) {
        					mErrorText.setText("SSN should only contain Integers  ");
        				}else {
        					mErrorText.setText("Invalid Format should be YYYY-MM-DD");
        				}
        			}
        		}else {
        			mErrorText.setText("Please fill in the text fields");
        		}
            }
        });
        
//      Get Next User 
        mNext.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionevent)
            {
        			try {
						ListDataNext(resultset);
					} catch (SQLException e) {
						if(e.getMessage().contains("After end of result set")) {
							mErrorText.setText("No more next data");
						}else {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
					}
            }
        });
        
//      Get the Previous user
        mPrevious.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionevent)
            {
        			try {
						ListDataPrevious(resultset);
					} catch (SQLException e) {
						if(e.getMessage().contains("Before start of result set")) {
							mErrorText.setText("No more previous data");
						}else {
							e.printStackTrace();
						}
					}
            }
        });
        
//      Clear all data and set the back the resultset back to normal
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
        		Statement stmt = null;
        		try {
        			stmt = getConnection().createStatement();
					resultset= stmt.executeQuery("SELECT * FROM USER");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
        frame.add(mSearchDropdown);
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
