package com.example.androidsocketfiletransferclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore.Files;
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
		// TODO Auto-generated method stub
		super.onCreate();

		Toast.makeText(this, "Peer File Server Started", Toast.LENGTH_LONG)
				.show();
		serverThread = new ServerThread();
		serverThread.start();
	}

	// @Override
	// public int onStartCommand(Intent intent, int flags, int startId) {
	// // TODO Auto-generated method stub
	// // return super.onStartCommand(intent, flags, startId);
	//
	// Toast.makeText(this, "Peer File Server Started", Toast.LENGTH_LONG)
	// .show();
	// serverThread = new ServerThread();
	// serverThread.start();
	// return START_STICKY;
	// }

	// @Override
	// public void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// Toast.makeText(this, "Peer File Server Destroyed", Toast.LENGTH_LONG)
	// .show();
	// }

	private class ServerThread extends Thread {

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

				Log.i("PEER SST", "Waiting on " + 9090);

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

	private class FileTxThread extends Thread {
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
			// try {
			// DataInputStream dataInputStream = new DataInputStream(
			// socket.getInputStream());
			// String fileName = dataInputStream.readUTF();
			// // dataInputStream.close();
			//
			// String filePath = (Environment.getExternalStorageDirectory() +
			// "/Ringtones/")
			// .trim() + fileName.trim();
			// Log.i("Peer SST", "actual filename with path " + filePath);
			//
			// // File file = new
			// // File(Environment.getExternalStorageDirectory()
			// // + "/Ringtones/", fileName);
			//
			// File file = new File(filePath);
			//
			// Log.i("Peer SST", file.getAbsolutePath());

			// original
			// if (file.exists()) {
			// Log.i("Peer SST", fileName + " exists in peer");
			// byte[] bytes = new byte[(int) file.length()];
			// BufferedInputStream bis = new BufferedInputStream(
			// new FileInputStream(file));
			// Log.i("Peer SST", "BIS created");
			// bis.read(bytes, 0, bytes.length);
			// Log.i("Peer SST", "BIS read");
			// OutputStream os = socket.getOutputStream();
			// Log.i("Peer SST", "OS got");
			// os.write(bytes, 0, bytes.length);
			// os.flush();
			// }

			// http://stackoverflow.com/questions/17285846/large-file-transfer-over-java-socket
			// if (file.exists()) {
			// byte[] mybytearray = new byte[8192];
			// Log.i("Peer SST", fileName + " exists in peer");
			// FileInputStream fis = new FileInputStream(file);
			// Log.i("Peer SST", fileName + " got FIS");
			// BufferedInputStream bis = new BufferedInputStream(fis);
			// Log.i("Peer SST", fileName + " got BIS");
			// DataInputStream dis = new DataInputStream(bis);
			// Log.i("Peer SST", fileName + " got DIS");
			// try {
			// OutputStream os = socket.getOutputStream();
			// Log.i("Peer SST", fileName + " got OS");
			// DataOutputStream dos = new DataOutputStream(os);
			// Log.i("Peer SST", fileName + " got DOS");
			// dos.writeLong(mybytearray.length);
			// Log.i("Peer SST", fileName + " wrote length");
			// int read;
			// while ((read = dis.read(mybytearray)) != -1) {
			// dos.write(mybytearray, 0, read);
			// Log.i("Peer SST", "writing to requestor");
			// }
			//
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }

			// http://www.coderanch.com/t/596202/Android/Mobile/Transfer-File-Android-server-socket
			// if (file.exists()) {
			// Log.i("Peer SST", "file exists in peer");
			// OutputStream os = socket.getOutputStream();
			// Log.i("Peer SST", "got OS");
			// DataOutputStream dos = new DataOutputStream(os);
			// Log.i("Peer SST", "got DOS");
			// int fileSize = (int) file.length();
			// Log.i("Peer SST", "got file len");
			// dos.writeInt(fileSize);
			// Log.i("Peer SST", "dos write filesize");
			// byte[] buffer = new byte[fileSize];
			// Log.i("Peer SST", "got buff");
			//
			// FileInputStream fis = new FileInputStream(file.toString());
			// Log.i("Peer SST", "got fis");
			// BufferedInputStream bis = new BufferedInputStream(fis);
			// Log.i("Peer SST", "got bis");
			//
			// // Sending file name and file size to the server
			// bis.read(buffer, 0, buffer.length);
			// Log.i("Peer SST", "read bis");
			// dos.write(buffer, 0, buffer.length);
			// Log.i("Peer SST", "wrote dos");
			// dos.flush();
			// Log.i("Peer SST", "flush dos");
			// }
			// Toast.makeText(getApplicationContext(),
			// "Transfer file is completed!!", Toast.LENGTH_LONG)
			// .show();

			String fileName = " ";
			try {
				ObjectInputStream objectInput = new ObjectInputStream(
						socket.getInputStream());
				try {
//					fileName = objectInput.readUTF();
					fileName = (String) objectInput.readObject();
//					objectInput.close();
					Log.i("PEER SST", fileName);

				} catch (Exception e) {
					// Log.e("Client", e.getMessage());
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out
						.println("The socket for reading the ObjectInputStream has problem");
				e.printStackTrace();
			}

			if(socket.isClosed()){
				Log.i("PEER SST", "socket state " + socket.isClosed());
			}
			try {
				ObjectOutputStream objectOutput = new ObjectOutputStream(
						socket.getOutputStream());
				Log.i("Peer SST", "read OOS");
				try {
					String filePath = (Environment
							.getExternalStorageDirectory() + "/Ringtones/")
							.trim()
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
								bos.write(buf, 0, readNum); // no doubt here is
															// 0
								// Writes len bytes from the specified byte
								// array starting at offset off to this byte
								// array output stream.
								System.out.println("read " + readNum
										+ " bytes,");
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
				System.out
						.println("The socket for reading the ObjectOutputStream has problem");
				e.printStackTrace();
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

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

			// } catch (Exception e) {
			// Log.e("Client SST", e.getMessage());
			// e.printStackTrace();
			// }

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
	}

}
