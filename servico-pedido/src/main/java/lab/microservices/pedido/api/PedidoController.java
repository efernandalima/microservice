package lab.microservices.pedido.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lab.microservices.pedido.api.dto.PedidoRequest;
import lab.microservices.pedido.api.dto.PedidoResponse;
import lab.microservices.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de pedidos
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido no sistema")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo ID")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable("id") Long id) {
        PedidoResponse response = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Retorna lista de todos os pedidos")
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        List<PedidoResponse> response = pedidoService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente")
    public ResponseEntity<PedidoResponse> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pedido", description = "Remove um pedido do sistema")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        pedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
