package lab.microservices.usuario.domain;

import jakarta.persistence.*;
import lab.microservices.usuario.crypto.EncryptedStringConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade Usuario com campos criptografados
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 500)
    @Convert(converter = EncryptedStringConverter.class)
    private String cpf;

    @Column(nullable = false, unique = true, length = 500)
    @Convert(converter = EncryptedStringConverter.class)
    private String email;

    @Column(nullable = false, length = 500)
    @Convert(converter = EncryptedStringConverter.class)
    private String telefone;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
