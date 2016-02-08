package edu.nav.hermes.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.nav.hermes.MapActivityFragment;

/**
 *
 * Created by eduardo on 07/02/16.
 */
public class TileDownloader extends AsyncTask<Void, Integer, Void> {

    private static final int BUFFER_SIZE = 4096;
    private ProgressDialog progressDialog;
    private Context context;
    private MapActivityFragment mapActivityFragment;


    public TileDownloader(Context ctx, MapActivityFragment mapActivityFragment) {
        this.context = ctx;
        this.mapActivityFragment = mapActivityFragment;
    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Download do Mapa");
        progressDialog.setMessage("Por favor, aguarde enquanto o download do mapa é realizado");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values != null) {
            progressDialog.setMax(values[0]);
        }
        progressDialog.incrementProgressBy(BUFFER_SIZE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            final File file = new File(Environment.getExternalStorageDirectory(), "osmdroid");
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdir();
            }
            downloadFile("https://s3.amazonaws.com/tcchermes/rio_01.zip", file.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
            Integer[] as_array = {contentLength};

            publishProgress(as_array);

//            System.out.println("Content-Type = " + contentType);
//            System.out.println("Content-Disposition = " + disposition);
//            System.out.println("Content-Length = " + contentLength);
//            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                publishProgress();
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

}
