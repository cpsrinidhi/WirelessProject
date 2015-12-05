package com.example.androidsocketfiletransferclient;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	// Button buttonConnect;
	TextView textPort;
	// ArrayList<String> titleList;

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

//		serverSocketThread = new ServerSocketThread();
//		serverSocketThread.start();
		
		startService(new Intent(getBaseContext(), ServerSocketThread.class));
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

	public void mySharedFiles(View view) {
		Intent i = new Intent(this, MySharedFilesActivity.class);
		startActivity(i);
	}
	
	public void sendToTracker(View view) {
		ClientRxThread clientRxThread = new ClientRxThread(editTextAddress
				.getText().toString(), SocketServerPORT);

		clientRxThread.start();
	}

	// public void trackerFiles(View view) {
	// ClientRxThread clientRxThread = new ClientRxThread(editTextAddress
	// .getText().toString(), SocketServerPORT);
	//
	// clientRxThread.start();
	//
	// Log.i("Client", "moving to Tracker list");
	// Intent i = new Intent(this, TrackerFileListActivity.class);
	// i.putExtra("ip", editTextAddress.getText().toString());
	// startActivity(i);
	// }

	private class ClientRxThread extends Thread {
		String dstAddress;
		int dstPort;
		ArrayList<String> fromTrackerList;

		ClientRxThread(String address, int port) {
			dstAddress = address;
			dstPort = port;
		}

		@Override
		public void run() {
			Socket socket = null;

			try {
				socket = new Socket(dstAddress, dstPort);

				// File file = new
				// File(Environment.getExternalStorageDirectory(),
				// "test.txt");

				byte[] bytes = new byte[1024];
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
//					System.out
//							.println("inside for loop of converting values of i to string and splitting");
//					System.out.println("value of i" + fileName);
					list.add(IP + " : " + fileName);
//					Log.i("MySharedFilesActivity - ", "Added data to list "
//							+ fileName);
				}

				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(
							socket.getOutputStream());
					try {
						objectOutput.writeObject(list);
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("The socket for reading the object has problem");
					e.printStackTrace();
				}

				// socket.close();

				try {
					ObjectInputStream objectInput = new ObjectInputStream(
							socket.getInputStream());
					try {
						Object object = objectInput.readObject();
						fromTrackerList = (ArrayList<String>) object;
						// someList = new ArrayList<String>();
						// for(int i=0;i<fromTrackerList.size();i++){
						// Log.i("Peer from tracker", fromTrackerList.get(i));
						// someList.add(fromTrackerList.get(i));
						// }
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
						Toast.makeText(MainActivity.this, "Going to TFLA",
								Toast.LENGTH_LONG).show();

						Intent i = new Intent(MainActivity.this,
								TrackerFileListActivity.class);
						i.putStringArrayListExtra("fromTrackerList",
								fromTrackerList);
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

/*	public class ServerSocketThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(9090);
				// MainActivity.this.runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// infoPort.setText("I'm waiting here: "
				// + serverSocket.getLocalPort());
				// }
				// });

				while (true) {
					socket = serverSocket.accept();
					FileTxThread fileTxThread = new FileTxThread(socket);
					fileTxThread.start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	}*/

	/*public class FileTxThread extends Thread {
		Socket socket;

		FileTxThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// File file = new File(Environment.getExternalStorageDirectory(),
			// "test.txt");

			// byte[] bytes = new byte[(int) file.length()];

			// ObjectInputStream objectInput = new ObjectInputStream(
			// socket.getInputStream());
			try {
				DataInputStream dataInputStream = new DataInputStream(
						socket.getInputStream());
				String fileName = dataInputStream.readUTF();
				Log.i("Peer MA", fileName);
				dataInputStream.close();

				File file = new File(
						Environment.getExternalStorageDirectory()
						+ "/Ringtones", fileName);

				byte[] bytes = new byte[(int) file.length()];
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(file));
				bis.read(bytes, 0, bytes.length);
				OutputStream os = socket.getOutputStream();
				os.write(bytes, 0, bytes.length);
				os.flush();
				socket.close();

				// Object object = objectInput.readObject();
				// ArrayList<String> peerFileList = new ArrayList<String>();
				// peerFileList.add("1");

				// synchronized (viewFileList) {
				// Iterator<String> i = peerFileList.iterator();
				// while (i.hasNext()) {
				// viewFileList.add(i.next());
				// }
				// Log.i("Tracker", viewFileList.size() + " ");
				// }
				// System.out.println("From peer " + peerFileList.get(1));

				// MainActivity.this.runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// final StableArrayAdapter adapter = new
				// StableArrayAdapter(
				// MainActivity.this,
				// android.R.layout.simple_list_item_1,
				// viewFileList);
				// listViewPeerFiles.setAdapter(adapter);
				// }
				// });

			} catch (Exception e) {
				Log.e("Client", e.getMessage());
			}

			// Send file list to peer
			// try {
			// bis = new BufferedInputStream(new FileInputStream(file));
			// bis.read(bytes, 0, bytes.length);
			// OutputStream os = socket.getOutputStream();
			// os.write(bytes, 0, bytes.length);
			// os.flush();
			//
			// ArrayList<String> my = new ArrayList<String>();
			// my.add(0, "Bernard");
			// my.add(1, "Grey");
			// for (int i = 0; i < viewFileList.size(); i++) {
			// my.add(viewFileList.get(i));
			// }
			// try {
			// ObjectOutputStream objectOutput = new ObjectOutputStream(
			// socket.getOutputStream());
			// objectOutput.writeObject(my);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// socket.close();
			//
			// final String sentMsg = "File sent to: "
			// + socket.getInetAddress();
			// MainActivity.this.runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// Toast.makeText(MainActivity.this, sentMsg,
			// Toast.LENGTH_LONG).show();
			// }
			// });
			//
			// }
			// catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } finally {
			// try {
			// socket.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

		}
	}*/
}
