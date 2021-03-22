package smb.client;

import java.io.IOException;
import java.util.ArrayList;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import smb.gui.GUI;

public class SambaClient {

    private final String userName;
    private final char[] password;
    private final String shareFolderName;
    private final String hostIP;
    private final String filterTag;
    private String subFolder;
    private ArrayList<String> folderList;
    private ArrayList<String[]> filePathList;

    public SambaClient(GUI gui, String userName, char[] password, String shareFolderName, String hostIP,
            String filterTag, String subFolder) {
        this.userName = userName;
        this.password = password;
        this.shareFolderName = shareFolderName;
        this.hostIP = hostIP;
        this.filterTag = filterTag;
        this.subFolder = subFolder;

    }

    public void listSubFolder(Session session, String subFolderPath, int level) {
        // Connect to Share
        String subPath = subFolderPath;
        int subLevel = level;
        subLevel += 1;
        try (DiskShare share = (DiskShare) session.connectShare(this.shareFolderName)) {
            for (FileIdBothDirectoryInformation f : share.list(subPath, this.filterTag)) {
                String[] temp = { String.format("%s", subLevel), subPath, f.getFileName() };
                this.filePathList.add(temp);
                if (f.getFileAttributes() == 48 && !f.getFileName().endsWith(".")) {
                    this.folderList.add("| ".repeat(subLevel) + "|--" + f.getFileName() + " Path: " + subPath);
                    listSubFolder(session, subPath + f.getFileName() + "/", subLevel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> retrieveFolderContentList() {

        SMBClient client = new SMBClient();
        this.folderList = new ArrayList<String>();
        this.filePathList = new ArrayList<String[]>();

        try (Connection connection = client.connect(this.hostIP)) {
            AuthenticationContext ac = new AuthenticationContext(this.userName, this.password, "DOMAIN");
            Session session = connection.authenticate(ac);
            this.subFolder = ("".equals(this.subFolder)) ? this.subFolder : this.subFolder + "/";
            folderList.add(this.shareFolderName + "/" + this.subFolder);
            listSubFolder(session, this.subFolder, -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        return this.folderList;
    }

    public ArrayList<String> getFolderList() {
        return folderList;
    }

    public void setFolderList(ArrayList<String> folderList) {
        this.folderList = folderList;
    }

    public ArrayList<String[]> getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(ArrayList<String[]> filePathList) {
        this.filePathList = filePathList;
    }
}
