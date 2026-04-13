package lab.microservices.pedido.service;

import lab.microservices.pedido.api.dto.PedidoRequest;
import lab.microservices.pedido.api.dto.PedidoResponse;
import lab.microservices.pedido.client.UsuarioClient;
import lab.microservices.pedido.domain.Pedido;
import lab.microservices.pedido.events.PedidoEventPublisher;
import lab.microservices.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de negócio para gerenciamento de pedidos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher eventPublisher;
    private final UsuarioClient usuarioClient;

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        log.info("Criando novo pedido para usuário ID: {}", request.getUsuarioId());

        // Validação de quantidade
        if (request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Validação de usuário via OpenFeign (comunicação entre microsserviços)
        if (request.getUsuarioId() == null || request.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido");
        }

        try {
            Boolean existe = usuarioClient.usuarioExiste(request.getUsuarioId());
            if (Boolean.FALSE.equals(existe)) {
                throw new IllegalArgumentException(
                    "Usuário com ID " + request.getUsuarioId() + " não encontrado. Não é possível criar o pedido.");
            }
        } catch (IllegalArgumentException e) {
            throw e; // Re-lança validações de negócio
        } catch (Exception e) {
            log.error("Falha ao consultar servico-usuario para ID {}: {}", request.getUsuarioId(), e.getMessage());
            throw new IllegalStateException(
                "Não foi possível validar o usuário. O serviço de usuários está indisponível.");
        }

        // Cria entidade
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(request.getUsuarioId());
        pedido.setNomeProduto(request.getNomeProduto());
        pedido.setQuantidade(request.getQuantidade());
        pedido.setPrecoTotal(request.getPrecoTotal());
        pedido.setStatus(request.getStatus());

        // Salva
        pedido = pedidoRepository.save(pedido);

        // Publica evento
        eventPublisher.publishCreated(pedido);

        log.info("Pedido criado com ID: {}", pedido.getId());
        return toResponse(pedido);
    }

    @Cacheable(value = "pedidos", key = "#id")
    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        log.info("Listando todos os pedidos");
        return pedidoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "pedidos", key = "#id")
    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest request) {
        log.info("Atualizando pedido ID: {}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        // Validação de quantidade
        if (request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Atualiza
        pedido.setUsuarioId(request.getUsuarioId());
        pedido.setNomeProduto(request.getNomeProduto());
        pedido.setQuantidade(request.getQuantidade());
        pedido.setPrecoTotal(request.getPrecoTotal());
        pedido.setStatus(request.getStatus());

        pedido = pedidoRepository.save(pedido);

        // Publica evento
        eventPublisher.publishUpdated(pedido);

        log.info("Pedido atualizado: {}", id);
        return toResponse(pedido);
    }

    @CacheEvict(value = "pedidos", key = "#id")
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando pedido ID: {}", id);

        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        pedidoRepository.deleteById(id);

        // Publica evento
        eventPublisher.publishDeleted(id);

        log.info("Pedido deletado: {}", id);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setUsuarioId(pedido.getUsuarioId());
        response.setNomeProduto(pedido.getNomeProduto());
        response.setQuantidade(pedido.getQuantidade());
        response.setPrecoTotal(pedido.getPrecoTotal());
        response.setStatus(pedido.getStatus());
        response.setCriadoEm(pedido.getCriadoEm());
        response.setAtualizadoEm(pedido.getAtualizadoEm());
        return response;
    }
}
