**📘 PokeApi Pokedex**

Este projeto é uma Pokédex interativa que consome a PokéAPI para exibir informações detalhadas sobre diferentes Pokémon. Desenvolvido com Java e Spring Boot no backend, e HTML, CSS e JavaScript no frontend, oferece uma experiência completa para os fãs de Pokémon.

**🔍 Funcionalidades**

Listagem de Pokémon com informações básicas.
Detalhes individuais de cada Pokémon, incluindo habilidades, tipos e estatísticas.
Busca por nome ou ID do Pokémon.
Interface responsiva para diferentes dispositivos.

**🛠️ Tecnologias Utilizadas**
Backend:

Java 17+
Spring Boot
Maven

Frontend:

HTML5
CSS3
JavaScript

API Externa:

PokéAPI


**🚀 Como Executar o Projeto**

**Pré-requisitos**

Java 17 ou superior instalado.

Maven instalado.

Passos para execução

**Clone o repositório:**

git clone https://github.com/Ryannzadas/PokeApi-pokedex.git

cd PokeApi-pokedex

**Compile e execute o projeto:**

mvn spring-boot:run

**Acesse a aplicação:**

Frontend: http://localhost:8080/index.html

API: http://localhost:8080/api/pokemon

**📁 Estrutura do Projeto**

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


**📄 Licença**
Este projeto está sob a licença MIT. Sinta-se à vontade para utilizá-lo e modificá-lo conforme necessário.
