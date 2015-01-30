package mt.izz.networking;

import java.net.*;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

// TODO Make individual classes.

public class Client extends Applet implements Runnable, KeyListener{
	
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	
	int playerid, uA = 5, playerx, playery;
	boolean left, up, right, down; 
	
	int[] x = new int[uA];
	int[] y = new int[uA];
	
	public void init(){
		setSize(600, 600);
		addKeyListener(this);
		
		//
		try{
		System.out.println("Connecting...");
		socket = new Socket("localhost", 7777);
		System.out.println("Connection successful.");
		
		//
		in = new DataInputStream(socket.getInputStream());
		playerid = in.readInt();
		out = new DataOutputStream(socket.getOutputStream());
		
		//
		Input input = new Input(in, this);
		Thread thread = new Thread(input);
		thread.start();
		Thread thread2 = new Thread(this);
		thread2.start();
		
		//
		
		}catch(Exception e){
			System.out.println("Unable to start client.");
		}
		
		//
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 38){//left
			left = true;
		}if(e.getKeyCode() == 39){//up
			up = true;
		}if(e.getKeyCode() == 40){//right
			right = true;
		}if(e.getKeyCode() == 41){//down
			down = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == 38){//left
			left = false;
		}if(e.getKeyCode() == 39){//up
			up = false;
		}if(e.getKeyCode() == 40){//right
			right = false;
		}if(e.getKeyCode() == 41){//down
			down = false;
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}




public void updateCoordinates(int pid, int x, int y){
	this.x[pid] = x;
	this.y[pid] = y;
}

public void paint(Graphics g){
	for(int i = 0; 1 < uA;i++){
	g.drawOval(x[i], y[i], 5, 5);
	}
}

class Input implements Runnable{
	
	DataInputStream in;
	Client client;
	
	public Input(DataInputStream in, Client c){
		this.in = in;
		this.client = c;
	}
	
	public void run() {
		while(true){
			try {
				int playerid = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				
				//
				client.updateCoordinates(playerid, x, y);
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		}
	
	}

public void run() {
	
	
	while (true){
			if(right == true){
				playerx += 10;
			}if(left == true){
				playerx -= 10;
			}if(up == true){
				playery -= 10;
			}if(down == true){
				playery += 1;
			}if(up || down || left || right){
				try {
					out.writeInt(playerid);
					out.writeInt(playerx);
					out.writeInt(playery);
				} catch (IOException e) {System.out.println("Error sending Coordinates.");}
			}
			repaint();
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateCoordinates(playerid, x[playerid], y[playerid]);
	}
}
}