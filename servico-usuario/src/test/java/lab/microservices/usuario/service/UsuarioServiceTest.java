package lab.microservices.usuario.service;

import lab.microservices.usuario.api.dto.UsuarioRequest;
import lab.microservices.usuario.api.dto.UsuarioResponse;
import lab.microservices.usuario.domain.Usuario;
import lab.microservices.usuario.events.UsuarioEventPublisher;
import lab.microservices.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioEventPublisher eventPublisher;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest validRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        validRequest = new UsuarioRequest();
        validRequest.setNome("João da Silva");
        validRequest.setCpf("12345678901");
        validRequest.setEmail("joao@example.com");
        validRequest.setTelefone("11987654321");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João da Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@example.com");
        usuario.setTelefone("11987654321");
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        // Given
        when(usuarioRepository.existsByCpf(validRequest.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        UsuarioResponse response = usuarioService.criar(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João da Silva");
        assertThat(response.getCpf()).isEqualTo("12345678901");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(eventPublisher).publishCreated(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        // Given
        when(usuarioRepository.existsByCpf(validRequest.getCpf())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> usuarioService.criar(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Given
        when(usuarioRepository.existsByCpf(validRequest.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> usuarioService.criar(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        UsuarioResponse response = usuarioService.buscarPorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João da Silva");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> usuarioService.buscarPorId(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        // Given
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Santos");
        usuario2.setCpf("98765432101");
        usuario2.setEmail("maria@example.com");
        usuario2.setTelefone("11999998888");
        usuario2.setCriadoEm(LocalDateTime.now());
        usuario2.setAtualizadoEm(LocalDateTime.now());

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        // When
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();

        // Then
        assertThat(usuarios).hasSize(2);
        assertThat(usuarios.get(0).getNome()).isEqualTo("João da Silva");
        assertThat(usuarios.get(1).getNome()).isEqualTo("Maria Santos");
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioRequest updateRequest = new UsuarioRequest();
        updateRequest.setNome("João Atualizado");
        updateRequest.setCpf("12345678901");
        updateRequest.setEmail("joao@example.com");
        updateRequest.setTelefone("11999999999");

        // When
        UsuarioResponse response = usuarioService.atualizar(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
        verify(eventPublisher).publishUpdated(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // When
        usuarioService.deletar(1L);

        // Then
        verify(usuarioRepository).deleteById(1L);
        verify(eventPublisher).publishDeleted(1L);
    }
}
