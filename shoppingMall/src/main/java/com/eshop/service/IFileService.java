package com.eshop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Paula Lin
 *
 */
public interface IFileService {

	/**
	 * 
	 * @param file
	 * @param path
	 * @return
	 */
    String upload(MultipartFile file, String path);
}
