package lab.microservices.usuario.events;

import lab.microservices.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioEventPublisher {

    private final MongoTemplate mongoTemplate;

    public void publishCreated(Usuario usuario) {
        UsuarioEvent event = buildEvent("CREATED", usuario);
        mongoTemplate.save(event);
        log.info("Evento CREATED publicado para usuário ID: {}", usuario.getId());
    }

    public void publishUpdated(Usuario usuario) {
        UsuarioEvent event = buildEvent("UPDATED", usuario);
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

    private UsuarioEvent buildEvent(String type, Usuario usuario) {
        UsuarioEvent event = new UsuarioEvent();
        event.setEventType(type);
        event.setUsuarioId(usuario.getId());
        event.setNome(usuario.getNome());
        event.setCpf(mask(usuario.getCpf()));
        event.setEmail(mask(usuario.getEmail()));
        event.setTelefone(mask(usuario.getTelefone()));
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");
        return event;
    }

    /** Mantém apenas os 3 primeiros e últimos caracteres; oculta o resto. */
    private String mask(String value) {
        if (value == null || value.length() <= 6) return "***";
        return value.substring(0, 3) + "***" + value.substring(value.length() - 3);
    }
}
