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
class UserHandler implements Runnable {

    private Server server;
    private User user;


    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.showUsers();
    }
    public void run() {
        String message;

        // when there is a new message, broadcast to all
        Scanner scanner = new Scanner(this.user.getInputStream());
        while (scanner.hasNextLine()) {
            message = scanner.nextLine();

            // Output for private messages
            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private message : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendPrivatemsg(
                            message.substring(
                                    firstSpace+1, message.length()
                            ), user, userPrivate
                    );
                }
                // update user list
                this.server.showUsers();
            }else{
                server.sendMessage(message, user);
            }
        }
        // end of Thread
        server.removeUser(user);
        this.server.showUsers();
        scanner.close();
    }
}
