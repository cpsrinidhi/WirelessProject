package com.example.androidsocketfiletransferclient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MySharedFilesActivity extends Activity {

	ListView listViewMyShareFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_shared_files);

		listViewMyShareFiles = (ListView) findViewById(R.id.listViewMyShareFiles);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/Ringtones";
		Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();

		File f = new File(path);
		final File[] fileList = f.listFiles();

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i].getName();
			System.out
					.println("inside for loop of converting values of i to string and splitting");
			System.out.println("value of i" + fileName);
			list.add(fileName);
			Log.i("MySharedFilesActivity - ", "Added data to list " + fileName);
		}

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listViewMyShareFiles.setAdapter(adapter);
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
}
