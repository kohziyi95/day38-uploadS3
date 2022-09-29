package com.vttp.day38server.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AmazonS3 s3;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postUpload(@RequestPart MultipartFile myfile,
            @RequestPart String title) throws IOException {
        Map<String, String> userData = new HashMap<>();
        userData.put("title", title);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(myfile.getContentType());
        metadata.setContentLength(myfile.getSize());
        metadata.setUserMetadata(userData);

        PutObjectRequest putReq = new PutObjectRequest("vttpbucket", "uploads/%s".formatted(myfile.getName()),
                myfile.getInputStream(), metadata);
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(putReq);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<byte[]> getFile() {
        try {
            GetObjectRequest getReq = new GetObjectRequest("vttpbucket", "uploads/myfile");
            S3Object result = s3.getObject(getReq);
            ObjectMetadata metadata = result.getObjectMetadata();
            Map<String, String> userData = metadata.getUserMetadata();
            try (S3ObjectInputStream is = result.getObjectContent()) {
                byte[] buffer = is.readAllBytes();
                return ResponseEntity.status(HttpStatus.OK).contentLength(metadata.getContentLength())
                        .contentType(MediaType.parseMediaType(metadata.getContentType()))
                        .header("X-name", userData.get("title")).body(buffer);
            }
        } catch (AmazonS3Exception ex) {

        } catch (Exception ex) {

        }
        return null;
    }
}
