package com.example.androidsocketfiletransferclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TrackerFileListActivity extends Activity implements
		OnItemClickListener {
	ArrayList<String> trackerFileList;
	ListView listViewTrackerFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker_file_list);

		listViewTrackerFiles = (ListView) findViewById(R.id.listViewTrackerFiles);

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

				String filePath = (Environment.getExternalStorageDirectory() + "/Ringtones/")
						.trim() + dstFileName.trim();

				//Send the file name to the respective peer 
				try {
					ObjectOutputStream objectOutput = new ObjectOutputStream(
							socket.getOutputStream());
					try {
						objectOutput.writeObject(dstFileName);
					} catch (Exception e) {
						Log.e("Peer", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("The socket for reading the ObjectOutputStream has problem");
					e.printStackTrace();
				}

				//Used as socket closed test. Makes no addition to functionality
				final boolean state = socket.isClosed();
				TrackerFileListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(getApplicationContext(),
								"socket state: " + "state " + state,
								Toast.LENGTH_LONG).show();
					}
				});

				//Receive the file from the peer.
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
						e.printStackTrace();
					}
				}
			}
		}
	}
}
