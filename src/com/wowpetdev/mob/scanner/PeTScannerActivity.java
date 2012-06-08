package com.wowpetdev.mob.scanner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PeTScannerActivity extends Activity {
	private String contentsString;
	private DbAdapter mDbHelper;
	
    private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	
	private static final String TAG = "Main";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btnScanBarCode = (Button) findViewById(R.id.btnScanBarCode);
		Button btnIndex = (Button) findViewById(R.id.btnIndex);
		

		
		btnScanBarCode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				IntentIntegrator integrator = new IntentIntegrator(
						PeTScannerActivity.this);
				integrator.initiateScan();

			}
		});
		
		btnIndex.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "Click Index", Toast.LENGTH_SHORT).show();
				//mDbHelper.createNote("Initial Title", "This is APN field");
				//mDbHelper.fetchAllNotes();
//				Cursor product = mDbHelper.fetchNote(4);
//				String prd = product.getString(product.getColumnIndexOrThrow(DbAdapter.KEY_TITLE));
//				Toast.makeText(getBaseContext(), prd, Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getBaseContext(), Index.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		if (scanResult != null) {

			// handle scan result
			contentsString = scanResult.getContents() == null ? "0"
					: scanResult.getContents();
			if (contentsString.equalsIgnoreCase("0")) {
				Toast.makeText(this, "Problem to get the  content Number",
						Toast.LENGTH_LONG).show();

	        	
			} else {
				Toast.makeText(this, contentsString, Toast.LENGTH_LONG).show();
				

	            
				try {
			        mDbHelper = new DbAdapter(this);
			        mDbHelper.open();
					Cursor note = mDbHelper.fetchAPN(contentsString);
		            startManagingCursor(note);
		        	Long id = note.getLong(note.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));				
		        	//Toast.makeText(this, Long.toString(id), Toast.LENGTH_LONG).show(); 
				
					Intent i = new Intent(getApplicationContext(), Edit.class);
					i.putExtra(DbAdapter.KEY_ROWID, id);
					startActivity(i);
				}
				catch (Exception e) {
			        mDbHelper = new DbAdapter(this);
			        mDbHelper.open();
					Log.d(TAG,e.toString());
					mDbHelper.createNote(contentsString, contentsString);
				}
			}

		} else {
			Toast.makeText(this, "Problem to secan the barcode.",
					Toast.LENGTH_LONG).show();
		}
	}

}