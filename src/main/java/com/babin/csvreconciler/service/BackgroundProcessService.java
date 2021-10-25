package com.babin.csvreconciler.service;

import com.babin.csvreconciler.dao.BackgroundProcessDao;
import com.babin.csvreconciler.model.BackgroundProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BackgroundProcessService {

    @Autowired
    BackgroundProcessDao backgroundProcessDao;

    public BackgroundProcess createBackgroundProcess(BackgroundProcess backgroundProcess) {
        return backgroundProcessDao.save(backgroundProcess);
    }

    public BackgroundProcess createBackgroundProcess(String processName, String message) {
        BackgroundProcess backgroundProcess = new BackgroundProcess();
        backgroundProcess.setStatus("Started");
        backgroundProcess.setStartTime(new Date());
        backgroundProcess.setProcessName(processName);
        backgroundProcess.setMessage(message);

        return backgroundProcessDao.save(backgroundProcess);
    }

    public Optional<BackgroundProcess> getBackgroundProcessById(long id) {
        return backgroundProcessDao.findById(id);
    }

    public BackgroundProcess updateDetails(BackgroundProcess process) {
        return backgroundProcessDao.save(process);
    }
}
