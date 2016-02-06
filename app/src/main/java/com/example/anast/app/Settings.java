package com.example.anast.app;

import android.content.Context;

import com.dropbox.client2.DropboxAPI;

//Класс настроек дропбокса
class Settings
{
    public DropboxAPI<?> mApi;//Dropbox API
    public String mPath;//путь к объекту
    public DropboxAPI.UploadRequest mRequest;//запрос на загрузку
    public Context mContext;//контекст
    public String mErrorMsg;//вывод ошибки
    public int mFilesUploaded;//кол-во загружаемых файлов
    public int mCurrentFileIndex;//номер загружаемого файла
    public API_Listener api_Listener;//обработчик API
    public int requestNumber;//номер запроса на загрузку
    public DropboxAPI dropboxApi;//DropBox API

    public Settings(int request_num, MainActivity activity, DropboxAPI dropboxApi, DropboxAPI<?> api, String dropboxPath)
    {
        this.dropboxApi = dropboxApi;
        mContext = activity;
        api_Listener = activity;
        requestNumber = request_num;
        mApi = api;
        mPath = dropboxPath;
        mFilesUploaded = 0;
        mCurrentFileIndex = 0;
    }
}
