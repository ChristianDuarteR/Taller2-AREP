package edu.escuelaing.arem.ASE.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import static org.mockito.Mockito.*;

public class AppTest {

    private RESTService restService;
    private Socket mockSocket;
    private OutputStream mockOutputStream;
    private BufferedReader mockBufferedReader;

    @Before
    public void setUp() throws IOException {
        restService = new RestServiceImpl();
        mockSocket = Mockito.mock(Socket.class);
        mockOutputStream = Mockito.mock(OutputStream.class);
        mockBufferedReader = Mockito.mock(BufferedReader.class);

        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        when(mockSocket.getRemoteSocketAddress()).thenReturn(mock(SocketAddress.class));
    }

    @Test
    public void testHandleGet() throws IOException {
        when(mockBufferedReader.readLine())
                .thenReturn("GET /api/resource HTTP/1.1")
                .thenReturn("Host: localhost")
                .thenReturn("")
                .thenReturn(null);

        restService.handleGet(new String[]{"GET", "/api/resource"}, mockBufferedReader, mockOutputStream, mockSocket);

        verify(mockOutputStream, times(1)).write(any(byte[].class));
    }

    @Test
    public void testHandlePost() throws IOException {
        StringBuilder jsonString = new StringBuilder("{\"message\":\"Hello World\"}");
        BufferedReader reader = new BufferedReader(new StringReader("POST /api HTTP/1.1\r\nContent-Length: 27\r\n\r\n" + jsonString));
        OutputStream outputStream = Mockito.mock(OutputStream.class);

        RestServiceImpl service = new RestServiceImpl();

        service.handlePost(reader, outputStream);

        Mockito.verify(outputStream, Mockito.times(1)).write(Mockito.any(byte[].class));
    }
}
