package maceda.alejandro.verificadoronline;



public class productList {
    private String ean;
    private String name;
    private String price;
    private String link;
    private String image;
    private String currency;

    public productList() {
    }

    public productList(String ean, String name, String price, String link, String image, String currency) {

        this.ean = ean;
        this.name = name;
        this.price = price;
        this.link = link;
        this.image = image;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getEan() {
        return ean;
    }
    public void setEan(String ean) {
        this.ean= ean;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getPrice() {
        return price;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCurrency(String pcurrency) {
        this.currency = currency;
    }
    public String getCurrency() {
        return currency;
    }
}
