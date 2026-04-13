package lab.microservices.pedido.service;

import lab.microservices.pedido.api.dto.PedidoRequest;
import lab.microservices.pedido.api.dto.PedidoResponse;
import lab.microservices.pedido.domain.Pedido;
import lab.microservices.pedido.domain.StatusPedido;
import lab.microservices.pedido.events.PedidoEventPublisher;
import lab.microservices.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - Testes Unitários")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEventPublisher eventPublisher;

    @Mock
    private lab.microservices.pedido.client.UsuarioClient usuarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequest validRequest;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        validRequest = new PedidoRequest();
        validRequest.setUsuarioId(1L);
        validRequest.setNomeProduto("Notebook Dell");
        validRequest.setQuantidade(2);
        validRequest.setPrecoTotal(new BigDecimal("2500.00"));
        validRequest.setStatus(StatusPedido.PENDENTE);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(1L);
        pedido.setNomeProduto("Notebook Dell");
        pedido.setQuantidade(2);
        pedido.setPrecoTotal(new BigDecimal("2500.00"));
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setCriadoEm(LocalDateTime.now());
        pedido.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        // Given
        when(usuarioClient.usuarioExiste(1L)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // When
        PedidoResponse response = pedidoService.criar(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNomeProduto()).isEqualTo("Notebook Dell");
        assertThat(response.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(eventPublisher).publishCreated(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponse response = pedidoService.buscarPorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNomeProduto()).isEqualTo("Notebook Dell");
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não encontrado")
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Given
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> pedidoService.buscarPorId(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pedido não encontrado");
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodosOsPedidos() {
        // Given
        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setUsuarioId(2L);
        pedido2.setNomeProduto("Mouse Gamer");
        pedido2.setQuantidade(1);
        pedido2.setPrecoTotal(new BigDecimal("150.00"));
        pedido2.setStatus(StatusPedido.CONFIRMADO);
        pedido2.setCriadoEm(LocalDateTime.now());
        pedido2.setAtualizadoEm(LocalDateTime.now());

        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido, pedido2));

        // When
        List<PedidoResponse> pedidos = pedidoService.listarTodos();

        // Then
        assertThat(pedidos).hasSize(2);
        assertThat(pedidos.get(0).getNomeProduto()).isEqualTo("Notebook Dell");
        assertThat(pedidos.get(1).getNomeProduto()).isEqualTo("Mouse Gamer");
    }

    @Test
    @DisplayName("Deve atualizar pedido com sucesso")
    void deveAtualizarPedidoComSucesso() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoRequest updateRequest = new PedidoRequest();
        updateRequest.setUsuarioId(1L);
        updateRequest.setNomeProduto("Notebook Dell XPS");
        updateRequest.setQuantidade(3);
        updateRequest.setPrecoTotal(new BigDecimal("3500.00"));
        updateRequest.setStatus(StatusPedido.CONFIRMADO);

        // When
        PedidoResponse response = pedidoService.atualizar(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(pedidoRepository).save(any(Pedido.class));
        verify(eventPublisher).publishUpdated(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve deletar pedido com sucesso")
    void deveDeletarPedidoComSucesso() {
        // Given
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        // When
        pedidoService.deletar(1L);

        // Then
        verify(pedidoRepository).deleteById(1L);
        verify(eventPublisher).publishDeleted(1L);
    }
}
