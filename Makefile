.PHONY: help build up down logs clean restart status

help:
	@echo "Comandos disponíveis:"
	@echo "  make build    - Compila todos os serviços"
	@echo "  make up       - Inicia toda a infraestrutura"
	@echo "  make down     - Para toda a infraestrutura"
	@echo "  make logs     - Visualiza logs de todos os serviços"
	@echo "  make clean    - Remove containers, volumes e imagens"
	@echo "  make restart  - Reinicia todos os serviços"
	@echo "  make status   - Mostra status de todos os containers"

build:
	@echo "Compilando projeto..."
	mvn clean package -DskipTests
	@echo "Build concluído!"

up:
	@echo "Iniciando infraestrutura..."
	cd infra && docker-compose up -d
	@echo "Aguardando serviços iniciarem..."
	@timeout /t 30 /nobreak
	@echo "Infraestrutura iniciada!"
	@echo ""
	@echo "Serviços disponíveis:"
	@echo "  - Serviço Usuário: http://localhost:8081"
	@echo "  - Serviço Pedidos: http://localhost:8082"
	@echo "  - Swagger Usuário: http://localhost:8081/swagger-ui.html"
	@echo "  - Swagger Pedidos: http://localhost:8082/swagger-ui.html"
	@echo "  - Kibana: http://localhost:5601"
	@echo "  - Jaeger: http://localhost:16686"

down:
	@echo "Parando infraestrutura..."
	cd infra && docker-compose down
	@echo "Infraestrutura parada!"

logs:
	cd infra && docker-compose logs -f

clean:
	@echo "Limpando containers, volumes e imagens..."
	cd infra && docker-compose down -v --rmi all
	mvn clean
	@echo "Limpeza concluída!"

restart:
	@echo "Reiniciando serviços..."
	cd infra && docker-compose restart
	@echo "Serviços reiniciados!"

status:
	@echo "Status dos containers:"
	cd infra && docker-compose ps
