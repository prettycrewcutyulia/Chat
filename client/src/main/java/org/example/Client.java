package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 50001;

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Connected to the server.");
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            Thread readerThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int bytesRead = input.read(buffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        System.out.print(new String(buffer, 0, bytesRead));
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            readerThread.start();

            while (true) {
                System.out.print("> ");
                String inputLine = scanner.nextLine();
                if (inputLine.toLowerCase().startsWith("/quit")) {
                    output.write(inputLine.getBytes());
                    return;
                }
                output.write(inputLine.getBytes());
                output.write("\n".getBytes());
            }
        }
    }
}
