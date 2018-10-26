package com.michalraq.proximitylightapp;

public class ServerContent {

    private  String serverIP;
    private int portNumber;

    public ServerContent( String ip , int port){
        serverIP = ip;
        portNumber =port;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
