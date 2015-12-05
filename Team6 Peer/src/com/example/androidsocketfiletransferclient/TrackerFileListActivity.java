package com.example.androidsocketfiletransferclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TrackerFileListActivity extends Activity implements
		OnItemClickListener {
	ArrayList<String> trackerFileList;
	ListView listViewTrackerFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker_file_list);

		listViewTrackerFiles = (ListView) findViewById(R.id.listViewTrackerFiles);

		// String ip = getIntent().getStringExtra("ip");

		startService(new Intent(getBaseContext(), ServerSocketThread.class));

		trackerFileList = getIntent()
				.getStringArrayListExtra("fromTrackerList");

		final StableArrayAdapter adapter = new StableArrayAdapter(
				TrackerFileListActivity.this,
				android.R.layout.simple_list_item_1, trackerFileList);
		listViewTrackerFiles.setAdapter(adapter);
		listViewTrackerFiles.setOnItemClickListener(this);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String itemName = parent.getItemAtPosition(position).toString();
		String peerIP = itemName.split(":")[0];
		String fileName = itemName.split(":")[1];
		ClientRxThread clientRxThread = new ClientRxThread(fileName, peerIP,
				9090);

		clientRxThread.start();
	}

	private class ClientRxThread extends Thread {
		String dstFileName;
		String dstAddress;
		int dstPort;

		ClientRxThread(String fileName, String address, int port) {
			dstFileName = fileName;
			dstAddress = address;
			dstPort = port;
		}

		@Override
		public void run() {
			Socket socket = null;

			try {
				Log.i("PEER TFLA", dstFileName + ":" + dstAddress + ":"
						+ dstPort);
				socket = new Socket(dstAddress, dstPort);

				// File file = new
				// File(Environment.getExternalStorageDirectory()
				// + "/Ringtones", dstFileName);

				// DataOutputStream dataOutputStream = new DataOutputStream(
				// socket.getOutputStream());
				// Log.i("PEER TFLA", "DOS created");
				// dataOutputStream.writeUTF(dstFileName);
				// Log.i("PEER TFLA", "DOS wrote");
				// dataOutputStream.close();
				// Log.i("PEER TFLA", "DOS closed");

				String filePath = (Environment.getExternalStorageDirectory() + "/Ringtones/")
						.trim() + dstFileName.trim();

				// http://stackoverflow.com/questions/17285846/large-file-transfer-over-java-socket
				// try {
				// int bufferSize = socket.getReceiveBufferSize();
				// Log.i("PEER TFLA", "got buff size");
				// InputStream in = socket.getInputStream();
				// Log.i("PEER TFLA", "got IS");
				// DataInputStream clientData = new DataInputStream(in);
				// Log.i("PEER TFLA", "got DIS");

				// Log.i("PEER TFLA", filePath);
				// OutputStream output = new FileOutputStream(filePath);
				// Log.i("PEER TFLA", "got OS");
				// byte[] buffer = new byte[(int) clientData.readLong()];
				// int read;
				// while ((read = clientData.read(buffer)) != -1) {
				// output.write(buffer, 0, read);
				// Log.i("PEER TFLA", "writing to file");
				// }
				//
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				// byte[] bytes = new byte[1024];
				// InputStream is = socket.getInputStream();
				// Log.i("PEER TFLA", "IS got");
				//
				// String filePath = (Environment.getExternalStorageDirectory()
				// + "/Ringtones/").trim() + dstFileName.trim();
				// File file = new File(filePath);
				// if (!file.exists()) {
				// Log.i("PEER TFLA", "File not exists");
				// file.createNewFile();
				// Log.i("PEER TFLA", "File created");
				// FileOutputStream fos = new FileOutputStream(file);
				// Log.i("PEER TFLA", "FOS created");
				// BufferedOutputStream bos = new BufferedOutputStream(fos);
				// Log.i("PEER TFLA", "BOS created");
				// int bytesRead = is.read(bytes, 0, bytes.length);
				// Log.i("PEER TFLA", "bytesRead created");
				// bos.write(bytes, 0, bytesRead);
				// Log.i("PEER TFLA", "bytesRead wrote");
				// bos.close();
				// Log.i("PEER TFLA", "bos closed");
				// }

				// FileOutputStream fos = new FileOutputStream(file);
				// BufferedOutputStream bos = new BufferedOutputStream(fos);
				// int bytesRead = is.read(bytes, 0, bytes.length);
				// bos.write(bytes, 0, bytesRead);
				// bos.close();

				// http://www.coderanch.com/t/596202/Android/Mobile/Transfer-File-Android-server-socket
				// InputStream in = socket.getInputStream();
				// Log.i("PEER TFLA", "got IS");
				// DataInputStream clientData = new DataInputStream(in);
				// Log.i("PEER TFLA", "got DIS");
				// BufferedInputStream clientBuff = new BufferedInputStream(in);
				// Log.i("PEER TFLA", "got BIS");
				// int fileSize = clientData.read();
				// Log.i("PEER TFLA", "got filesize " + fileSize);
				// int len = clientData.readInt();
				// Log.i("PEER TFLA", "got len " + len);
				// int smblen = 0;
				// File file = new File(filePath);
				// FileOutputStream output = new FileOutputStream(file);
				// Log.i("PEER TFLA", "got FOS");
				// DataOutputStream dos = new DataOutputStream(output);
				// Log.i("PEER TFLA", "got DOS");
				// BufferedOutputStream bos = new BufferedOutputStream(output);
				// Log.i("PEER TFLA", "got BOS");
				//
				// byte[] buffer = new byte[1024];
				//
				// bos.write(buffer, 0, buffer.length);
				// Log.i("PEER TFLA", "wrote BOS");
				//
				// while (len > 0 && (smblen = clientData.read(buffer)) > 0) {
				// dos.write(buffer, 0, smblen);
				// Log.i("PEER TFLA", "wrote DOS");
				// len = len - smblen;
				// dos.flush();
				// Log.i("PEER TFLA", "flush DOS");
				// }

				// Trying object output and input stream;
				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(
							socket.getOutputStream());
					try {
//						objectOutput.writeUTF(dstFileName);
						objectOutput.writeObject(dstFileName);
//						objectOutput.close();
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("The socket for reading the ObjectOutputStream has problem");
					e.printStackTrace();
				}

				final boolean state = socket.isClosed();
				TrackerFileListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(getApplicationContext(),
								"socket state: " + "state " + state,
								Toast.LENGTH_LONG).show();
					}
				});

				try {
					ObjectInputStream objectInput = new ObjectInputStream(
							socket.getInputStream());
					Log.i("TFLA", "got OIS");
					try {
						Object object = objectInput.readObject();
						Log.i("TFLA", "read object");
						byte[] buf = (byte[]) object;
						Log.i("TFLA", "object to buf");
						File file = new File(filePath);
						if (!file.exists()) {
							FileOutputStream fos = new FileOutputStream(file);
							Log.i("TFLA", "got FOS");
							fos.write(buf);
							Log.i("TFLA", "wrote buf to FOS");
							fos.close();
							Log.i("TFLA", "close FOS");
						}
						objectInput.close();
						Log.i("TFLA", "close OI");
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("PEER The socket for reading the ObjectInputStream has problem");
					e.printStackTrace();
				}

				socket.close();

				TrackerFileListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(TrackerFileListActivity.this,
								"Finished", Toast.LENGTH_LONG).show();
					}
				});

			} catch (FileNotFoundException e) {

				e.printStackTrace();

				final String eMsg = "Peer TFLA File wrong: " + e.getMessage();
				TrackerFileListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(TrackerFileListActivity.this, eMsg,
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (IOException e) {

				e.printStackTrace();

				final String eMsg = "Peer TFLA Something wrong: "
						+ e.getMessage();
				TrackerFileListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(TrackerFileListActivity.this, eMsg,
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
