package lab.microservices.usuario.service;

import lab.microservices.usuario.api.dto.UsuarioRequest;
import lab.microservices.usuario.api.dto.UsuarioResponse;
import lab.microservices.usuario.api.exception.NotFoundException;
import lab.microservices.usuario.crypto.HmacService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEventPublisher eventPublisher;
    private final HmacService hmacService;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        log.info("Criando novo usuário: {}", request.getNome());

        String cpfHash   = hmacService.hash(request.getCpf());
        String emailHash = hmacService.hash(request.getEmail());

        if (usuarioRepository.existsByCpfHash(cpfHash)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (usuarioRepository.existsByEmailHash(emailHash)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpfHash(cpfHash);
        usuario.setEmailHash(emailHash);

        usuario = usuarioRepository.save(usuario);
        eventPublisher.publishCreated(usuario);

        log.info("Usuário criado com ID: {}", usuario.getId());
        return toResponse(usuario);
    }

    @Cacheable(value = "usuarios", key = "#id")
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));
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
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));

        String cpfHash   = hmacService.hash(request.getCpf());
        String emailHash = hmacService.hash(request.getEmail());

        if (!usuario.getCpfHash().equals(cpfHash) &&
                usuarioRepository.existsByCpfHash(cpfHash)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (!usuario.getEmailHash().equals(emailHash) &&
                usuarioRepository.existsByEmailHash(emailHash)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpfHash(cpfHash);
        usuario.setEmailHash(emailHash);

        usuario = usuarioRepository.save(usuario);
        eventPublisher.publishUpdated(usuario);

        log.info("Usuário atualizado: {}", id);
        return toResponse(usuario);
    }

    @CacheEvict(value = "usuarios", key = "#id")
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando usuário ID: {}", id);

        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado: " + id);
        }

        usuarioRepository.deleteById(id);
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
