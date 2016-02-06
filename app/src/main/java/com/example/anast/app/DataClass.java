package com.example.anast.app;

import android.net.Uri;

//класс, хранящий в себе информацию о всех мультимедиа объектах
class DataClass {
    public String filePath;//путь к файлу
    public Uri coverPath;//путь к обложке
    public int id;//ай ди файла
    public String mimeType;//mime type
    public String thumbPath;//путь к миниатюре
    public String title;//название
    private boolean selected;//выбран ли файл

    public DataClass() {
        super();
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
