package lab.microservices.pedido.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lab.microservices.pedido.domain.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para requisição de criação/atualização de pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criar ou atualizar um pedido")
public class PedidoRequest {

    @NotNull(message = "ID do usuário é obrigatório")
    @Schema(description = "ID do usuário que fez o pedido", example = "1")
    private Long usuarioId;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Schema(description = "Nome do produto", example = "Notebook Dell")
    private String nomeProduto;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @Schema(description = "Quantidade de produtos", example = "2")
    private Integer quantidade;

    @NotNull(message = "Preço total é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço total deve ser maior que zero")
    @Schema(description = "Preço total do pedido", example = "2500.00")
    private BigDecimal precoTotal;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status do pedido", example = "PENDENTE")
    private StatusPedido status;
}
