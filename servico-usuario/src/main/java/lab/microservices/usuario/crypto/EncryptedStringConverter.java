package lab.microservices.usuario.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversor JPA para criptografar/descriptografar automaticamente campos de String
 */
@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static AesEncryptor aesEncryptor;

    @Autowired
    public void setAesEncryptor(AesEncryptor encryptor) {
        EncryptedStringConverter.aesEncryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (aesEncryptor == null) {
            throw new IllegalStateException("AesEncryptor não foi inicializado");
        }
        return aesEncryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (aesEncryptor == null) {
            throw new IllegalStateException("AesEncryptor não foi inicializado");
        }
        return aesEncryptor.decrypt(dbData);
    }
}
