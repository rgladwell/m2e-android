package com.test.issue43;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.test.issue43.library.LibraryClass;

public class TestActivity extends Activity {
     @Override
     protected void onCreate (Bundle savedInstanceState) {
          super.onCreate (savedInstanceState);
          
          setContentView (R.layout.library_layout);
          
          TextView textView = (TextView) findViewById (R.id.libraryTextView);
          
          textView.setText (new LibraryClass().getValue());
     }
}
