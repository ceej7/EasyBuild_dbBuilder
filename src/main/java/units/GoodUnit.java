package units;

public class GoodUnit {
    private long id;
    public String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public float getOprice() {
        return oprice;
    }

    public void setOprice(float oprice) {
        this.oprice = oprice;
    }

    public float getMprice() {
        return mprice;
    }

    public void setMprice(float mprice) {
        this.mprice = mprice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "[id:"+id+" title:"+title+" imageURL:"+img+" oPrice:"+oprice+" mPrice:"+mprice+" price:"+price+"]";
    }

    public String img;
    public float oprice;
    public float mprice;
    public float price;
}
