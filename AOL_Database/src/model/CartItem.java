package model;

public class CartItem {
    private String nama;
    private int qty;
    private int price;
    private int total;
    // Nanti kita butuh kodeMenu untuk insert ke DB
    private String kodeMenu; 

    public CartItem(String kodeMenu, String nama, int qty, int price) {
        this.kodeMenu = kodeMenu;
        this.nama = nama;
        this.qty = qty;
        this.price = price;
        this.total = qty * price;
    }

    public String getNama() { return nama; }
    public int getQty() { return qty; }
    public int getPrice() { return price; }
    public int getTotal() { return total; }
    public String getKodeMenu() { return kodeMenu; }

    public void addQty(int n) { 
        this.qty += n; 
        this.total = this.qty * this.price; 
    }
}