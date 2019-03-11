package project.com.googlesheetsapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity {

    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY ,SheetsScopes.DRIVE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);




        new MakeRequestTask().execute();

    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Exception mLastError = null;
        com.google.api.services.sheets.v4.Sheets mService = null;

        MakeRequestTask() {

            java.io.File licenseFile = getSecretFile();
            try {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                GoogleCredential credential1 = new GoogleCredential.Builder()
                        .setTransport(transport)
                        .setJsonFactory(jsonFactory)
                        .setServiceAccountId("test-134@alien-trainer-234104.iam.gserviceaccount.com")
                        .setServiceAccountScopes(Arrays.asList(SCOPES))
                        .setServiceAccountPrivateKeyFromP12File(licenseFile)
                        .build();

                mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                        transport, jsonFactory, credential1)
                        .setApplicationName("Google Sheets API Android Quickstart")
                        .build();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            Toast.makeText(Main2Activity.this, strings.get(0), Toast.LENGTH_SHORT).show();

        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */



        private List<String> getDataFromApi() throws IOException {
            String spreadsheetId = "10GsEFeMSu_Tu6AKsaux7AjaM-BzFf0aEUwagvq8H6vw";
            String range = "A1:C1";
            List<String> results = new ArrayList<String>();

            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    results.add(row.get(0) + ", " + row.get(4));
                }
            }


            return results;
        }

    }
    public  java.io.File getSecretFile()
    {
        File f = new File(Main2Activity.this.getCacheDir()+ "/" +"googleSheets.p12");
        if (f.exists())
        {
            f.delete();
        }
        try
        {
            InputStream is = Main2Activity.this.getAssets().open("googleSheets.p12");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return f;
    }
}




