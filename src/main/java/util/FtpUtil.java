package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

public class FtpUtil {

	private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
	private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
	private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");
	
	public FtpUtil(String ip,int port,String user,String pwd) {
		this.ip=ip;
		this.port=port;
		this.user=user;
		this.pwd=pwd;
	}
	
	public static boolean uploadFile(List<File> fileList) throws  Exception {
		FtpUtil ftpUtil = new FtpUtil(ftpIp, 21, ftpUser,ftpPass);
		boolean result = ftpUtil.uploadFile("img", fileList);
		return result;
	}
	
	private boolean uploadFile(String remotePath,List<File> fileList) throws Exception {
		boolean uploadFile = true;
		FileInputStream fis = null;
		if(connectServer(this.ip, this.port, this.user,this.pwd)) {
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				//设置成二进制
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				//打开本地的被动模式
				ftpClient.enterLocalPassiveMode();
				for (File fileItem : fileList) {
					fis = new FileInputStream(fileItem);
					ftpClient.storeFile(fileItem.getName(), fis);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				uploadFile=false;
				e.printStackTrace();
			}finally {
				fis.close();
				ftpClient.disconnect();
			}
		}
		return uploadFile;
	}
	
	private boolean connectServer(String ip,int port,String user,String pwd) {
		ftpClient = new FTPClient();
		boolean isSucess = false;
		try {
			ftpClient.connect(ip);
			isSucess = ftpClient.login(user, pwd);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
		
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSucess;
	}
	
	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	
	
}
