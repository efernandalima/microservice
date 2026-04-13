package lab.microservices.pedido.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicação com o servico-usuario.
 *
 * Responsável por verificar se um usuário existe antes de criar um pedido,
 * garantindo integridade referencial entre os microsserviços de forma síncrona.
 *
 * A URL é configurável via propriedade 'services.usuario.url' no application.yml,
 * permitindo diferentes configurações para ambiente local e Docker.
 */
@FeignClient(name = "servico-usuario", url = "${services.usuario.url}")
public interface UsuarioClient {

    /**
     * Verifica se um usuário com o ID fornecido existe no servico-usuario.
     *
     * @param id ID do usuário a verificar
     * @return true se o usuário existe, false caso contrário
     */
    @GetMapping("/api/usuarios/{id}/existe")
    Boolean usuarioExiste(@PathVariable("id") Long id);
}
