package model;

public class CategoryModel {
    private String id;
    private String nama;

    public CategoryModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    
    // Override toString agar saat tampil di dropdown cuma namanya (Opsional)
    @Override
    public String toString() { return id + " - " + nama; }
}