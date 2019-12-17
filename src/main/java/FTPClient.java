import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

class FtpClient {

    private FTPClient ftp = null;

    /**
     * Eastablishes an FTP connection to the host using the username and password provided.
     * Does not support TLS
     *
     * @param host ip address of the ftp server
     * @param user username
     * @param pwd  password
     * @throws Exception
     */
    public FtpClient(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect(host);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server: " + host);
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    /**
     * Upload the file to the remote server location
     *
     * @param localFileFullName absolute file path of the file to upload
     * @param fileName          name of the file on upload
     * @param hostDir           relative location in the host directory to upload the file to
     * @throws Exception
     */
    public void uploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
        InputStream input = new FileInputStream(new File(localFileFullName));
        this.ftp.storeFile(hostDir + fileName, input);
    }

    /**
     * Checks for an existing connection and disconnects
     */
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
            }
        }
    }


    public static void main(String[] args) throws Exception {
        // To test this code out you can use any existing server or setup up a new FileZilla Server on your local machine.
        // Ensure the user has read & write privileges for the below scenario to work

        // 1. Eastablish a connection to the FTP server
        FtpClient ftpUploader = new FtpClient("192.168.3.92", "prathamesh", "@four123");
        System.out.println("##### FTP Connection eastablished #####");
        // 2. Upload a binary file
        File file = new File(ftpUploader.getClass().getClassLoader().getResource("wallpaper_1.jpg").getFile());
        ftpUploader.uploadFile(file.getAbsolutePath(), "wallpaoper.png", "/");
        // 3. Disconnect from the server
        ftpUploader.disconnect();
        System.out.println("##### File upload successfull #####");
    }
}