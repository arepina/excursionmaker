package com.example.anast.app;

import com.dropbox.client2.session.Session;

//данные дропбокса
public interface DropboxData {
    String DROPBOX_FILE_DIR = "";//      /ExcursionMaker/
    String DROPBOX_NAME = "dropbox_prefs";
    String ACCESS_KEY = "ejnv6emaxutkxkb";
    String ACCESS_SECRET = "2tpqqwbnrptt8qn";
    Session.AccessType ACCESS_TYPE = Session.AccessType.APP_FOLDER;
}
