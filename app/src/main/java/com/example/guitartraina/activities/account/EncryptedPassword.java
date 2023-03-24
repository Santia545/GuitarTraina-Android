package com.example.guitartraina.activities.account;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptedPassword extends Password {
    private final String salt;

    public EncryptedPassword() {
        super();
        salt = "";
    }



    /**
     * checa si la contraseña es valida
     *
     * @return regresa true siempre y cuando se haya usado un constructor con parametros
     */
    @Override
    public boolean isValid() {
        return !this.salt.equals("");
    }

    public EncryptedPassword(String password) {
        super();
        this.salt=BCrypt.gensalt();//overload with 12 rounds
        setPassword(BCrypt.hashpw(password, salt));
    }
    public EncryptedPassword(Password password) {
        this.salt=BCrypt.gensalt();//overload with 12 for slow hashing
        setPassword(BCrypt.hashpw(password.getPassword(), salt));
    }
    public boolean comparePassword(String password) {
        return BCrypt.checkpw(password, getPassword());
    }
    @Override
    public boolean equals(Object object) {
        // si el objeto se compara con el mismo se regresa true
        if (object == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(object instanceof EncryptedPassword)) {
            return false;
        }
        // Castear el objeto para poder comparar los miembros de la clase
        EncryptedPassword encryptedPassword = (EncryptedPassword) object;
        // Comparar los miembros
        return encryptedPassword.getPassword().equals(this.getPassword());
    }
}
