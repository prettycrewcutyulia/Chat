package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static final int PORT = 50001;
    private static Set<String> names = new HashSet<>();
    private static Set<Socket> sockets = new HashSet<>();

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                Socket socket = listener.accept();
                new Handler(socket).start();
            }
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private Scanner in;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                while (true) {
                    socket.getOutputStream().write("Enter your name: ".getBytes());
                    name = in.nextLine().trim();
                    if (!name.isEmpty() && !names.contains(name)) {
                        names.add(name);
                        break;
                    }
                }
                sockets.add(socket);
                System.out.println(name + " has joined the chat.");
                broadcast(name + " has joined the chat.");

                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    broadcast(name + ": " + input);
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                if (socket != null) {
                    sockets.remove(socket);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
                broadcast(name + " has left the chat.");
            }
        }
    }

    private static void broadcast(String message) {
        for (Socket socket : sockets) {
            try {
                socket.getOutputStream().write(message.getBytes());
                socket.getOutputStream().write("\n".getBytes());
            } catch (IOException e) {
            }
        }
    }
}
