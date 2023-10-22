package grpcServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


import com.ashrafsusts19.grpc.User.APIRegisterResponse;
import com.ashrafsusts19.grpc.User.APIResponse;
import com.ashrafsusts19.grpc.User.APIUpdateResponse;
import com.ashrafsusts19.grpc.User.EmptyMessage;
import com.ashrafsusts19.grpc.User.LoginRequest;
import com.ashrafsusts19.grpc.User.RegisterRequest;
import com.ashrafsusts19.grpc.User.UpdateRequest;
import com.ashrafsusts19.grpc.userGrpc.userImplBase;

import io.grpc.stub.StreamObserver;

public class ClientServices extends userImplBase {

	private String dbmsUrl = "jdbc:mysql://localhost:3306/dis2_assignment";
	private String username = "root";
	private String password = "";
	Connection dbmsConnection;
	
	private class User{
		public String username;
		public String hashedPassword;
		public String salt;
		public String email;
		public String phoneNo;
		public String institution;
	}
	
	
//	public ClientServices() {
//		super();
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			
//			dbmsConnection = DriverManager.getConnection(this.dbmsUrl, this.username, this.password);
//			
//		}
//		catch (Exception e) {
//			System.out.println(e);
//		}
//		
//	}
	
	private User getData(String username) {
		User user = new User();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			dbmsConnection = DriverManager.getConnection(this.dbmsUrl, this.username, this.password);
			
			Statement statement = dbmsConnection.createStatement();
			String qry = "Select * from users where username = '" + username + "'";
			ResultSet resultSet = statement.executeQuery(qry);
			
			if (resultSet.next()) {
				user.username = resultSet.getString(1);
				user.hashedPassword = resultSet.getString(2);
				user.salt = resultSet.getString(3);
				user.email = resultSet.getString(4);
				user.phoneNo = resultSet.getString(5);
				user.institution = resultSet.getString(6);
			}
			
			dbmsConnection.close();
			
		}
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return user;
	}
	
	private void updateDatabase(User user) throws Exception {

		Class.forName("com.mysql.cj.jdbc.Driver");
		
		dbmsConnection = DriverManager.getConnection(this.dbmsUrl, this.username, this.password);
		
		String qry;
		if (user.hashedPassword.equals("")) {
			qry = "UPDATE users\r\n"
					+ "SET email = '" + user.email + "', phoneNo = '" + user.phoneNo + "', institution = '" + user.institution + "'\n"
					+ "WHERE username = '" + user.username + "';";
		}
		else {
			qry = "UPDATE users\r\n"
					+ "SET email = '" + user.email + "', hashedPassword = '" + user.hashedPassword + "', salt = '" + user.salt + "', phoneNo = '" + user.phoneNo + "', institution = '" + user.institution + "'\n"
					+ "WHERE username = '" + user.username + "';";
		}
		PreparedStatement pstmt = dbmsConnection.prepareStatement(qry);
		pstmt.executeUpdate();
		
		
		dbmsConnection.close();

		
	}
	
	
	private void addNewUser(User user) throws Exception {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		
		dbmsConnection = DriverManager.getConnection(this.dbmsUrl, this.username, this.password);
		
		String qry;
		qry = "insert into users values ('" + user.username + "', '" + user.hashedPassword + 
				"', '" + user.salt + "', '" + user.email + "', '" + user.phoneNo + "', '" + user.institution  + "')";
		
		
		PreparedStatement pstmt = dbmsConnection.prepareStatement(qry);
		pstmt.executeUpdate();
		
		dbmsConnection.close();
			
		
	}
	
	
	@Override
	public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
		
		String username = request.getUsername();
		String password = request.getPassword();
		APIResponse.Builder response = APIResponse.newBuilder();
		User chosenuser = this.getData(username);
		if (chosenuser != null && chosenuser.username != null && chosenuser.username.equals(username)) {
			if (PasswordUtils.verifyUserPassword(password, chosenuser.hashedPassword, chosenuser.salt)) {
				response.setResponseCode(0).setResponseMessage("SUCCESS");
				response.setEmail(chosenuser.email);
				response.setInstitution(chosenuser.institution);
				response.setPhone(chosenuser.phoneNo);
			}			
			else {
				response.setResponseCode(1).setResponseMessage("WRONG PASSWORD");
			}
		}

		else {
			response.setResponseCode(2).setResponseMessage("NO SUCH USER FOUND");
		}
		
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	@Override
	public void logout(EmptyMessage request, StreamObserver<APIResponse> responseObserver) {
		
	}

	@Override
	public void register(RegisterRequest request, StreamObserver<APIRegisterResponse> responseObserver) {
		String username = request.getUsername();
		String password = request.getPassword();
		String confirmpassword = request.getConfirmPassword();
		
		APIRegisterResponse.Builder response = APIRegisterResponse.newBuilder();
		if (password.equals(confirmpassword)) {
			User user = getData(username);
			if (user == null || user.username == null || !user.username.equals(username)) {
			
				String salt = PasswordUtils.getSalt(30);
				String hashedPassword = PasswordUtils.generateSecurePassword(password, salt);
				
				User newUser = new User();
				newUser.username = username;
				newUser.hashedPassword = hashedPassword;
				newUser.salt = salt;
				newUser.email = "";
				newUser.institution = "";
				newUser.phoneNo = "";
				try {
					this.addNewUser(newUser);
					response.setResponseCode(0).setResponseMessage("SUCCESS");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setResponseCode(100).setResponseMessage("FAILURE");
				}
				response.setResponseCode(0).setResponseMessage("SUCCESS");
			
				
			}
			else {
				response.setResponseCode(2).setResponseMessage("USERNAME TAKEN");
			}
							
		}
		else {
			response.setResponseCode(1).setResponseMessage("PASSWORDS DON'T MATCH");
		}
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
		
	}

	@Override
	public void update(UpdateRequest request, StreamObserver<APIUpdateResponse> responseObserver) {
		
		User user = new User();
		user.username = request.getUsername();
		user.email = request.getEmail();
		user.institution = request.getInstitution();
		user.phoneNo = request.getPhone();
		
		APIUpdateResponse.Builder response = APIUpdateResponse.newBuilder();
		
		if (!request.getPassword().equals("")) {
			String salt = PasswordUtils.getSalt(30);
			String hashedPassword = PasswordUtils.generateSecurePassword(request.getPassword(), salt);
			user.hashedPassword = hashedPassword;
			user.salt = salt;
		}
		else {
			user.hashedPassword = "";
		}
		try {
			this.updateDatabase(user);
			response.setResponseCode(0).setResponseMessage("Success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setResponseCode(100).setResponseMessage(e.getMessage());
		}
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}
	
	
	

}
