package service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

	 String upload(MultipartFile file,String path) throws Exception;
}
