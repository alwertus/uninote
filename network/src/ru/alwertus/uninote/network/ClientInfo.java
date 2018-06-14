package ru.alwertus.uninote.network;

public class ClientInfo {
    private String name;
    public String login;
    public boolean isLogining;

    public ClientInfo(String name) {
        this.name = name;
        isLogining = false;
        login = "";
    }

    public void changeName(String newName) {
        if (newName.length()>0)
            name = newName;
    }

    public String getName() {
        return name;
    }
}
