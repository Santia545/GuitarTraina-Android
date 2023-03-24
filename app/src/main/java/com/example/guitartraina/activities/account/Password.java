package com.example.guitartraina.activities.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Password {
    private String password;

    public Password() {
        this.password="";
    }

    public Password(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() { //quiza agregar que aviente exepciones dependiendo de que le falte
        Pattern regularExpresion = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[._-])[A-Za-z\\d._-]{6,20}$");
        Matcher matcher = regularExpresion.matcher(this.password);
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
        if (!(object instanceof Password)) {
            return false;
        }
        // Castear el objeto para poder comparar los miembros de la clase
        Password password = (Password) object;
        // Comparar los miembros
        return password.password.equals(this.password);
    }
}
