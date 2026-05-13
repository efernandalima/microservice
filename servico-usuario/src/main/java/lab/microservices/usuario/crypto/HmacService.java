package lab.microservices.usuario.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Produz HMAC-SHA256 determinístico para permitir buscas de unicidade em campos
 * criptografados com AES-GCM (que usa IV aleatório e portanto não é pesquisável).
 */
@Service
public class HmacService {

    private final SecretKeySpec hmacKey;

    public HmacService(@Value("${encryption.key:VGhpcyBpcyBhIDMyIGJ5dGUga2V5IGZvciBBRVMyNTY=}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.hmacKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String hash(String value) {
        if (value == null) return null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] result = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular hash HMAC", e);
        }
    }
}
