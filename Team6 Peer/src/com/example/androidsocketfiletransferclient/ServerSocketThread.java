package com.example.androidsocketfiletransferclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServerSocketThread extends Service {
	ServerThread serverThread;
	ServerSocket serverSocket;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Toast.makeText(this, "Peer File Server Started", Toast.LENGTH_LONG).show();
		serverThread = new ServerThread();
		serverThread.start();
	}

	private class ServerThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(9090);

				Log.i("PEER SST", "Waiting on " + 9090);

				while (true) {
					socket = serverSocket.accept();
					FileTxThread fileTxThread = new FileTxThread(socket);
					fileTxThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
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

	private class FileTxThread extends Thread {
		Socket socket;

		FileTxThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			String fileName = " ";
			
			//Get the file name from the requesting peer.
			try {
				ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
				try {
					fileName = (String) objectInput.readObject();
					Log.i("PEER SST", fileName);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("The socket for reading the ObjectInputStream has problem");
				e.printStackTrace();
			}

			//Used as socket closed test. Makes no addition to functionality
			if (socket.isClosed()) {
				Log.i("PEER SST", "socket state " + socket.isClosed());
			}
			
			//Send the request file to the requesting peer.
			try {
				ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
				Log.i("Peer SST", "read OOS");
				try {
					String filePath = (Environment.getExternalStorageDirectory() + "/Ringtones/").trim()
							+ fileName.trim();
					Log.i("Peer SST", "actual filename with path " + filePath);

					File file = new File(filePath);
					if (file.exists()) {
						Log.i("Peer SST", "file exists");
						FileInputStream fis = new FileInputStream(file);
						Log.i("Peer SST", "got FIS");
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						Log.i("Peer SST", "got BOS");
						byte[] buf = new byte[1024];
						try {
							for (int readNum; (readNum = fis.read(buf)) != -1;) {
								bos.write(buf, 0, readNum);
								System.out.println("read " + readNum + " bytes,");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						byte[] content = bos.toByteArray();
						Log.i("Peer SST", "bos to byte array");
						objectOutput.writeObject(content);
						Log.i("Peer SST", "wrote content");
					}
					objectOutput.close();
				} catch (Exception e) {
					Log.e("Peer", e.getMessage());
				}
			} catch (IOException e) {
				System.out.println("The socket for reading the ObjectOutputStream has problem");
				e.printStackTrace();
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
