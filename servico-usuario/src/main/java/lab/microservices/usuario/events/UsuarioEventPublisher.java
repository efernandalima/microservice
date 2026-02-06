package lab.microservices.usuario.events;

import lab.microservices.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publicador de eventos de usuário no MongoDB
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioEventPublisher {

    private final MongoTemplate mongoTemplate;

    public void publishCreated(Usuario usuario) {
        UsuarioEvent event = new UsuarioEvent();
        event.setEventType("CREATED");
        event.setUsuarioId(usuario.getId());
        event.setNome(usuario.getNome());
        event.setCpf(usuario.getCpf());
        event.setEmail(usuario.getEmail());
        event.setTelefone(usuario.getTelefone());
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento CREATED publicado para usuário ID: {}", usuario.getId());
    }

    public void publishUpdated(Usuario usuario) {
        UsuarioEvent event = new UsuarioEvent();
        event.setEventType("UPDATED");
        event.setUsuarioId(usuario.getId());
        event.setNome(usuario.getNome());
        event.setCpf(usuario.getCpf());
        event.setEmail(usuario.getEmail());
        event.setTelefone(usuario.getTelefone());
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento UPDATED publicado para usuário ID: {}", usuario.getId());
    }

    public void publishDeleted(Long usuarioId) {
        UsuarioEvent event = new UsuarioEvent();
        event.setEventType("DELETED");
        event.setUsuarioId(usuarioId);
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento DELETED publicado para usuário ID: {}", usuarioId);
    }
}
