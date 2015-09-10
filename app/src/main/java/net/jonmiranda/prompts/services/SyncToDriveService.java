package net.jonmiranda.prompts.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;

public class SyncToDriveService extends IntentService implements ResultCallback<DriveApi.DriveContentsResult> {

  GoogleApiClient mGoogleApiClient;

  public SyncToDriveService() {
    super("SyncToDriveService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d("Jonny", "onHandleIntent");

    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .addApi(Drive.API)
      .addScope(Drive.SCOPE_FILE)
      .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle connectionHint) {
          Log.d("Jonny", "onConnected: " + connectionHint);
          Query query = new Query.Builder().addFilter(
            Filters.and(
              Filters.eq(SearchableField.MIME_TYPE, "text/realm"),
              Filters.contains(SearchableField.TITLE, "prompts-backup.realm"))).build();


          while (!mGoogleApiClient.isConnected()) {
          }
          Drive.DriveApi.query(mGoogleApiClient, query)
            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
              @Override
              public void onResult(DriveApi.MetadataBufferResult results) {
                while (!mGoogleApiClient.isConnected()) {
                }

                DriveFile file = null;
                Iterator<Metadata> iterator = results.getMetadataBuffer().iterator();
                if (iterator.hasNext()) {
                  Metadata data = iterator.next();
                  if (!data.isTrashed() && data.isEditable()) {
                    file = Drive.DriveApi.getFile(mGoogleApiClient, data.getDriveId());
                  }
                }

                if (file != null) {
                  PendingResult<DriveApi.DriveContentsResult> result = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null);
                  result.setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                      Log.d("Jonny", "File exists!!!");
                      try {
                        saveFile(
                          new File(getApplicationContext().getFilesDir(), "default.realm"),
                          result.getDriveContents().getParcelFileDescriptor().getFileDescriptor());
                        result.getDriveContents().commit(mGoogleApiClient, null);
                      } catch (IOException e) {
                        Log.d("Jonny", "Failed trying to save file.", e);
                      }
                    }
                  });
                } else {
                  Log.d("Jonny", "File does not exist, will attempt to create now.");

                  Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(SyncToDriveService.this);
//                      Log.d("Jonny", "onConnected! " + connectionHint);
                }
              }
            });
        }

        @Override
        public void onConnectionSuspended(int cause) {
          Log.d("Jonny", "onConnectionSuspendedonConnectionSuspended !" + cause);
        }
      })
      .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
          Log.d("Jonny", "OnConnectionFailed: " + result);
        }
      })
      .build();

    mGoogleApiClient.blockingConnect();
    try {
      Thread.sleep(10000);
    } catch (Exception e) {}
  }

  @Override
  public void onResult(DriveApi.DriveContentsResult result) {
    if (!result.getStatus().isSuccess()) {
      Log.d("Jonny", "onResult: Error: " + result);
      // Handle error
      return;
    }


    try {
      saveFile(
        new File(getApplicationContext().getFilesDir(), "default.realm"),
        result.getDriveContents().getParcelFileDescriptor().getFileDescriptor());
    } catch (IOException e) {
      Log.d("Jonny", "Failed trying to save file.", e);
    }

    // Create a file in the app folder
    if (mGoogleApiClient.isConnected()) {
      MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
        .setTitle("prompts-backup.realm")
        .setMimeType("text/realm")
        .build();

      Drive.DriveApi.getRootFolder(mGoogleApiClient)
        .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
          @Override
          public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
              Log.d("Jonny", "Error while trying to create the file");
            }
            Log.d("Jonny", "Created a file in App Folder: " + result.getDriveFile().getDriveId());
          }
        });
    }
  }

  private static void tryClose(Closeable close) {
    Log.d("Jonny", "tryClose: " + close);
    try {
      close.close();
    } catch (IOException e) {
      Log.d("Jonny", "Failed to close: " + close);
    }
  }

  private static void saveFile(File source, FileDescriptor destination) throws IOException{
    FileChannel src = new FileInputStream(source).getChannel();
    FileChannel dest = new FileOutputStream(destination).getChannel();
    dest.transferFrom(src, 0, src.size());
    tryClose(src);
    tryClose(dest);
  }

}
