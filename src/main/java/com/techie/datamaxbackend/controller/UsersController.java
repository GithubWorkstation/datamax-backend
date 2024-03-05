package com.techie.datamaxbackend.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techie.datamaxbackend.helpers.FileSecurity;
import com.techie.datamaxbackend.model.Response;
import com.techie.datamaxbackend.model.SharedWithMe;
import com.techie.datamaxbackend.model.Users;
import com.techie.datamaxbackend.repository.ShareWithMeRepository;
import com.techie.datamaxbackend.repository.UsersRepository;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:4300" })
public class UsersController {
	
	@Autowired
	ShareWithMeRepository shMeRepository;

	@Autowired
	UsersRepository repository;

	final String TAG = "User";
	final String ROOT_FOLDER_PATH = System.getProperty("user.home") + "/datamaxuploads/";


	@PostMapping(path = "/login")
	public ResponseEntity<Response<Users>> loginUser(@RequestParam String email, @RequestParam String password)
			throws NullPointerException {

		Date date = new Date();

		try {
			Users user = repository.findByEmailAndPassword(email, password).get();
			List<Users> users = new ArrayList<Users>();
			users.add(user);
			
			if(user.getStatus() == 1) {
				return new ResponseEntity<Response<Users>>(new Response<Users>(101, "", users),
						HttpStatus.valueOf(201));				
			} else {
				return new ResponseEntity<Response<Users>>(new Response<Users>(102, "Your account has been deactivated", null),
						HttpStatus.valueOf(201));	
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<Response<Users>>(new Response<>(101, e.getMessage(), null),
					HttpStatus.valueOf(404));
		}

	}

	@SuppressWarnings("unused")
	@PostMapping(path = "/add")
	public Response<Users> addUser(@RequestParam String email, @RequestParam String password,
			@RequestParam String fullName, @RequestParam int access, @RequestParam int status)
			throws NoSuchAlgorithmException {

		Date date = new Date();
		List<Users> users = repository.findByEmail(email);
		if (users.size() == 0) {
			// new user
			String secretKey = FileSecurity.getNewSecretKey();
			Users user = new Users(null, email, password, fullName, secretKey, access, status, date);

			// Creating New Folder for user when they registered on data max server with
			// their full name
			String FOLDER_NAME = user.getEmail().trim();
			new File(ROOT_FOLDER_PATH + FOLDER_NAME).mkdir();

			repository.save(user);
			return new Response<Users>(101, TAG + " Saved Successfully at " + date, new ArrayList<Users>() {
				{
					add(user);
				}
			});
		} else {
			// already registered with this email address
			File dirPth = new File(ROOT_FOLDER_PATH + users.get(0).getRootFolderName());
			if (dirPth == null) {
				dirPth.mkdir();
			}

			return new Response<Users>(101, "Already registered with email address", users);
		}

	}

	@PostMapping(path = "/upload")
	public Response<Users> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("rootFolderName") String userRootFolderName, @RequestParam("secretKey") String secretKey,
			@RequestParam("isUploadDirectory") String isUploadDirectory)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, IOException, NullPointerException {

		String filePath;
		if (isUploadDirectory.equals("true")) {
			filePath = userRootFolderName;
		} else {
			filePath = ROOT_FOLDER_PATH + userRootFolderName;
		}

		filePath += "/" + file.getOriginalFilename();
		byte[] encryptedContent = FileSecurity.encryptFile(file, secretKey);

		FileOutputStream outputstream = new FileOutputStream(new File(filePath));
		if (outputstream != null) {
			outputstream.write(encryptedContent);
		}

		return new Response<Users>(101, "File Uplaoded Successfully", null);
	}

	@GetMapping(path = "/create-new-folder")
	public ResponseEntity<String> createnewFolder(@RequestParam("folderName") String folderName,
			@RequestParam("rootFolderName") String rootFolderName) {
		try {
			String newFolderPath = ROOT_FOLDER_PATH + rootFolderName + "/" + folderName;
			File newFolder = new File(newFolderPath);
			newFolder.mkdir();

			return new ResponseEntity<>(HttpStatus.valueOf(201));
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.valueOf(404));
		}
	}

	@GetMapping(path = "/delete-file")
	public ResponseEntity<Object> deleteFile(@RequestParam("filePath") String filePath) {
		try {
			File fileToBeDeleted = new File(filePath);
			fileToBeDeleted.delete();
			return new ResponseEntity<>(HttpStatus.valueOf(201));
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.valueOf(404));
		}
	}

	@GetMapping(path = "/getAllFiles")
	public List<Map<String, Object>> getAllFiles(@RequestParam("rootFolderName") String userRootFolder,
			@RequestParam(value="isSubFolder", required = false) boolean isSubFolder) throws NullPointerException {
		
		List<Map<String, Object>> files = new ArrayList<>();

		File dirPath;
		if (isSubFolder) {
			dirPath = new File(userRootFolder);
		} else {
			dirPath = new File(ROOT_FOLDER_PATH + userRootFolder);
		}

		File[] dirFiles = dirPath.listFiles();
		if (dirFiles != null) {
			for (File file : dirFiles) {
				Map<String, Object> fileMap = new HashMap<>();
				fileMap.put("fileName", file.getName());
				fileMap.put("filePath", file.getPath());
				fileMap.put("fileAbsolutePath", file.getAbsolutePath());
				fileMap.put("isDirectory", file.isDirectory());
				fileMap.put("isFile", file.isFile());
				fileMap.put("size", file.length());
				fileMap.put("uploadedOn", file.lastModified());

				files.add(fileMap);
			}
		}

		return files;
	}

	@GetMapping(path = "/get-encrypted-file")
	public Map<String, Object> getEncryptedFile(@RequestParam("filePath") String filePath) throws IOException {
		File file = new File(filePath);
		byte[] fileContent = Files.readAllBytes(file.toPath());

		Map<String, Object> fileMap = new HashMap<>();
		fileMap.put("fileName", file.getName());
		fileMap.put("content", fileContent);

		return fileMap;

	}

	@PostMapping(path = "/get-decrypted-file")
	public Map<String, Object> getDecryptedFile(@RequestParam("filePath") String filePath,
			@RequestParam("secretKey") String secretKey) throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, NullPointerException {
		File file = new File(filePath);
		byte[] fileContent = FileSecurity.decryptFile(filePath, secretKey);

		Map<String, Object> fileMap = new HashMap<>();
		fileMap.put("fileName", file.getName());
		fileMap.put("content", fileContent);

		return fileMap;
	}

	@PostMapping(path = "/share-file")
	public ResponseEntity<Object> shareFileWithUserMetho(@RequestParam("filePath") String filePath,
			@RequestParam("sharedToUserEmail") String sharedToUserEmail,
			@RequestParam("sharedByUserId") int sharedByUserId, @RequestParam("sharedToUserId") int sharedToUserId,
			@RequestParam("sharedBySecretKey") String sharedBySecretKey) {
		
		try {
			List<Users> users = repository.findByEmail(sharedToUserEmail);
			if(users.size() != 0) {
				Users user = users.get(0);
				SharedWithMe shareWithMe = new SharedWithMe(null, filePath, user.getEmail(), sharedByUserId, user.getUserId(), sharedBySecretKey);
				shMeRepository.save(shareWithMe);
				
				return new ResponseEntity<>(HttpStatus.valueOf(201));
			} else {
				return new ResponseEntity<>(HttpStatus.valueOf(500));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<>(e, HttpStatus.valueOf(500));
		}

	}
	
	@GetMapping(path = "/get-all-shared-files")
	public List<Map<String, Object>> getAllSharedFiles(@RequestParam("rootFolderName") String emailAddress) {
		List<SharedWithMe> sharedFilesRows = shMeRepository.findBySharedToUserEmail(emailAddress);
		List<Map<String, Object>> list = new ArrayList<>();
		if (sharedFilesRows != null) {
			for (SharedWithMe shFile: sharedFilesRows) {
				Map<String, Object> map = new HashMap<>();
				map.putAll(shFile.getMap());
				
				File file = new File(shFile.getFilePath());
				Map<String, Object> fileMap = new HashMap<>();
				fileMap.put("fileName", file.getName());
				fileMap.put("filePath", file.getPath());
				fileMap.put("fileAbsolutePath", file.getAbsolutePath());
				fileMap.put("isDirectory", file.isDirectory());
				fileMap.put("isFile", file.isFile());
				fileMap.put("size", file.length());
				fileMap.put("uploadedOn", file.lastModified());
				
				map.put("file", fileMap);
				list.add(map);
			}
		}
		
		return list;
	}
	
	@PostMapping(path = "/move-file")
	public ResponseEntity<Object> moveFile(@RequestParam("oldPath") String oldPath, @RequestParam("newPath") String newPath) {
		try {
			Files.move(Paths.get(oldPath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
			return new ResponseEntity<>(HttpStatus.valueOf(202));
		} catch (Exception e) {
			// TODO: handle exception	
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(404));
		}
	}
	
	@PostMapping(path = "/copy-file")
	public ResponseEntity<Object> copyFile(@RequestParam("oldPath") String oldPath, @RequestParam("newPath") String newPath) {
		try {
			Files.copy(Paths.get(oldPath), Paths.get(newPath));
			return new ResponseEntity<>(HttpStatus.valueOf(202));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(404));
		}
	}
	

	@GetMapping(path = "/get")
	public Response<Users> getUsers() {

		ArrayList<Users> list = new ArrayList<Users>();
		repository.findAll().forEach((user) -> list.add(user));

		Date date = new Date();
		return new Response<Users>(101, list.size() + " " + TAG + "s Fetched Successfully at " + date, list);

	}

	@GetMapping(path = "/get/{id}")
	public Response<Users> getUserById(@PathVariable("id") Integer id) {

		ArrayList<Users> list = new ArrayList<Users>();
		Users user = repository.findById(id).get();
		list.add(user);

		Date date = new Date();
		return new Response<Users>(101, TAG + " Fetched Successfully at " + date, list);

	}

	@PostMapping(path= "/update")
	public Response<Users> updateUser(@RequestParam Integer userId, @RequestParam String email, @RequestParam String password,
			@RequestParam String fullName, @RequestParam int access, @RequestParam int status, @RequestParam String secretKey) {
		
		Date date = new Date();
		
		Users user = new Users(userId, email, password, fullName, secretKey, access, status, date);
		repository.save(user);
		
		return new Response<Users>(101, TAG+" Updated Successfully at "+date, new ArrayList<Users>() {{
			add(user);
		}});
		
	}

	@GetMapping(path = "/delete/{id}")
	public Response<Users> deleteUser(@PathVariable("id") Integer id) {

		Users user = new Users();
		user.setUserId(id);
		repository.delete(user);

		Date date = new Date();
		return new Response<Users>(101, TAG + " Deleted Successfully at " + date, null);

	}

}
