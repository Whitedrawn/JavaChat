/***************************************************
 *    Module: COMP1549 - Advanced Programming
 *    Title: Java application: Multi Thread Chat Server
 *    Author: David Buendia Soler(001076459), Sergiu Macsim (001120364)
 *    Group : 9
 *    Date: 29/03/2022

 Reference:

 *    Title: <java-chat>
 *    Author: <pchampio>
 *    Date: <12 August 2017>
 *    Availability: <https://github.com/pchampio/java-chat>
 ****************************************************/
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

class User {
     static int nbUser = 0;
     int userId;
     PrintStream streamOut;
     InputStream streamIn;
     String username;
     Socket client;
    private Boolean flagC = false;

    // constructor
    public User(Socket client, String name) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.username = name;
        this.userId = nbUser;
        nbUser += 1;
    }

    //overloading the constructor so different types of users can be created
    public User(Socket client, String name, Boolean flagC) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.username = name;
        this.userId = nbUser;
        nbUser += 1;
        this.flagC = flagC;
    }

    public static int getNbUser() {
        return nbUser;
    }

    public static void setNbUser(int nbUser) {
        User.nbUser = nbUser;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public PrintStream getStreamOut() {
        return streamOut;
    }

    public void setStreamOut(PrintStream streamOut) {
        this.streamOut = streamOut;
    }

    public InputStream getStreamIn() {
        return streamIn;
    }

    public void setStreamIn(InputStream streamIn) {
        this.streamIn = streamIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public Boolean getFlagC() {
        return flagC;
    }

    public void setFlagC(Boolean flagC) {
        this.flagC = flagC;
    }

    // Returning streams and username
    public PrintStream getOutStream() {
        return this.streamOut;
    }

    public InputStream getInputStream() {
        return this.streamIn;
    }

    public String getUsername() {
        return this.username;
    }

    // print users
    public String toString() {

        return "<u><span style='color:"
                + "'>" + this.getUsername() + "</span></u>";

    }
}