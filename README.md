🐾 Pokémon API

Este projeto é uma API REST desenvolvida com Java e Spring Boot que fornece informações detalhadas sobre Pokémon, incluindo habilidades, movimentos, tipos, estatísticas e evoluções. Também inclui uma interface web simples para interação com a API.

📦 Funcionalidades
Consulta de detalhes de Pokémon individuais.

Informações sobre habilidades, movimentos, tipos e estatísticas.

Dados sobre cadeia de evolução.

Interface web para visualização básica.

Integração com fontes externas (como a PokéAPI).

🚀 Tecnologias Utilizadas
Java 17+

Spring Boot

Maven

REST API

HTML/CSS/JavaScript (Frontend simples)

📁 Estrutura do Projeto
bash
Copiar
Editar
api de pokemon2/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/pokemon/
│   │   │   ├── controller/          # Controladores da API
│   │   │   ├── model/               # Modelos de domínio (Pokemon, Ability, etc.)
│   │   │   ├── repository/          # Interfaces de acesso a dados
│   │   │   ├── service/             # Lógica de negócio
│   │   │   └── PokemonApplication.java  # Classe principal
│   │   └── resources/
│   │       ├── static/              # Frontend estático
│   │       └── application.properties
📌 Como Executar

Pré-requisitos

JDK 17 ou superior
Maven 3.8+

Passos para execução

Clone o repositório:

git clone https://github.com/Ryannzadas/PokeApi-pokedex.git
cd PokeApi-pokedex

Compile e execute:

mvn spring-boot:run

Acesse:

API: http://localhost:8080/api/pokemon

Frontend: http://localhost:8080/index.html

🧪 Exemplos de Endpoints
GET /api/pokemon: Lista de Pokémon

GET /api/pokemon/{id}: Detalhes de um Pokémon

GET /api/pokemon/{id}/evolutions: Cadeia de evolução

📄 Licença
Este projeto é de uso educacional e está sob a licença MIT.
