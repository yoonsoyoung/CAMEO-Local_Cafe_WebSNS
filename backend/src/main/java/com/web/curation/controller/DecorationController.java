package com.web.curation.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;
import com.web.curation.model.BasicResponse;
import com.web.curation.model.decoration.Decoration;
import com.web.curation.service.DecorationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
		@ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
		@ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
		@ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/decoration")
public class DecorationController {
	
	@Autowired
	private DecorationService service;
	
	public static final Logger logger = LoggerFactory.getLogger(DecorationController.class);
	
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	// ???????????? ????????????
	@ApiOperation(value = "?????? ?????? ??????", notes = "???????????? ????????? ?????? ????????? ????????????", response = Map.class)
	@RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Map<String, Object>> insertDecoration(@RequestPart("file") MultipartFile[] files){
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		
		try {
//			Decoration decoration = service.getJson(decoraitonstr);
			int cntPic = 0;
			
			
			cntPic = service.insertDecoration(files);
			System.out.println("cntpic :" + cntPic);
			
			
			if(cntPic != 0) {
				resultMap.put("message", SUCCESS);
				status = HttpStatus.ACCEPTED;
			}else {
				resultMap.put("message", FAIL);
				status = HttpStatus.ACCEPTED;
			}
		} catch (DataAccessException e) {
			// TODO: handle exception
			resultMap.put("message", "DB ??????");
			status = HttpStatus.FAILED_DEPENDENCY;
		} catch(Exception e) {
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap, status);
		
	}
	
	// ??????????????????
	// user_id??? ????????? 
	// user_total_score???
	// ????????? user_total_score??? ?????? ????????? ????????????

	
//	@ApiOperation(value = "?????? ?????? ????????????", notes = "user_id??? ????????? ??????????????? ????????? ??? ??????")
//	@GetMapping(value = "/read",
//			// ?????? ????????? ?????? ??????
//			produces = {MediaType.IMAGE_JPEG_VALUE}
//			
//			)
//	public ResponseEntity<byte[]> readDecoration(@RequestParam String user_id){
//		Map<String, Object> resultMap = new HashMap<>();
//		byte[] imageByteArray = null;
//		HttpStatus status = null;
//		
//		try {
//			int score = service.selectScore(user_id);
//			
//			String absolutePath =
//		        	new File("").getAbsolutePath() + File.separator + File.separator;
//			String path;
//			
//			Decoration pic = service.selectPic(score);
//			
//			System.out.println(pic.getSaveName());
//			
//			path = "C:\\decoration\\upload\\" + pic.getSaveName();
//			InputStream imageStream = new FileInputStream(path);
//			imageByteArray = IOUtils.toByteArray(imageStream);
//			imageStream.close();
//			
//			if(score > 0) {
//				resultMap.put("message", SUCCESS);
//				resultMap.put("score", score);
//				resultMap.put("pic", imageByteArray);
//				status = HttpStatus.ACCEPTED;
//			}else {
//				resultMap.put("message", FAIL);
//				status = HttpStatus.ACCEPTED;
//				
//			}
//		} catch (Exception e) {
//			resultMap.put("message",  e.getMessage());
//			status = HttpStatus.INTERNAL_SERVER_ERROR;
//		}
//		
//		return new ResponseEntity<byte[]>(imageByteArray, status);
//		
//	}
	// ??????????????? ???????????? ????????????????????? ??????????????? savename??????????????????
	@ApiOperation(value = "?????? ??????", notes = "user_id??? ????????? savename??? ????????? ??? ??????")
	@GetMapping("/score/{user_id}")
	public ResponseEntity<Map<String,Object>> decoScore(@PathVariable String user_id) throws Exception{
		Map<String,Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		try {
			//?????????????????????
			int score = service.selectScore(user_id);
			
			Decoration pic = service.selectPic(score);
			
			if(score>=0) {
				resultMap.put("message", SUCCESS);
				resultMap.put("score", score);
				resultMap.put("savename", pic.getSaveName());
				status = HttpStatus.ACCEPTED;
			}else {
				resultMap.put("message", FAIL);
				status = HttpStatus.ACCEPTED;
			}
		} catch (Exception e) {
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			// TODO: handle exception
		}
			return new ResponseEntity<Map<String,Object>>(resultMap,status);
	}
	
	
	@GetMapping("/read/{fileName}")
	@ApiOperation(value = "????????????", notes = "fileName??? savename??????????????????")
    public Object bFile(@PathVariable String fileName) throws MalformedURLException{
		Resource resource = new FileSystemResource(
				"/home/ubuntu/S05P12C202/frontend/src/assets/images/decoration/" + fileName);

        if(fileName == null) {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return ResponseEntity.ok()
        		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename() + "")
				.body(resource);
    }
	
	

	


}
