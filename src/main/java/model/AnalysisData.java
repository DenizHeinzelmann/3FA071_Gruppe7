package model;

public class AnalysisData {
    private String meterType;
    private String periodLabel; // z.B. "2024-01" oder "2020"
    private double avgValue;

    public AnalysisData() {}

    public AnalysisData(String meterType, String periodLabel, double avgValue) {
        this.meterType = meterType;
        this.periodLabel = periodLabel;
        this.avgValue = avgValue;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public double getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(double avgValue) {
        this.avgValue = avgValue;
    }
}
