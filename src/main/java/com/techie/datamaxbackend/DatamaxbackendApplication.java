package com.techie.datamaxbackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DatamaxbackendApplication {
	
	final static String ROOT_FOLDER_PATH = System.getProperty("user.home") + "/datamaxuploads/";

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DatamaxbackendApplication.class, args);
		
		try {
			System.out.println("ROOT_FOLDER_PATH:"+ROOT_FOLDER_PATH);
			File file = new File(ROOT_FOLDER_PATH);
			if(!file.exists()) {
				file.mkdir();
				System.out.println("Project Directory Dreated for Storing Files..");
			}
		} catch (Exception e) {
			System.out.println("Something Went Wrong: "+e);
		}
		
	
			
		
	}

}
