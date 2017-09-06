package service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;

import service.IFileService;
import util.FtpUtil;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
	public String upload(MultipartFile file,String path) throws Exception {
		String fileName = file.getOriginalFilename();
		String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
		String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
		File fileDir = new File(path);
		if(!fileDir.exists()) {
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		File targetFile = new File(path, uploadFileName);
		try {
			file.transferTo(targetFile);
			//todo 上传到FTP服务器上
			FtpUtil.uploadFile(Lists.newArrayList(targetFile));
			//上传成功后，删除本地图片
			targetFile.delete();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetFile.getName();
	}
}
