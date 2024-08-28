package edu.escuelaing.arem.ASE.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AppTest {

    private OutputStream outputStream;
    private Request mockRequest;
    private Response mockResponse;

    private static final String TEST_DIRECTORY = "testwebroot";


    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        mockRequest = mock(Request.class);
        mockResponse = new Response(outputStream);
        Path testPath = Paths.get("target/classes/" + TEST_DIRECTORY);
        try {
            Files.deleteIfExists(testPath);
        } catch (IOException e) {
            e.printStackTrace();
            fail("No se pudo eliminar el directorio de prueba antes de la ejecución de la prueba.");
        }
    }

    @AfterEach
    public void tearDown() {
        Path testPath = Paths.get("target/classes/" + TEST_DIRECTORY);
        try {
            Files.deleteIfExists(testPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDirectory() {
        // Llama al método staticfiles
        SimpleWebServer.staticfiles(TEST_DIRECTORY);

        // Verifica que el directorio se haya creado
        Path testPath = Paths.get("target/classes/" + TEST_DIRECTORY);
        assertTrue(Files.exists(testPath), "El directorio debería haber sido creado.");
    }

    @Test
    public void testStartServices_apiWorking() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api");
        String result = service.handleREST(mockRequest, mockResponse);
        assertEquals("API WORKING", result);
    }

    @Test
    public void testGreetService() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api/greet");

        when(mockRequest.getValue("name")).thenReturn("John");
        when(mockRequest.getValue("greeting")).thenReturn("Hello");

        String result = service.handleREST(mockRequest, mockResponse);
        assertEquals("Hello John", result);
    }

    @Test
    public void testCalculateService_add() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api/calculate");

        when(mockRequest.getValue("operation")).thenReturn("add");
        when(mockRequest.getValue("num1")).thenReturn("5");
        when(mockRequest.getValue("num2")).thenReturn("10");

        String result = service.handleREST(mockRequest, mockResponse);
        assertEquals("15.0", result);
    }

    @Test
    public void testCalculateService_divideByZero() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api/calculate");

        when(mockRequest.getValue("operation")).thenReturn("divide");
        when(mockRequest.getValue("num1")).thenReturn("10");
        when(mockRequest.getValue("num2")).thenReturn("0");

        String result = service.handleREST(mockRequest, mockResponse);
        assertEquals("Cannot divide by zero", result);
    }

    @Test
    public void testSystemInfoService() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api/system-info");

        String result = service.handleREST(mockRequest, mockResponse);

        assert result.contains("\"OS\":");
        assert result.contains("\"Java Version\":");
    }

    @Test
    public void testIndexService() {
        SimpleWebServer.startServices();
        RESTService service = SimpleWebServer.services.get("/api/index");

        String result = service.handleREST(mockRequest, mockResponse);
        assert result.contains("<html>");
        assert result.contains("Bienvenido a la API SimpleWebServer");
    }
}
