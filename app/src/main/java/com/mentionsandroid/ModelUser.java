package com.mentionsandroid;

/**
 * Created by Nidhi on 23-04-2016.
 */
public class ModelUser {
    int startPosition;
    int endPosition;
    String userName;

    public ModelUser(int startPosition, int endPosition, String userName) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.userName = userName;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
