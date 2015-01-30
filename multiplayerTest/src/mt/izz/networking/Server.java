
package mt.izz.networking;
import java.io.*;
import java.net.*;

public class Server {
	private static int uA = 5;
	static ServerSocket serverSocket;
	static Socket socket;
	static DataOutputStream out;
	static Users[] user = new Users[uA];
	static DataInputStream in;
	
	public static void main(String[] args) throws Exception{
		System.out.println("Starting server...");
		serverSocket = new ServerSocket(7777);
		System.out.println("Server started...");
		while(true){
			socket = serverSocket.accept();
			for (int i = 0; i < uA; i++){
				if(user[i]==null) {
					System.out.println("Connection from: " + socket.getInetAddress());
					out = new DataOutputStream(socket.getOutputStream());
					System.out.println("Data has been sent.");
					in = new DataInputStream(socket.getInputStream());
					
						user[i] = new Users(out, in, user,i);
						Thread thread = new Thread(user[i]);
						thread.start();
						break;
					}
				}
			}
		}
		
	}


class Users implements Runnable{

	private static int uA = 5;
	DataOutputStream out;
	DataInputStream in;
	Users[] user = new Users[uA]; 
	String name;
	int playerid;
	
	//
	int playeridin, xin, yin;
	
	public Users(DataOutputStream out, DataInputStream in, Users[] user, int pid){
		this.out = out;
		this.in = in;
		this.user = user;
		this.playerid = pid;
	}
	
	public void run() {
		try {
			out.writeInt(playerid);
		} catch (IOException e1) {
			System.out.println("Failed to send PlayerID.");
		}
		while(true){
				try {
					playeridin = in.readInt();
					xin = in.readInt();
					yin = in.readInt();
					
					for(int i = 0; i < uA; i++){
						if(user[i]!=null){
							user[i].out.writeInt(playeridin);
							user[i].out.writeInt(xin);
							user[i].out.writeInt(yin);
						}
					}
					
				} catch (IOException e) {
					user[playerid] = null;
				}
		}
	}

}
