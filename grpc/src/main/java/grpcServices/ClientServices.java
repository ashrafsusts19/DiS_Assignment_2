package grpcServices;

import com.ashrafsusts19.grpc.User.APIRegisterResponse;
import com.ashrafsusts19.grpc.User.APIResponse;
import com.ashrafsusts19.grpc.User.APIUpdateResponse;
import com.ashrafsusts19.grpc.User.EmptyMessage;
import com.ashrafsusts19.grpc.User.LoginRequest;
import com.ashrafsusts19.grpc.User.RegisterRequest;
import com.ashrafsusts19.grpc.User.UpdateRequest;
import com.ashrafsusts19.grpc.userGrpc.userImplBase;

import io.grpc.stub.StreamObserver;;

public class ClientServices extends userImplBase {

	@Override
	public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
		
		String username = request.getUsername();
		String password = request.getPassword();
		
		APIResponse.Builder response = APIResponse.newBuilder();
		if (username.equals(password)) {
			response.setResponseCode(0).setResponseMessage("SUCCESS");
			response.setEmail("helloWorld@gmail.com");
			response.setInstitution("SUST");
			response.setPhone("0123456");
			
		}
		else {
			response.setResponseCode(100).setResponseMessage("FAILURE");
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
			response.setResponseCode(0).setResponseMessage("SUCCESS");				
		}
		else {
			response.setResponseCode(100).setResponseMessage("FAILURE");
		}
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
		
	}

	@Override
	public void update(UpdateRequest request, StreamObserver<APIUpdateResponse> responseObserver) {
		
		
	}
	
	
	

}
