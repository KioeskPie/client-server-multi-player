package mt.izz.networking;

import java.net.*;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

// TODO Make individual classes.

public class Client extends Applet implements Runnable, KeyListener
{
	private static final int LEFT = 38, UP = 39, RIGHT = 40, DOWN = 41;
	private static final int SLOT_LEFT = 0, SLOT_UP = 1, SLOT_RIGHT = 2, SLOT_DOWN = 3;
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	private final int MAX_USER_COUNT = 5;
	
	int playerid, playerx, playery;
	boolean[] directions = new boolean[4];
	//left, up, right, down;
	
	int[] x = new int[MAX_USER_COUNT];
	int[] y = new int[MAX_USER_COUNT];
	
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

	private int getSlot(int code) {
		switch (code) {
			case LEFT: return SLOT_LEFT;
			case UP: return SLOT_UP;
			case RIGHT: return SLOT_RIGHT;
			case DOWN: return SLOT_DOWN;
		}
		return -1;
	}

	public void keyPressed(KeyEvent e) {
		int slot = getSlot(e.getKeyCode());
		if (slot != -1) {
			directions[slot] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int slot = getSlot(e.getKeyCode());
		if (slot != -1) {
			directions[slot] = false;
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void updateCoordinates(int pid, int x, int y){
		this.x[pid] = x;
		this.y[pid] = y;
	}

	public void paint(Graphics g){
		for (int i = 0; i < MAX_USER_COUNT;i++){
			g.drawOval(x[i], y[i], 5, 5);
		}
	}

	class Input implements Runnable
	{
		DataInputStream in;
		Client client;
	
		public Input(DataInputStream in, Client c){
			this.in = in;
			this.client = c;
		}
	
		public void run() {
			while (true) {
				try {
					int playerid = in.readInt();
					int x = in.readInt();
					int y = in.readInt();
					System.out.println(String.format("X: %d Y: %d", x, y));

					//
					client.updateCoordinates(playerid, x, y);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void run()
	{
		while (true){
			boolean doUpdate = false;
			if (directions[SLOT_RIGHT]) {
				playerx += 10;
				doUpdate = true;
			}
			if(directions[SLOT_LEFT]){
				playerx -= 10;
				doUpdate = true;
			}
			if(directions[SLOT_UP]){
				playery -= 10;
				doUpdate = true;
			}
			if(directions[SLOT_DOWN]){
				playery += 1;
				doUpdate = true;
			}
			if(doUpdate){
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