package com.example.androidsocketfiletransferclient;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText editTextAddress;
	TextView textPort;

	static final int SocketServerPORT = 8080;
	ServerSocketThread serverSocketThread;
	ServerSocket serverSocket;

	private String IP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextAddress = (EditText) findViewById(R.id.address);
		textPort = (TextView) findViewById(R.id.port);
		textPort.setText("port: " + SocketServerPORT);

		IP = getIpAddress();

		startService(new Intent(getBaseContext(), ServerSocketThread.class));
	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += inetAddress.getHostAddress() + "\n";
						ip.trim();
					}

				}

			}

		} catch (SocketException e) {
			e.printStackTrace();
			ip += "Peer Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}

	public void mySharedFiles(View view) {
		Intent i = new Intent(this, MySharedFilesActivity.class);
		startActivity(i);
	}

	public void sendToTracker(View view) {
		ClientRxThread clientRxThread = new ClientRxThread(editTextAddress.getText().toString(), SocketServerPORT);

		clientRxThread.start();
	}

	private class ClientRxThread extends Thread {
		String dstAddress;
		int dstPort;
		ArrayList<String> fromTrackerList;

		ClientRxThread(String address, int port) {
			dstAddress = address;
			dstPort = port;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			Socket socket = null;

			try {
				socket = new Socket(dstAddress, dstPort);

				String path = Environment.getExternalStorageDirectory().toString() + "/Ringtones";

				File f = new File(path);
				final File[] fileList = f.listFiles();

				final ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < fileList.length; i++) {
					String fileName = fileList[i].getName();
					list.add(IP + " : " + fileName);
				}

				// Send the files to the tracker.
				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
					try {
						objectOutput.writeObject(list);
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out.println("The socket for reading the object has problem");
					e.printStackTrace();
				}

				// Get the list of file names from tracker
				try {
					ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
					try {
						Object object = objectInput.readObject();
						fromTrackerList = (ArrayList<String>) object;
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out.println("PEER The socket for reading the object has problem");
					e.printStackTrace();
				}

				socket.close();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Going to TFLA", Toast.LENGTH_LONG).show();

						Intent i = new Intent(MainActivity.this, TrackerFileListActivity.class);
						i.putStringArrayListExtra("fromTrackerList", fromTrackerList);
						startActivity(i);
					}
				});

			} catch (IOException e) {

				e.printStackTrace();

				final String eMsg = "Something wrong: " + e.getMessage();
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, eMsg, Toast.LENGTH_LONG).show();
					}
				});

			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
