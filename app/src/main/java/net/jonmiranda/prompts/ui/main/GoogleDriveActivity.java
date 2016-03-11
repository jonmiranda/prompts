package net.jonmiranda.prompts.ui.main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import net.jonmiranda.prompts.services.SyncToDriveService;


public class GoogleDriveActivity
  extends FragmentActivity
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LinearLayout(this));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
          .addApi(Drive.API)
          .addScope(Drive.SCOPE_FILE)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Syncing Prompts to Google Drive.", Toast.LENGTH_SHORT).show();
        startService(new Intent(getApplicationContext(), SyncToDriveService.class));
        finish();
    }

    @Override
    public void onConnectionSuspended(int unused) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, 1 /** REQUEST_CODE_RESOLUTION */);
        } catch (IntentSender.SendIntentException e) {
            Log.e("GoogleDriveActivity", "Exception while starting resolution activity", e);
        }
    }
}
