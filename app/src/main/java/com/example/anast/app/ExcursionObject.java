package com.example.anast.app;

import java.util.ArrayList;

//Класс - объект экскурсии
class ExcursionObject {
    public ArrayList<DataClass> obselectedphotos;//конкретные фото отдельного объекта
    public ArrayList<DataClass> obselectedvideos;//конкретные видео отдельного объекта
    public ArrayList<DataClass> obselectedaudios;//конкретные аудио отдельного объекта
    public String obobjectText;//конкретный текст отдельного объекта
    public Double oblatitude;//широта объекта
    public Double oblongitude;//долгота объекта
    public String obname;//запланированное имя объекта

    public ExcursionObject() {
        obselectedphotos = new ArrayList<>();
        obselectedvideos = new ArrayList<>();
        obselectedaudios = new ArrayList<>();
        obname = "";
        obobjectText = "";
        oblatitude = 0.0;
        oblongitude = 0.0;
    }
}
