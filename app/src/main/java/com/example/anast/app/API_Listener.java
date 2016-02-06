package com.example.anast.app;

public interface API_Listener {
    void onSuccess(int requestnumber, Object obj);//загрузка прошла успешно

    void onFail(String errormessage);//возникли проблемы при загрузке
}