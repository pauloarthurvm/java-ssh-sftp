package basicexamples;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Sftp {

    public static void main(String[] args) {

        String username = "username";
        String password = "password";
        String host = "127.0.0.1";
        String fileInResourceDir = "whilescript.sh";
        String remoteFile = "/root/whilescript.sh";

        uploadFileUsingSshj(username, password, host, fileInResourceDir, remoteFile);

    }

    private static void uploadFileUsingSshj(String username, String password, String host, String localFile,
                                            String remoteFile) {
        try {
            SSHClient sshClient = setupSshj(username, password, host);
            SFTPClient sftpClient = sshClient.newSFTPClient();
            System.out.println("Setup done");

            sftpClient.put(getFileFromResource(localFile), remoteFile);
            System.out.println("File sent");

            sftpClient.close();
            sshClient.disconnect();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static SSHClient setupSshj(String username, String password, String host) throws IOException {
        System.out.println("Start setup to send file");
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(host);
        client.authPassword(username, password);
        return client;
    }

    private static FileSystemFile getFileFromResource(String fileName) throws URISyntaxException {
        System.out.println("Getting file from resources folder");
        ClassLoader classLoader = Sftp.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new FileSystemFile(new File(resource.toURI()));
        }
    }

}
