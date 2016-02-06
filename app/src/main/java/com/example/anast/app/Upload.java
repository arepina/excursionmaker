package com.example.anast.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Upload extends AsyncTask<Void, Long, Boolean> {

    private ProgressDialog mDialog;//прогресс процесса загрузки
    Settings set;//перемеенная настроек дропбокса

    public Upload(int request_num, MainActivity activity, DropboxAPI dropboxApi, DropboxAPI<?> api, String dropboxPath) {
        set = new Settings(request_num, activity, dropboxApi, api, dropboxPath);
        mDialog = new ProgressDialog(activity);
        int number_of_ob_to_load = 0;
        for (int i = 0; i < MainActivity.exlist.size(); i++)
            if (MainActivity.exlist.get(i).selected)
                number_of_ob_to_load++;
        mDialog.setMax(number_of_ob_to_load);
        mDialog.setMessage("Uploading... Please be patient, it will take some time");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    //Подготовка к загрузке
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    //загрузка объектов в DropBox
    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDropboxDirectory = set.mContext.getCacheDir();
        File tempFileToUpload;
        FileWriter fileWriter;
        FileInputStream fis;
        int k = 0;
        try {
            for (int j = 0; j < MainActivity.exlist.size(); j++)//загружаем все экскурсии
            {
                if (MainActivity.exlist.get(j).selected) {
                    k++;
                    set.mPath = "/" + MainActivity.exlist.get(j).exname;
                    try {
                        set.dropboxApi.createFolder(set.mPath);
                    }
                    catch (DropboxServerException ex)//значит папка уже существует, ошибка 403
                    {
                        ex.printStackTrace();
                    }
                    for (int q = 0; q < MainActivity.exlist.get(j).objects.size(); q++) {//загружаем все объекты 1 экскурсии
                        tempFileToUpload = File.createTempFile("file", ".txt", tempDropboxDirectory);//загрузка текстового файла
                        fileWriter = new FileWriter(tempFileToUpload);
                        fileWriter.write(MainActivity.exlist.get(j).objects.get(q).obobjectText);
                        fileWriter.close();
                        FileInputStream text = new FileInputStream(tempFileToUpload);
                        set.dropboxApi.putFile(set.mPath + "/" + MainActivity.exlist.get(j).objects.get(q).obname + "/text.txt", text, tempFileToUpload.length(), null, null);
                        tempFileToUpload = File.createTempFile("geo", ".txt", tempDropboxDirectory);//загрузка geo файла
                        fileWriter = new FileWriter(tempFileToUpload);
                        String s = MainActivity.exlist.get(j).objects.get(q).oblatitude + " : " + MainActivity.exlist.get(j).objects.get(q).oblongitude;
                        fileWriter.write(s);
                        fileWriter.close();
                        FileInputStream text1 = new FileInputStream(tempFileToUpload);
                        set.dropboxApi.putFile(set.mPath + "/" + MainActivity.exlist.get(j).objects.get(q).obname + "/geo.txt", text1, tempFileToUpload.length(), null, null);
                        for (int i = 0; i < MainActivity.exlist.get(j).objects.get(q).obselectedphotos.size(); i++)//загружаем фото
                        {
                            set.mCurrentFileIndex = i;
                            File file = new File(MainActivity.exlist.get(j).objects.get(q).obselectedphotos.get(i).filePath);
                            fis = new FileInputStream(file);
                            String path = set.mPath + "/" + MainActivity.exlist.get(j).objects.get(q).obname + "/photo" + i + ".jpg";
                            set.mRequest = set.mApi.putFileOverwriteRequest(path, fis, file.length(), null);
                            set.mRequest.upload();
                            if (!isCancelled()) {
                                set.mFilesUploaded++;
                            } else {
                                return false;
                            }
                        }
                        for (int i = 0; i < MainActivity.exlist.get(j).objects.get(q).obselectedvideos.size(); i++)//загружаем видео
                        {
                            set.mCurrentFileIndex = i;
                            File file = new File(MainActivity.exlist.get(j).objects.get(q).obselectedvideos.get(i).filePath);
                            fis = new FileInputStream(file);
                            String path = set.mPath + "/" + MainActivity.exlist.get(j).objects.get(q).obname + "/video" + i + ".mp4";
                            set.mRequest = set.mApi.putFileOverwriteRequest(path, fis, file.length(), null);
                            set.mRequest.upload();
                            if (!isCancelled()) {
                                set.mFilesUploaded++;
                            } else {
                                return false;
                            }
                        }
                        for (int i = 0; i < MainActivity.exlist.get(j).objects.get(q).obselectedaudios.size(); i++)//загружаем аудио
                        {
                            set.mCurrentFileIndex = i;
                            File file = new File(MainActivity.exlist.get(j).objects.get(q).obselectedaudios.get(i).filePath);
                            fis = new FileInputStream(file);
                            String path = set.mPath + "/" + MainActivity.exlist.get(j).objects.get(q).obname + "/audio" + i + ".mp3";
                            set.mRequest = set.mApi.putFileOverwriteRequest(path, fis, file.length(), null);
                            set.mRequest.upload();
                            if (!isCancelled()) {
                                set.mFilesUploaded++;
                            } else {
                                return false;
                            }
                        }
                    }
                    publishProgress(Long.parseLong("" + k));
                }
            }
            return true;
        } catch (Exception e) {
            set.mErrorMsg = "The was an error during the upload!";
            return false;
        }
    }

    //апдейт прогресса загрузки объектов в DropBox
    @Override
    protected void onProgressUpdate(Long... progress) {
        mDialog.setProgress(Integer.parseInt("" + progress[0]));
        super.onProgressUpdate(progress);
    }

    //загрузка завершена, проверяем ее успех
    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result)
            set.api_Listener.onSuccess(set.requestNumber, result);
        else
            showToast(set.mErrorMsg);
    }

    //вывод сообщения об ошибке на экран для пользователя
    private void showToast(String msg) {
        Toast error = Toast.makeText(set.mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}