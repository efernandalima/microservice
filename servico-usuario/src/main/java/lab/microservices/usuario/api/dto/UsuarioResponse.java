package lab.microservices.usuario.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de usuário (dados descriptografados)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de um usuário")
public class UsuarioResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "CPF do usuário", example = "12345678901")
    private String cpf;

    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @Schema(description = "Telefone do usuário", example = "11987654321")
    private String telefone;

    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data da última atualização", example = "2024-01-01T10:00:00")
    private LocalDateTime atualizadoEm;
}
