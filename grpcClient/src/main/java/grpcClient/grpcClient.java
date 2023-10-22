package grpcClient;

import java.util.Scanner;

import com.ashrafsusts19.grpc.User.APIRegisterResponse;
import com.ashrafsusts19.grpc.User.APIResponse;
import com.ashrafsusts19.grpc.User.APIUpdateResponse;
import com.ashrafsusts19.grpc.User.LoginRequest;
import com.ashrafsusts19.grpc.User.RegisterRequest;
import com.ashrafsusts19.grpc.User.UpdateRequest;
import com.ashrafsusts19.grpc.userGrpc;
import com.ashrafsusts19.grpc.userGrpc.userBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class grpcClient {
	public ManagedChannel channel;
	public userBlockingStub userStub;
	private int menuCode;
	private String clientUsername;
	private String clientEmail;
	private String clientPhoneNo;
	private String clientInstitution;
	public static void main(String[] args) {
		
		
		grpcClient client = new grpcClient();
		
		while (!client.channel.isShutdown()) {
			client.updateDisplay();
		}
		
		client.channel.shutdown();
		
	}
	
	public grpcClient() {
		this.channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		this.userStub = userGrpc.newBlockingStub(channel); 
		this.menuCode = 0;
		
	}
	
	public void updateDisplay() {
		switch (this.menuCode) {
		case 0:
			this.showMainMenu();
			break;
		case 1:
			this.userLogin();
			break;
		case 2:
			this.userRegister();
			break;
		case 3:
			this.userInfo();
			break;
		}
	}
	
	public void userInfo() {
		System.out.println("");
		System.out.println("Username: " + this.clientUsername);
		System.out.println("1 - " + "Email: " + this.clientEmail);
		System.out.println("2 - " + "Phone: " + this.clientPhoneNo);
		System.out.println("3 - " + "Institution: " + this.clientInstitution);
		System.out.println("4 - " + "Change Password");
		System.out.println("Press 1-4 to update information");
		System.out.println("5 - Logout");
		System.out.println("6 - Exit");
		System.out.println("");
		
		Scanner scanner = new Scanner(System.in);
		int command = 0;
		boolean passwordChanged = false;
		String newPassword = "";
		command = scanner.nextInt();
		scanner.nextLine();
		switch (command) {
		case 1:
			System.out.println("New Email:");				
			String Email = scanner.nextLine();
			this.clientEmail = Email;
			break;
		case 2:
			System.out.println("New Phone:");
			String PhoneNo = scanner.nextLine();
			this.clientPhoneNo = PhoneNo;
			break;
		case 3:
			System.out.println("New Institution:");
			String Institution = scanner.nextLine();
			this.clientInstitution = Institution;
			break;
		case 4:
			System.out.println("New Password:");
			newPassword = scanner.nextLine();
			passwordChanged = true;
			break;
		}
			
		if (command == 5) {
			this.clientUsername = "";
			this.clientEmail = "";
			this.clientPhoneNo = "";
			this.clientInstitution = "";
			this.menuCode = 0;
		}
		else if (command == 6){
			this.channel.shutdown();
		}
		else {
			UpdateRequest.Builder builder= UpdateRequest.newBuilder()
					.setEmail(clientEmail)
					.setInstitution(clientInstitution)
					.setPhone(clientPhoneNo)
					.setUsername(clientUsername);
			if (passwordChanged) {
				builder.setPassword(newPassword);
			}
			else {
				builder.setPassword("");
			}
			
			UpdateRequest updateRequest = builder.build();
			APIUpdateResponse updateResponse = userStub.update(updateRequest);
		}
	}
	
	public void showMainMenu(){
		System.out.println("1 - Login\n2 - Register\n3 - Exit");
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		if (choice == 1) {
			this.menuCode = 1;
		}
		else if (choice == 2){
			this.menuCode = 2;
		}
		else {
			this.channel.shutdown();
		}
		
	}
	
	public void userLogin() {
		
		String _username, _password;
		Scanner scanner = new Scanner(System.in);
		System.out.println("");
		System.out.print("Username: ");
		_username = scanner.nextLine();
		System.out.print("Password: ");
		_password = scanner.nextLine();

		LoginRequest loginRequest = LoginRequest.newBuilder().setUsername(_username).setPassword(_password).build();
			
		APIResponse apiResponse = userStub.login(loginRequest);
		
		
		System.out.println(apiResponse.getResponseMessage());
		System.out.println("");
		if (apiResponse.getResponseCode() == 0) {
			this.clientEmail = apiResponse.getEmail();
			this.clientInstitution = apiResponse.getInstitution();
			this.clientPhoneNo = apiResponse.getPhone();
			this.clientUsername = _username;
			this.menuCode = 3;
		}
		else {
			this.menuCode = 0;
		}
	}
	
	public void userRegister() {
		String _username, _password, _confirmPassword;
		Scanner scanner = new Scanner(System.in);
		System.out.println("");
		System.out.print("Username: ");
		_username = scanner.nextLine();
		System.out.print("Password: ");
		_password = scanner.nextLine();
		System.out.print("Confirm Password: ");
		_confirmPassword = scanner.nextLine();
		
		RegisterRequest registerRequest = RegisterRequest.newBuilder().setUsername(_username).setPassword(_password)
				.setConfirmPassword(_confirmPassword).build();
		
		APIRegisterResponse apiRegisterResponse = userStub.register(registerRequest);
		System.out.println(apiRegisterResponse.getResponseMessage());
		System.out.println("");
		this.menuCode = 0;
	}
	
}
