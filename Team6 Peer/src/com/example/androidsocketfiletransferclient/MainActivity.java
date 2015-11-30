package com.example.androidsocketfiletransferclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText editTextAddress;
	// Button buttonConnect;
	TextView textPort;
	// ArrayList<String> titleList;

	static final int SocketServerPORT = 8080;

	private String IP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextAddress = (EditText) findViewById(R.id.address);
		textPort = (TextView) findViewById(R.id.port);
		textPort.setText("port: " + SocketServerPORT);

		IP = getIpAddress();
		// buttonConnect = (Button) findViewById(R.id.connect);

		// buttonConnect.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ClientRxThread clientRxThread = new ClientRxThread(
		// editTextAddress.getText().toString(), SocketServerPORT);
		//
		// clientRxThread.start();
		//
		// Intent i = new Intent(this, TrackerFileListActivity.class);
		// startActivity(i);
		//
		// }
		// });

	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += inetAddress.getHostAddress() + "\n";
						ip.trim();
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Peer Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}

	public void sendToTracker(View view) {
		ClientRxThread clientRxThread = new ClientRxThread(editTextAddress
				.getText().toString(), SocketServerPORT);

		clientRxThread.start();
	}

	public void trackerFiles(View view) {
		// ClientRxThread clientRxThread = new ClientRxThread(editTextAddress
		// .getText().toString(), SocketServerPORT);
		//
		// clientRxThread.start();

		Log.i("Client", "moving to Tracker list");
		Intent i = new Intent(this, TrackerFileListActivity.class);
		i.putExtra("ip", editTextAddress.getText().toString());
		i.putStringArrayListExtra("list", null);
		startActivity(i);
	}

	public void mySharedFiles(View view) {
		Intent i = new Intent(this, MySharedFilesActivity.class);
		startActivity(i);
	}

	private class ClientRxThread extends Thread {
		ArrayList<String> someList;
		String dstAddress;
		int dstPort;

		ClientRxThread(String address, int port) {
			dstAddress = address;
			dstPort = port;
		}

		@Override
		public void run() {
			Socket socket = null;

			try {
				System.out.println(dstAddress + ":" + dstPort);
				socket = new Socket(dstAddress, dstPort);

				// File file = new
				// File(Environment.getExternalStorageDirectory(),
				// "test.txt");

				// byte[] bytes = new byte[1024];
				// InputStream is = socket.getInputStream();
				// FileOutputStream fos = new FileOutputStream(file);
				// BufferedOutputStream bos = new BufferedOutputStream(fos);
				// int bytesRead = is.read(bytes, 0, bytes.length);
				// bos.write(bytes, 0, bytesRead);
				// bos.close();

				String path = Environment.getExternalStorageDirectory()
						.toString() + "/Ringtones";

				File f = new File(path);
				final File[] fileList = f.listFiles();

				final ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < fileList.length; i++) {
					String fileName = fileList[i].getName();
					// System.out
					// .println("inside for loop of converting values of i to string and splitting");
					// System.out.println("value of i" + fileName);
					list.add(IP + " : " + fileName);
					Log.i("MySharedFilesActivity - ", "Added data to list "
							+ fileName);
				}

				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(
							socket.getOutputStream());
					try {
						objectOutput.writeObject(list);
						objectOutput.close();
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("The socket for reading the object has problem");
					e.printStackTrace();
				}
				
				try {
					ObjectInputStream objectInput = new ObjectInputStream(
							socket.getInputStream());
					try {
						Object object = objectInput.readObject();
						final ArrayList<String> fromTrackerList = (ArrayList<String>) object;
						someList = new ArrayList<String>();
						for(int i=0;i<fromTrackerList.size();i++){
							Log.i("Peer from tracker", fromTrackerList.get(i));
							someList.add(fromTrackerList.get(i));
						}
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("PEER The socket for reading the object has problem");
					e.printStackTrace();
				}

				socket.close();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Finished",
								Toast.LENGTH_LONG).show();

						Log.i("Client", "moving to Tracker list from thread");
						Intent i = new Intent(MainActivity.this,
								TrackerFileListActivity.class);
						i.putExtra("ip", "9999");
						i.putStringArrayListExtra("list", someList);
						startActivity(i);
					}
				});

			} catch (IOException e) {

				e.printStackTrace();

				final String eMsg = "Something wrong: " + e.getMessage();
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, eMsg,
								Toast.LENGTH_LONG).show();
					}
				});

			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
