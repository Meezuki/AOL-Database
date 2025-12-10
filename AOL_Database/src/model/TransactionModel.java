package model;

public class TransactionModel {
    private String noNota;
    private String tanggal; 
    private String namaKasir;
    private int totalBelanja;

    public TransactionModel(String noNota, String tanggal, String namaKasir, int totalBelanja) {
        this.noNota = noNota;
        this.tanggal = tanggal;
        this.namaKasir = namaKasir;
        this.totalBelanja = totalBelanja;
    }

    public String getNoNota() { return noNota; }
    public String getTanggal() { return tanggal; }
    public String getNamaKasir() { return namaKasir; }
    public int getTotalBelanja() { return totalBelanja; }
}