package lab.microservices.pedido.events;

import lab.microservices.pedido.domain.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de pedido armazenado no MongoDB
 */
@Document(collection = "pedidoEvents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEvent {

    @Id
    private String id;

    private String eventType; // CREATED, UPDATED, DELETED

    private Long pedidoId;

    private Long usuarioId;

    private String nomeProduto;

    private Integer quantidade;

    private BigDecimal precoTotal;

    private StatusPedido status;

    private LocalDateTime timestamp;

    private String performedBy;
}
