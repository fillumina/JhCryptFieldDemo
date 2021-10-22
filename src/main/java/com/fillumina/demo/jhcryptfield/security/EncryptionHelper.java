package com.fillumina.demo.jhcryptfield.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fillumina.demo.jhcryptfield.config.ApplicationProperties;
import com.fillumina.demo.jhcryptfield.security.EncryptionHelper.IdSettable;
import com.fillumina.demo.jhcryptfield.security.EncryptionUtils.EncryptionException;
import javax.crypto.SecretKey;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EncryptionHelper<T extends IdSettable> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static interface IdSettable {
        void setId(Long id);
    }

    public static interface EncryptionSecret {
        String getPassword();
        String getSalt();
    }

    private final Class<T> objectClass;
    private SecretKey secretKey;

    public EncryptionHelper(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Lazy loading to allow properties to be loaded
     */
    private SecretKey getSecretKey() {
        if (this.secretKey == null) {
            EncryptionSecret secret = ApplicationProperties.FIELD_ENCRYPTION_SECRET;
            final char[] password = secret.getPassword().toCharArray();
            final byte[] salt = secret.getSalt().getBytes();
            this.secretKey = EncryptionUtils.getAESKeyFromPassword(password, salt);
        }
        return secretKey;
    }

    public T decryptObject(Long id, T actualObject, String encryptedJson) {
        if (actualObject == null && encryptedJson != null && !encryptedJson.isBlank()) {
            try {
                String decryptedJson = decryptHexString(encryptedJson);
                actualObject = OBJECT_MAPPER.readValue(decryptedJson, objectClass);
                actualObject.setId(id);
            } catch (JsonProcessingException | EncryptionException ex) {
                actualObject = null;
            }
        }
        return actualObject;
    }

    public String encryptObject(T actualObject) {
        if (actualObject == null) {
            return null;
        }
        try {
            String plainJson = OBJECT_MAPPER.writeValueAsString(actualObject);
            String encryptedJson = encryptToHexString(plainJson);
            return encryptedJson;
        } catch (JsonProcessingException | EncryptionException ex) {
            throw new RuntimeException("Customer.setAddress()", ex);
        }
    }

    public String encryptToHexString(String text) {
        byte[] iv = EncryptionUtils.getRandomNonce(EncryptionUtils.IV_LENGTH_BYTE);
        byte[] bytes = text.getBytes(UTF_8);
        byte[] encryptedText = EncryptionUtils.encryptWithPrefixIV(bytes, getSecretKey(), iv);
        return EncryptionUtils.hex(encryptedText);
    }

    public String decryptHexString(String cypher) {
        try {
            byte[] encryptedBytes = EncryptionUtils.decodeHexString(cypher);
            String decryptedText = EncryptionUtils.decryptWithPrefixIV(encryptedBytes, getSecretKey());
            return decryptedText;
        } catch (IllegalArgumentException ex) {
            throw new EncryptionException(ex);
        }
    }
}
