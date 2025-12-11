package model;

public class CustomerModel {
    private String idPelanggan;
    private String namaPelanggan;
    private String tipeMembership; // Gold, Silver, Bronze
    private String noTelp;

    // Constructor Lengkap
    public CustomerModel(String id, String nama, String tipe, String telp) {
        this.idPelanggan = id;
        this.namaPelanggan = nama;
        this.tipeMembership = tipe;
        this.noTelp = telp;
    }

    public String getIdPelanggan() { return idPelanggan; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public String getTipeMembership() { return tipeMembership; }
    public String getNoTelp() { return noTelp; }

    // Override toString agar Dropdown di Kasir tetap rapi (hanya nama)
    @Override
    public String toString() {
        return namaPelanggan + " (" + tipeMembership + ")";
    }
}