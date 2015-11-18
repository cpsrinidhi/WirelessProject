package com.example.androidsocketfiletransferclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
//	ArrayList<String> titleList;

	static final int SocketServerPORT = 8080;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextAddress = (EditText) findViewById(R.id.address);
		textPort = (TextView) findViewById(R.id.port);
		textPort.setText("port: " + SocketServerPORT);
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

	public void trackerFiles(View view) {
//		ClientRxThread clientRxThread = new ClientRxThread(editTextAddress
//				.getText().toString(), SocketServerPORT);
//
//		clientRxThread.start();

		Log.i("Client", "moving to Tracker list");
		Intent i = new Intent(this, TrackerFileListActivity.class);
		i.putExtra("ip", editTextAddress.getText().toString());
		startActivity(i);
	}

	public void mySharedFiles(View view) {
		Intent i = new Intent(this, MySharedFilesActivity.class);
		startActivity(i);
	}

//	private class ClientRxThread extends Thread {
//		String dstAddress;
//		int dstPort;
//
//		ClientRxThread(String address, int port) {
//			dstAddress = address;
//			dstPort = port;
//		}
//
//		@Override
//		public void run() {
//			Socket socket = null;
//
//			try {
//				socket = new Socket(dstAddress, dstPort);
//
//				// File file = new
//				// File(Environment.getExternalStorageDirectory(),
//				// "test.txt");
//
//				byte[] bytes = new byte[1024];
//				// InputStream is = socket.getInputStream();
//				// FileOutputStream fos = new FileOutputStream(file);
//				// BufferedOutputStream bos = new BufferedOutputStream(fos);
//				// int bytesRead = is.read(bytes, 0, bytes.length);
//				// bos.write(bytes, 0, bytesRead);
//				// bos.close();
//
//				titleList = new ArrayList<String>();
//				try {
//					ObjectInputStream objectInput = new ObjectInputStream(
//							socket.getInputStream());
//					try {
//						Object object = objectInput.readObject();
//						titleList = (ArrayList<String>) object;
//						System.out.println("Client " + titleList.get(1));
//					} catch (ClassNotFoundException e) {
//						System.out
//								.println("The title list has not come from the server");
//						e.printStackTrace();
//					} catch (Exception e) {
//						Log.e("Client", e.getMessage());
//					}
//				} catch (IOException e) {
//					System.out
//							.println("The socket for reading the object has problem");
//					e.printStackTrace();
//				}
//
//				socket.close();
//
//				MainActivity.this.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						Toast.makeText(MainActivity.this, "Finished",
//								Toast.LENGTH_LONG).show();
//					}
//				});
//
//			} catch (IOException e) {
//
//				e.printStackTrace();
//
//				final String eMsg = "Something wrong: " + e.getMessage();
//				MainActivity.this.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						Toast.makeText(MainActivity.this, eMsg,
//								Toast.LENGTH_LONG).show();
//					}
//				});
//
//			} finally {
//				if (socket != null) {
//					try {
//						socket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
}
