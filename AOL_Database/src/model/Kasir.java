package model;

public class Kasir {
    private String idKasir;
    private String namaKasir;
    private String password;

    public Kasir(String idKasir, String namaKasir, String password) {
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        this.password = password;
    }

    public String getIdKasir() { return idKasir; }
    public String getNamaKasir() { return namaKasir; }
}