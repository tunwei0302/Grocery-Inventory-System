
public class Address {

    private String addressLine1;
    private String city;
    private int postalCode;

    public Address(String addressLine1, String city, int postalCode) {
        this.addressLine1 = addressLine1;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getCity() {
        return city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setAddress(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return addressLine1 + ", " + city + ", " + postalCode;
    }

}
