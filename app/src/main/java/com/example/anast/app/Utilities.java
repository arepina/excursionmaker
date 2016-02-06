package com.example.anast.app;


public class Utilities {

    //милисекунды в формат таймера Hours:Minutes:Seconds
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (milliseconds / (Constants.MILLISEC * Constants.MINUTE * Constants.MINUTE));
        int minutes = (int) (milliseconds % (Constants.MILLISEC * Constants.MINUTE * Constants.MINUTE)) / (Constants.MILLISEC * Constants.MINUTE);
        int seconds = (int) ((milliseconds % (Constants.MILLISEC * Constants.MINUTE * Constants.MINUTE)) % (Constants.MILLISEC * Constants.MINUTE) / Constants.MILLISEC);
        if (hours > 0)
            finalTimerString = hours + ":";
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        // строка с временем для таймера
        return finalTimerString;
    }

    //получаем прогресс в процентах
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / Constants.MILLISEC);
        long totalSeconds = (int) (totalDuration / Constants.MILLISEC);
        percentage = (((double) currentSeconds) / totalSeconds) * Constants.PROC;
        return percentage.intValue();
    }

    //возвращаем текущее время таймера в милисекундах
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / Constants.MILLISEC);
        currentDuration = (int) ((((double) progress) / Constants.PROC) * totalDuration);
        return currentDuration * Constants.MILLISEC;
    }
}
