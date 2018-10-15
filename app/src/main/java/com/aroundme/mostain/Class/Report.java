package com.aroundme.mostain.Class;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Report {
    private String reporter;
    private String reported;
    private String reporterUid;
    private String reportedUid;
    private String reportMessage;
    private long reportTime;

    public Report() {
    }

    // Text Message
    public Report(String reporter, String reported, String reporterUid, String reportedUid, String reportMessage, long reportTime) {

        this.reporter = reporter;
        this.reported = reported;
        this.reporterUid = reporterUid;
        this.reportedUid = reportedUid;
        this.reportMessage = reportMessage;
        this.reportTime = reportTime;

    }


    public String getReporterUid() {
        return reporterUid;
    }

    public void setReporterUid(String reporterUid) {
        this.reporterUid = reporterUid;
    }

    public String getReportedUid() {
        return reportedUid;
    }

    public void setReportedUid(String reportedUid) {
        this.reportedUid = reportedUid;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }
}
