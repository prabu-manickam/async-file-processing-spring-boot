package com.babin.csvreconciler.service;

import com.babin.csvreconciler.constants.BackgroundProccessStatus;
import com.babin.csvreconciler.dao.QwoRecordDao;
import com.babin.csvreconciler.model.BackgroundProcess;
import com.babin.csvreconciler.model.QwoRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class QwoUploaderService {

    @Autowired
    QwoRecordDao qwoRecordDao;

    @Autowired
    BackgroundProcessService backgroundProcessService;

    @Async
    public void processAndStoreQwoCSvFile(MultipartFile file, BackgroundProcess process) {

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<QwoRecord> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(QwoRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<QwoRecord> qwoRecords = csvToBean.parse();

            AtomicInteger newRecords = new AtomicInteger();

            ForkJoinPool customThreadPool = new ForkJoinPool(4);//keep up to the no.of CPU cores of the machine

            List<QwoRecord> updatedQwoRecords;

            try {
                updatedQwoRecords = customThreadPool.submit(() ->
                    qwoRecords.stream().parallel().map(qwoRecord -> {
                        QwoRecord dbQwoRecord = qwoRecordDao
                                .findOneByCustomerNumberIsAndProductReference(qwoRecord.getCustomerNumber(), qwoRecord.getProductReference())
                                .orElseGet(() -> {
                                    newRecords.getAndIncrement();
                                    return new QwoRecord();
                                });

                        BeanUtils.copyProperties(qwoRecord, dbQwoRecord, new String[]{"id"});
                        return dbQwoRecord;
                    }).collect(Collectors.toList())
                ).get();
            } finally {
                customThreadPool.shutdown();
            }

            qwoRecordDao.saveAll(updatedQwoRecords);

            process.setStatus("Success");
            process.setMessage("The file was processed successfully and all the records are updated");
            process.setAdditionInfo("There are "+newRecords.get()+" new records identified out of "+qwoRecords.size());


        } catch (Exception ex) {
            process.setStatus("Failed");
            process.setMessage("An error occurred while processing the CSV file.");
            process.setAdditionInfo("Error occurred, check logs for the detailed errors");
            ex.printStackTrace();
        }

        process.setEndTime(new Date());
        backgroundProcessService.updateDetails(process);
    }

}
