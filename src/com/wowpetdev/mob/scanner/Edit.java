package com.wowpetdev.mob.scanner;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Edit extends Activity implements RadioGroup.OnCheckedChangeListener {
    private EditText mTitleText;
    private EditText mBodyText;
    private RadioGroup mRadioGroup;
    private String mStatus;
    
    private Long mRowId;
    private DbAdapter mDbHelper;

    private static final String TAG = "Edit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.product_edit);
        setTitle(R.string.edit_product);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        mRadioGroup.setOnCheckedChangeListener(this);
        
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(DbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(DbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_BODY)));
            
            try {
                String shoot_status = note.getString(note.getColumnIndexOrThrow(DbAdapter.KEY_STATUS));
                Log.d(TAG, "Status: " + shoot_status);
                
                if ( "received".equals(shoot_status) ) {
                	mRadioGroup.check(R.id.radio0);
                } else if ( "shoot".equals(shoot_status)) {
                	mRadioGroup.check(R.id.radio1);
                } else if ( "returned".equals(shoot_status) ) {
                	mRadioGroup.check(R.id.radio2);
                }
            } catch (Exception e){
            	Log.d(TAG,e.toString());
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDbHelper.close();
	}

	private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();        

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, mStatus);
        }
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
		RadioButton Choice = (RadioButton) findViewById(checkedId);
		mStatus = Choice.getText().toString();
		Toast.makeText(getApplicationContext(), mStatus, Toast.LENGTH_SHORT).show();
	}
}
