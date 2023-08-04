import javax.management.loading.PrivateClassLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

public class Server {

     int port;
     List<User> clients;
     ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(50000).run();
    }
    //server constructor
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }
    //run method (most important)
    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("Port 50000 is open.");

        while (true) {
            // accepts a new client
            Socket socket = server.accept();
            // get nickname of newUser
            String username = (new Scanner ( socket.getInputStream() )).nextLine();
            username = username.replace(",", ""); //  ',' use for serialisation
            username = username.replace(" ", "_");
            System.out.println("New Client: \"" + username + "\"\n\t     Host:" + socket.getInetAddress().getHostAddress());
            User newUser;
            //checks if this is the first user
            if (clients.isEmpty()){
                // create first new User as coordinator
                newUser = new User(socket, username,true);
                //calls coordinator message
                sendCoordinator(newUser);
            }else{
                // create new User
                newUser = new User(socket, username);
                for (User client : this.clients) {
                    client.getOutStream().println("System: "+newUser+" joined the conversation");
                }
            }
            // add newUser to list of users
            this.clients.add(newUser);
            // create a new thread for newUser incoming messages handling
            new Thread(new UserHandler(this, newUser)).start();
        }
    }

    // delete a user from the list
    public void removeUser(User user){
        this.clients.remove(user);
        //informs everyone else in the server
        for (User client : this.clients) {
            client.getOutStream().println("System: "+user+" left the conversation");
        }
        //the coordinator role is taken by the next user (in order of joining)
        clients.get(0).setFlagC(true);
        //the new coordinator gets notified
        sendCoordinator(clients.get(0));
    }

    // send incoming msg to all Users
    public void sendMessage(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(
                    userSender.toString() + "<span>: " + msg+"</span>");
        }
    }
    //method used to let the coordinator know he was chosen
    public void sendCoordinator(User client) {
        client.getOutStream().println("<span>" + "You are the coordinator"+"</span>");
    }


    // This sends the list of clients to all users.
    public void showUsers(){
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }

    // Sends a private message to a user.
    public void sendPrivatemsg(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client.getUsername().equals(user) && client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(
                        "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender.toString() + " -> (<b>no one!</b>): " + msg);
        }
    }
}
