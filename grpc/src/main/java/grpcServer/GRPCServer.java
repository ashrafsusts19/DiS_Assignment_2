package grpcServer;

import io.grpc.ServerBuilder;
import io.grpc.Server;

import java.io.IOException;

import grpcServices.ClientServices;

public class GRPCServer {
	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9090).addService(new ClientServices()).build();
		server.start();
		System.out.println("Server Started");
		server.awaitTermination();
	}
}
