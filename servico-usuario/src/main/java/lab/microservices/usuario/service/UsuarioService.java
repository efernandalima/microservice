package lab.microservices.usuario.service;

import lab.microservices.usuario.api.dto.UsuarioRequest;
import lab.microservices.usuario.api.dto.UsuarioResponse;
import lab.microservices.usuario.domain.Usuario;
import lab.microservices.usuario.events.UsuarioEventPublisher;
import lab.microservices.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de negócio para gerenciamento de usuários
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEventPublisher eventPublisher;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        log.info("Criando novo usuário: {}", request.getNome());

        // Validações de unicidade
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Cria entidade
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());

        // Salva
        usuario = usuarioRepository.save(usuario);

        // Publica evento
        eventPublisher.publishCreated(usuario);

        log.info("Usuário criado com ID: {}", usuario.getId());
        return toResponse(usuario);
    }

    @Cacheable(value = "usuarios", key = "#id")
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "usuarios", key = "#id")
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Validações de unicidade (se mudou)
        if (!usuario.getCpf().equals(request.getCpf()) &&
                usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (!usuario.getEmail().equals(request.getEmail()) &&
                usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Atualiza
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());

        usuario = usuarioRepository.save(usuario);

        // Publica evento
        eventPublisher.publishUpdated(usuario);

        log.info("Usuário atualizado: {}", id);
        return toResponse(usuario);
    }

    @CacheEvict(value = "usuarios", key = "#id")
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando usuário ID: {}", id);

        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(id);

        // Publica evento
        eventPublisher.publishDeleted(id);

        log.info("Usuário deletado: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existe(Long id) {
        return usuarioRepository.existsById(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setCpf(usuario.getCpf());
        response.setEmail(usuario.getEmail());
        response.setTelefone(usuario.getTelefone());
        response.setCriadoEm(usuario.getCriadoEm());
        response.setAtualizadoEm(usuario.getAtualizadoEm());
        return response;
    }
}
