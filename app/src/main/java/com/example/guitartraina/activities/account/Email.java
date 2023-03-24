package com.example.guitartraina.activities.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {
    private String email;

    public Email() {
        this.email = "";
    }

    public Email(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValid() {
        Pattern regularExpresion = Pattern.compile("^(?<username>[\\w-.]+)@(?<domainame>[\\w-]+)\\.(?<topleveldomain>[\\w-]{2,4})(?<subleveldomain>\\.[\\w-]{2,4})?$");
        Matcher matcher = regularExpresion.matcher(this.email);
        return matcher.find();
    }

    @Override
    public boolean equals(Object object) {
        // si el objeto se compara con el mismo se regresa true
        if (object == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(object instanceof Email)) {
            return false;
        }
        // Castear el objeto para poder comparar los miembros de la clase
        Email email = (Email) object;
        // Comparar los miembros
        return email.email.equals(this.email);
    }
}
