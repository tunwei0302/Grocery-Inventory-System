
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

public abstract class Person {

    private String name;
    private String email;

    public Person(){
        
    }
    
    protected Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" + "name=" + name + ", email=" + email + '}';
    }
    
    public abstract String encryption (String line)throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException;
    public abstract String decryption(String line)throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException;
    
}
