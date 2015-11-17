package com.example.androidsocketfiletransferclient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends ActionBarActivity {

	EditText editTextAddress;
	Button buttonConnect;
	TextView textPort;

	static final int SocketServerPORT = 8080;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextAddress = (EditText) findViewById(R.id.address);
		textPort = (TextView) findViewById(R.id.port);
		textPort.setText("port: " + SocketServerPORT);
		buttonConnect = (Button) findViewById(R.id.connect);

		buttonConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClientRxThread clientRxThread = new ClientRxThread(
						editTextAddress.getText().toString(), SocketServerPORT);

				clientRxThread.start();
			}
		});
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

				File file = new File(Environment.getExternalStorageDirectory(),
						"test.txt");

				byte[] bytes = new byte[1024];
				InputStream is = socket.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int bytesRead = is.read(bytes, 0, bytes.length);
				bos.write(bytes, 0, bytesRead);
				bos.close();
				socket.close();

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Finished",
								Toast.LENGTH_LONG).show();
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
