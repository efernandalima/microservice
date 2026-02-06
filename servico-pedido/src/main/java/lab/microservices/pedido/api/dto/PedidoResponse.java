package lab.microservices.pedido.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lab.microservices.pedido.domain.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de um pedido")
public class PedidoResponse {

    @Schema(description = "ID do pedido", example = "1")
    private Long id;

    @Schema(description = "ID do usuário", example = "1")
    private Long usuarioId;

    @Schema(description = "Nome do produto", example = "Notebook Dell")
    private String nomeProduto;

    @Schema(description = "Quantidade", example = "2")
    private Integer quantidade;

    @Schema(description = "Preço total", example = "2500.00")
    private BigDecimal precoTotal;

    @Schema(description = "Status do pedido", example = "PENDENTE")
    private StatusPedido status;

    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data da última atualização", example = "2024-01-01T10:00:00")
    private LocalDateTime atualizadoEm;
}
