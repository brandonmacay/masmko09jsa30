package maceda.alejandro.verificadoronline;

public class Bussiness {
    private String name;
    private String country;
    private String state;
    private String link;
    private String image;
    private String address;

    public Bussiness() {
    }

    public Bussiness(String name, String country, String state, String link, String image, String address) {
        this.name = name;
        this.country = country;
        this.state = state;
        this.link = link;
        this.image = image;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getState() {
        return state;

    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
