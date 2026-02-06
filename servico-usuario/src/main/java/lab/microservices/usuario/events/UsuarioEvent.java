package lab.microservices.usuario.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Evento de usuário armazenado no MongoDB
 */
@Document(collection = "usuarioEvents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEvent {

    @Id
    private String id;

    private String eventType; // CREATED, UPDATED, DELETED

    private Long usuarioId;

    private String nome;

    private String cpf;

    private String email;

    private String telefone;

    private LocalDateTime timestamp;

    private String performedBy;
}
