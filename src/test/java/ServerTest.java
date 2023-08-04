import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.rmi.server.LogStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ServerTest {
    @Test
    void testConstructor() {
        assertTrue((new Server(8080)).clients.isEmpty());
    }


    @Test
    void testSendMessage() {
        Server server = new Server(8080);
        server.sendMessage("Msg", null);
        assertTrue(server.clients.isEmpty());
        assertEquals(8080, server.port);
    }


    @Test
    void testSendCoordinator() {
        Server server = new Server(8080);
        User user = mock(User.class);
        when(user.getOutStream()).thenReturn(LogStream.log("Name"));
        server.sendCoordinator(user);
        verify(user).getOutStream();
    }

    @Test
    void testShowUsers() {
        Server server = new Server(8080);
        server.showUsers();
        assertTrue(server.clients.isEmpty());
        assertEquals(8080, server.port);
    }


    @Test
    void testSendPrivatemsg() {
        Server server = new Server(8080);
        User user = mock(User.class);
        when(user.getOutStream()).thenReturn(LogStream.log("Name"));
        when(user.getUsername()).thenReturn("janedoe");
        server.sendPrivatemsg("Msg", user, "User");
        verify(user).getOutStream();
        verify(user).getUsername();
    }
}

