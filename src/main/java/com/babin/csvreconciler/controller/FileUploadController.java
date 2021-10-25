package com.babin.csvreconciler.controller;

import com.babin.csvreconciler.constants.BackgroundProccessStatus;
import com.babin.csvreconciler.model.BackgroundProcess;
import com.babin.csvreconciler.service.BackgroundProcessService;
import com.babin.csvreconciler.service.QwoUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/qwo-management")
public class FileUploadController {

    @Autowired
    private QwoUploaderService qwoUploaderService;

    @Autowired
    private BackgroundProcessService backgroundProcessService;

    @GetMapping("/")
    public String index() {
        return "Welcome to the Qwo Manager";
    }

    @PostMapping(value = "/upload-qwo-csv-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadCSVFile(@RequestPart("file") MultipartFile file) {

        // validate file
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload valid file");
        } else {
            BackgroundProcess process = backgroundProcessService.createBackgroundProcess("QwoUpload", "The file has been uploaded and will be processed in the background and status will be notified. Find in the header information for more details");

            qwoUploaderService.processAndStoreQwoCSvFile(file, process);

            Map<String, String> response = new HashMap<>();
            response.put("location", "/api/qwo-management/status/"+process.getId());
            response.put("id", String.valueOf(process.getId()));
            response.put("name", process.getProcessName());
            response.put("message", process.getMessage());
            response.put("start-time", process.getStartTime().toString());
            response.put("status", process.getStatus().toString());
            response.put("retry-after", String.valueOf(120));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAll(response);

            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(httpHeaders).body(response);

        }
    }

    @PostMapping("/status/{process-id}")
    public ResponseEntity<Object> uploadCSVFile(@PathVariable("process-id") String processId) {

        try {
            BackgroundProcess process = backgroundProcessService.getBackgroundProcessById(Long.valueOf(processId))
                    .orElseThrow(() -> new IllegalArgumentException("No valid record found for the given details"));


            Map<String, String> response = new HashMap<>();
            response.put("id", String.valueOf(process.getId()));
            response.put("name", process.getProcessName());
            response.put("message", process.getMessage());
            response.put("start-time", process.getStartTime().toString());
            response.put("status", process.getStatus().toString());
            response.put("additional-info", process.getAdditionInfo());

            HttpStatus status;
            switch (process.getStatus()) {
                case "Success" :
                    status = HttpStatus.CREATED;
                    response.put("end-time", process.getEndTime().toString());
                    break;
                case "Fail" :
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    response.put("end-time", process.getEndTime().toString());
                    break;
                default:
                    status = HttpStatus.ACCEPTED;
                    response.put("location", "/api/qwo-management/status/" + process.getId());
                    response.put("retry-after", String.valueOf(120));
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAll(response);

            return ResponseEntity.status(status).headers(httpHeaders).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }
}