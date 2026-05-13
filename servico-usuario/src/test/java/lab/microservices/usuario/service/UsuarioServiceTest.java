package lab.microservices.usuario.service;

import lab.microservices.usuario.api.dto.UsuarioRequest;
import lab.microservices.usuario.api.dto.UsuarioResponse;
import lab.microservices.usuario.api.exception.NotFoundException;
import lab.microservices.usuario.crypto.HmacService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioEventPublisher eventPublisher;

    @Mock
    private HmacService hmacService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest validRequest;
    private Usuario usuario;

    private static final String CPF_HASH   = "hash-cpf-teste";
    private static final String EMAIL_HASH = "hash-email-teste";

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
        usuario.setCpfHash(CPF_HASH);
        usuario.setEmailHash(EMAIL_HASH);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        when(hmacService.hash(validRequest.getCpf())).thenReturn(CPF_HASH);
        when(hmacService.hash(validRequest.getEmail())).thenReturn(EMAIL_HASH);
        when(usuarioRepository.existsByCpfHash(CPF_HASH)).thenReturn(false);
        when(usuarioRepository.existsByEmailHash(EMAIL_HASH)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.criar(validRequest);

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
        when(hmacService.hash(validRequest.getCpf())).thenReturn(CPF_HASH);
        when(hmacService.hash(validRequest.getEmail())).thenReturn(EMAIL_HASH);
        when(usuarioRepository.existsByCpfHash(CPF_HASH)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(hmacService.hash(validRequest.getCpf())).thenReturn(CPF_HASH);
        when(hmacService.hash(validRequest.getEmail())).thenReturn(EMAIL_HASH);
        when(usuarioRepository.existsByCpfHash(CPF_HASH)).thenReturn(false);
        when(usuarioRepository.existsByEmailHash(EMAIL_HASH)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João da Silva");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não encontrado")
    void deveLancarNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Santos");
        usuario2.setCpf("98765432101");
        usuario2.setEmail("maria@example.com");
        usuario2.setTelefone("11999998888");
        usuario2.setCpfHash("hash-cpf-2");
        usuario2.setEmailHash("hash-email-2");
        usuario2.setCriadoEm(LocalDateTime.now());
        usuario2.setAtualizadoEm(LocalDateTime.now());

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        List<UsuarioResponse> usuarios = usuarioService.listarTodos();

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios.get(0).getNome()).isEqualTo("João da Silva");
        assertThat(usuarios.get(1).getNome()).isEqualTo("Maria Santos");
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(hmacService.hash(anyString())).thenReturn(CPF_HASH, EMAIL_HASH);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioRequest updateRequest = new UsuarioRequest();
        updateRequest.setNome("João Atualizado");
        updateRequest.setCpf("12345678901");
        updateRequest.setEmail("joao@example.com");
        updateRequest.setTelefone("11999999999");

        UsuarioResponse response = usuarioService.atualizar(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
        verify(eventPublisher).publishUpdated(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.deletar(1L);

        verify(usuarioRepository).deleteById(1L);
        verify(eventPublisher).publishDeleted(1L);
    }
}
