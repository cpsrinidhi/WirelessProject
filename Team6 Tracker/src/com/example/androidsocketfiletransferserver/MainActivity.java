package com.example.androidsocketfiletransferserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView infoIp, infoPort;

	static final int SocketServerPORT = 8080;
	ServerSocket serverSocket;

	ServerSocketThread serverSocketThread;

	ArrayList<String> peerFileList;
	static ArrayList<String> viewFileList = new ArrayList<String>();;
	ListView listViewPeerFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoIp = (TextView) findViewById(R.id.infoip);
		infoPort = (TextView) findViewById(R.id.infoport);

		infoIp.setText(getIpAddress());

		listViewPeerFiles = (ListView) findViewById(R.id.listViewPeerFiles);

		serverSocketThread = new ServerSocketThread();
		serverSocketThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
						ip += "SiteLocalAddress: "
								+ inetAddress.getHostAddress() + "\n";
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}

	public class ServerSocketThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						infoPort.setText("I'm waiting here: "
								+ serverSocket.getLocalPort());
					}
				});

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

	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			Log.i("MySharedFilesActivity - ",
					"Inside StableArrayAdapter constructor");
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

	public class FileTxThread extends Thread {
		Socket socket;

		FileTxThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// File file = new File(Environment.getExternalStorageDirectory(),
			// "test.txt");

			// byte[] bytes = new byte[(int) file.length()];
			// BufferedInputStream bis;

			// Accept file list from peer
			try {
				Log.i("Server MA", "input");
				ObjectInputStream objectInput = new ObjectInputStream(
						socket.getInputStream());
				try {
					Object object = objectInput.readObject();
					peerFileList = new ArrayList<String>();
					peerFileList = (ArrayList<String>) object;
					objectInput.close();

					// synchronized (viewFileList) {
					if (!peerFileList.isEmpty()) {
						Iterator<String> i = peerFileList.iterator();
						while (i.hasNext()) {
							viewFileList.add(i.next());
						}
						Log.i("Tracker", viewFileList.size() + " ");
						// }
						// System.out.println("From peer " +
						// peerFileList.get(1));
					}

					if (!peerFileList.isEmpty()) {
						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								final StableArrayAdapter adapter = new StableArrayAdapter(
										MainActivity.this,
										android.R.layout.simple_list_item_1,
										viewFileList);
								listViewPeerFiles.setAdapter(adapter);
							}
						});
					}

				} catch (ClassNotFoundException e) {
					System.out
							.println("The title list has not come from the server");
					e.printStackTrace();
				} catch (Exception e) {
					Log.e("Client", "Client exceptions");
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out
						.println("SERVER The socket for reading the object has problem");
				e.printStackTrace();
			}
			// finally {
			// try {
			// socket.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

			// Send file list to peer
			try {
				// bis = new BufferedInputStream(new FileInputStream(file));
				// bis.read(bytes, 0, bytes.length);
				// OutputStream os = socket.getOutputStream();
				// os.write(bytes, 0, bytes.length);
				// os.flush();

				// ArrayList<String> my = new ArrayList<String>();
				// my.add(0, "Bernard");
				// my.add(1, "Grey");
				// for (int i = 0; i < viewFileList.size(); i++) {
				// my.add(viewFileList.get(i));
				// }

				Log.i("Server MA", "output");

				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(
							socket.getOutputStream());
					// objectOutput.writeObject(my);
					objectOutput.writeObject(viewFileList);
					objectOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket.close();

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

			}
			// catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
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
