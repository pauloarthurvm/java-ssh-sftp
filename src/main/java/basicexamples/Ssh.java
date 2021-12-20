package basicexamples;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Ssh {

    private static Session session;
    private static ChannelExec channel;

    public static void main(String[] args) throws JSchException {
        session = null;
        channel = null;
        String username = "username";
        String password = "password";
        String host = "127.0.0.1";
        final int port = 22;

        connect(username, password, host, port);
        getText("ls");
        disconnect();
    }

    public static void connect(String username, String password, String host, int port) throws JSchException {
        System.out.println("Starting connection\n");
        session = new JSch().getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    // Separated in 2 functions, in case of a need to do some work in received result
    public static void getText(String command) {
        String output = null;
        System.out.println("Using " + Ssh.class.toString() + "\n");
        try {
            output = listFolderStructure(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(output);
        return;
    }

    public static String listFolderStructure(String command) throws Exception {
        channel = (ChannelExec) session.openChannel("exec");
        System.out.println("Running command:\n" + command);
        channel.setCommand(command);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);
        channel.connect();

        while (channel.isConnected()) {
            Thread.sleep(1000);
        }

        return responseStream.toString();
    }

    public static void disconnect() {
        if (session != null) {
            session.disconnect();
            System.out.println("Session disconnected\n");
        }
        if (channel != null) {
            channel.disconnect();
            System.out.println("Channel disconnected\n");
        }
    }

    // For a command with a long period response
    public StringBuilder sendCommandLoggingResult(String command) throws JSchException, IOException {
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();

        StringBuilder result = new StringBuilder();
        InputStream in = channel.getInputStream();
        String tmpString;
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
                tmpString = new String(tmp, 0, i);
                System.out.print(tmpString);
                result.append(tmpString);
            }
            if (channel.isClosed()) {
                break;
            }
        }
        return result;
    }

}
