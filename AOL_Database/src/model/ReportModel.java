package model;

public class ReportModel {
    private String label; // Bisa nama menu, no meja, nama kasir, dll
    private int value;    // Bisa jumlah terjual, total omzet, atau frekuensi

    public ReportModel(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public int getValue() { return value; }
}