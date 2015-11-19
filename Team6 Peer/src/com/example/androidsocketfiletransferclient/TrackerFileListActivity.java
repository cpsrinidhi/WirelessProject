package com.example.androidsocketfiletransferclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TrackerFileListActivity extends Activity {
	ArrayList<String> trackerFileList;
	ListView listViewTrackerFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker_file_list);

		listViewTrackerFiles = (ListView) findViewById(R.id.listViewTrackerFiles);

		String ip = getIntent().getStringExtra("ip");

		ClientRxThread clientRxThread = new ClientRxThread(ip, 8080);

		clientRxThread.start();
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

	private class ClientRxThread extends Thread {
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
				socket = new Socket(dstAddress, dstPort);

				trackerFileList = new ArrayList<String>();
				try {
					ObjectInputStream objectInput = new ObjectInputStream(
							socket.getInputStream());
					try {
						Object object = objectInput.readObject();
						trackerFileList = (ArrayList<String>) object;
						System.out.println("Client " + trackerFileList.get(1));

						TrackerFileListActivity.this
								.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										final StableArrayAdapter adapter = new StableArrayAdapter(
												TrackerFileListActivity.this,
												android.R.layout.simple_list_item_1,
												trackerFileList);
										listViewTrackerFiles
												.setAdapter(adapter);
									}
								});
					} catch (ClassNotFoundException e) {
						System.out
								.println("The title list has not come from the server");
						e.printStackTrace();
					} catch (Exception e) {
						Log.e("Client", e.getMessage());
					}
				} catch (IOException e) {
					System.out
							.println("The socket for reading the object has problem");
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

			} catch (IOException e) {

				e.printStackTrace();

				final String eMsg = "Something wrong: " + e.getMessage();
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
