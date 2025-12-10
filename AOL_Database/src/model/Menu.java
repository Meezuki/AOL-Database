package model;

public class Menu {
    private String kodeMenu;
    private String namaMenu;
    private int harga;
    private String idKategori;

    public Menu(String kodeMenu, String namaMenu, int harga, String idKategori) {
        this.kodeMenu = kodeMenu;
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.idKategori = idKategori;
    }

    // Getter methods
    public String getKodeMenu() { return kodeMenu; }
    public String getNamaMenu() { return namaMenu; }
    public int getHarga() { return harga; }
    public String getIdKategori() { return idKategori; }
    
    @Override
    public String toString() {
        return namaMenu + "\nRp " + harga;
    }
}