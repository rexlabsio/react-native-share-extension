package com.alinz.parkerdan.shareextension;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;


public class ShareModule extends ReactContextBaseJavaModule {


  public ShareModule(ReactApplicationContext reactContext) {
      super(reactContext);
  }

  @Override
  public String getName() {
      return "ReactNativeShareExtension";
  }

  @ReactMethod
  public void close() {
    getCurrentActivity().finish();
  }

  @ReactMethod
  public void data(Promise promise) {
      promise.resolve(processIntent());
  }
 
  public WritableMap processIntent() {
      WritableMap map = Arguments.createMap();

      String value = "";
      String type = "";
      String action = "";

      Activity currentActivity = getCurrentActivity();

      if (currentActivity != null) {
        Intent intent = currentActivity.getIntent();
        action = intent.getAction();
        type = intent.getType();
        if (type == null) {
          type = "";
        }
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
          value = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else if (Intent.ACTION_SEND.equals(action) && ("image/*".equals(type) || "image/jpeg".equals(type) || "image/png".equals(type) || "image/jpg".equals(type) ) ) {
          Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
         value = "file://" + RealPathUtil.getRealPathFromURI(currentActivity, uri);
       } else if (Intent.ACTION_SEND.equals(action) && "text/x-vcard".equals(type)) {
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

            //read whole file to string
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        currentActivity.getContentResolver().openInputStream(uri), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    value += line + "\r\n";
                }
            reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

       } else {
         value = "";
       }      
      } else {
        value = "";
        type = "";
      }

      map.putString("type", type);
      map.putString("value",value);

      return map;
  }
}
