package lab.microservices.pedido.events;

import lab.microservices.pedido.domain.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publicador de eventos de pedido no MongoDB
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoEventPublisher {

    private final MongoTemplate mongoTemplate;

    public void publishCreated(Pedido pedido) {
        PedidoEvent event = new PedidoEvent();
        event.setEventType("CREATED");
        event.setPedidoId(pedido.getId());
        event.setUsuarioId(pedido.getUsuarioId());
        event.setNomeProduto(pedido.getNomeProduto());
        event.setQuantidade(pedido.getQuantidade());
        event.setPrecoTotal(pedido.getPrecoTotal());
        event.setStatus(pedido.getStatus());
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento CREATED publicado para pedido ID: {}", pedido.getId());
    }

    public void publishUpdated(Pedido pedido) {
        PedidoEvent event = new PedidoEvent();
        event.setEventType("UPDATED");
        event.setPedidoId(pedido.getId());
        event.setUsuarioId(pedido.getUsuarioId());
        event.setNomeProduto(pedido.getNomeProduto());
        event.setQuantidade(pedido.getQuantidade());
        event.setPrecoTotal(pedido.getPrecoTotal());
        event.setStatus(pedido.getStatus());
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento UPDATED publicado para pedido ID: {}", pedido.getId());
    }

    public void publishDeleted(Long pedidoId) {
        PedidoEvent event = new PedidoEvent();
        event.setEventType("DELETED");
        event.setPedidoId(pedidoId);
        event.setTimestamp(LocalDateTime.now());
        event.setPerformedBy("system");

        mongoTemplate.save(event);
        log.info("Evento DELETED publicado para pedido ID: {}", pedidoId);
    }
}
