package lab.microservices.usuario.repository;

import lab.microservices.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCpfHash(String cpfHash);

    boolean existsByEmailHash(String emailHash);

    Optional<Usuario> findByCpfHash(String cpfHash);

    Optional<Usuario> findByEmailHash(String emailHash);
}
