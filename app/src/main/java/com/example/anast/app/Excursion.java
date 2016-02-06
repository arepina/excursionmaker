package com.example.anast.app;

import java.util.ArrayList;

//Класс - экскурсия
class Excursion {
    public Excursion() {
        objects = new ArrayList<>();
        exname = "";
        selected = false;
    }

    public boolean selected;//выбран ли пользователем объект экскурсии для дальнейшего сохранения
    public String exname;//запланированное имя экскурсии
    public ArrayList<ExcursionObject> objects;//объекты конкретной экскурсии
}
