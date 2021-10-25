package com.babin.csvreconciler.model;

import com.babin.csvreconciler.constants.BackgroundProccessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class BackgroundProcess {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "PROCESS_NAME")
    private String processName;

    @Column(name = "START_TIME")
    private Date startTime;

    @Column(name = "END_TIME")
    private Date endTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "ADDITIONAL_INFO")
    private String additionInfo;
}
