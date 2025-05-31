let offset = 0;
const limit = 20;
let loading = false;
let currentType = 'all';
let currentPage = 1;
let isLoading = false;
let allPokemon = [];
let favorites = JSON.parse(localStorage.getItem('favorites')) || [];
let compareList = JSON.parse(sessionStorage.getItem('compareList')) || [];
let currentSort = { field: 'id', ascending: true };
let currentTypeFilter = 'all';

// Cores para os tipos de Pokémon
const typeColors = {
    normal: '#A8A878',
    fire: '#F08030',
    water: '#6890F0',
    electric: '#F8D030',
    grass: '#78C850',
    ice: '#98D8D8',
    fighting: '#C03028',
    poison: '#A040A0',
    ground: '#E0C068',
    flying: '#A890F0',
    psychic: '#F85888',
    bug: '#A8B820',
    rock: '#B8A038',
    ghost: '#705898',
    dragon: '#7038F8',
    dark: '#705848',
    steel: '#B8B8D0',
    fairy: '#EE99AC'
};

// Tradução dos tipos para português
const typeTranslations = {
    normal: 'Normal',
    fire: 'Fogo',
    water: 'Água',
    electric: 'Elétrico',
    grass: 'Planta',
    ice: 'Gelo',
    fighting: 'Lutador',
    poison: 'Veneno',
    ground: 'Terra',
    flying: 'Voador',
    psychic: 'Psíquico',
    bug: 'Inseto',
    rock: 'Pedra',
    ghost: 'Fantasma',
    dragon: 'Dragão',
    dark: 'Sombrio',
    steel: 'Aço',
    fairy: 'Fada'
};

// Carregar Pokémon iniciais
document.addEventListener('DOMContentLoaded', () => {
    loadSavedTheme();
    loadPokemon();
    setupTypeFilters();
    setupThemeToggle();
    updateFavoriteCount();
    setupSearchFilter();
    compareList = JSON.parse(sessionStorage.getItem('compareList')) || [];
    updateCompareCount();
    saveCompareList();
    forceFavoriteIcon();
    const savedFilter = localStorage.getItem('currentTypeFilter');
    if (savedFilter) {
        currentTypeFilter = savedFilter;
        filterPokemonByType();
    }
    const savedType = localStorage.getItem('currentType');
    if (savedType) {
        currentType = savedType;
        // Marcar o botão correto como ativo
        const typeButtons = document.querySelectorAll('.type-filters button');
        typeButtons.forEach(btn => {
            if (btn.dataset.type === savedType) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }
});

// Configurar filtros de tipo
function setupTypeFilters() {
    const typeButtons = document.querySelectorAll('.type-filters button');
    typeButtons.forEach(button => {
        button.addEventListener('click', () => {
            typeButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            setCurrentType(button.dataset.type);
            filterPokemonByType();
        });
    });
}

// Configurar toggle de tema
function setupThemeToggle() {
    const themeButton = document.getElementById('themeToggleBtn');
    themeButton.addEventListener('click', toggleTheme);
}

// Alternar entre tema claro e escuro
function toggleTheme() {
    const body = document.body;
    const currentTheme = body.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    body.setAttribute('data-theme', newTheme);
    
    // Salvar preferência no localStorage
    localStorage.setItem('theme', newTheme);
    
    // Atualizar ícone
    const themeIcon = document.querySelector('#themeToggleBtn i');
    themeIcon.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
}

// Carregar tema salvo
function loadSavedTheme() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        document.body.setAttribute('data-theme', savedTheme);
        const themeIcon = document.querySelector('#themeToggleBtn i');
        themeIcon.className = savedTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// Função para carregar mais Pokémon
async function loadMorePokemon() {
    if (!loading) {
        const spinner = document.getElementById('loadingSpinner');
        spinner.classList.remove('d-none');
        
        offset += limit;
        await loadPokemon();
        
        spinner.classList.add('d-none');
    }
}

// Função para carregar Pokémon
async function loadPokemon() {
    if (isLoading) return;
    isLoading = true;
    try {
        // Buscar todos de uma vez
        const response = await fetch(`/api/pokemon?size=1000&page=0`);
        const data = await response.json();
        if (data.content) {
            allPokemon = data.content;
            // Se houver filtro salvo, aplicar após carregar
            const savedFilter = localStorage.getItem('currentTypeFilter');
            if (savedFilter && savedFilter !== 'all') {
                filterPokemonByType();
            } else {
                displayPokemon(allPokemon);
            }
        }
    } catch (error) {
        console.error('Erro ao carregar Pokémon:', error);
    } finally {
        isLoading = false;
    }
}

// Função para buscar Pokémon
async function searchPokemon() {
    const searchInput = document.getElementById('searchInput');
    const searchTerm = searchInput.value.trim();
    
    if (!searchTerm) return;
    
    try {
        let url;
        if (isNaN(searchTerm)) {
            url = `/api/pokemon/name/${searchTerm}`;
        } else {
            url = `/api/pokemon/${searchTerm}`;
        }
        
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Pokémon não encontrado');
        }
        
        const pokemon = await response.json();
        showPokemonDetails(pokemon);
    } catch (error) {
        alert('Pokémon não encontrado');
    }
}

// Função para filtrar Pokémon por tipo
function filterPokemonByType() {
    let baseList = allPokemon;
    if (currentTypeFilter === 'favorites') {
        baseList = allPokemon.filter(pokemon => favorites.includes(Number(pokemon.id)));
        document.getElementById('backButtonRow').style.display = 'block';
    } else {
        document.getElementById('backButtonRow').style.display = 'none';
    }
    let filtered = baseList;
    if (currentType !== 'all') {
        filtered = baseList.filter(pokemon =>
            pokemon.types.some(type => type.type.name === currentType)
        );
    }
    displayPokemon(filtered);
    // Esconder ou mostrar o botão 'Carregar mais'
    const loadMoreBtn = document.querySelector('.load-more-btn');
    if (currentTypeFilter === 'favorites' && filtered.length <= 20) {
        loadMoreBtn.style.display = 'none';
    } else {
        loadMoreBtn.style.display = 'inline-block';
    }
}

// Função para criar card do Pokémon
function createPokemonCard(pokemon) {
    const col = document.createElement('div');
    col.className = 'col-md-4 col-lg-3 mb-4';
    
    col.innerHTML = `
        <div class="pokemon-card" onclick="showPokemonDetails(${pokemon.id})">
            <div class="pokemon-image-container">
                <img src="${pokemon.sprites.front_default}" alt="${pokemon.name}" class="pokemon-image">
            </div>
            <div class="pokemon-info">
                <div class="pokemon-name">${pokemon.name}</div>
                <div class="pokemon-id">#${String(pokemon.id).padStart(3, '0')}</div>
                <div class="types-container">
                    ${pokemon.types.map(type => `
                        <span class="type-badge" style="background-color: ${getTypeColor(type.type.name)}">
                            ${type.type.name}
                        </span>
                    `).join('')}
                </div>
            </div>
        </div>
    `;
    
    return col;
}

// Função para mostrar detalhes do Pokémon
async function showPokemonDetails(id) {
    try {
        const response = await fetch(`/api/pokemon/${id}`);
        const pokemon = await response.json();
        const modalTitle = document.getElementById('pokemonName');
        modalTitle.textContent = pokemon.name;
        modalTitle.setAttribute('data-id', pokemon.id);
        document.getElementById('pokemonImage').src = pokemon.sprites.front_default;
        
        // Tipos
        const typesContainer = document.getElementById('pokemonTypes');
        typesContainer.innerHTML = pokemon.types.map(type => `
            <span class="type-badge" style="background-color: ${getTypeColor(type.type.name)}">
                ${type.type.name}
            </span>
        `).join('');
        
        // Estatísticas
        const statsContainer = document.getElementById('pokemonStats');
        statsContainer.innerHTML = pokemon.stats.map(stat => `
            <div class="stat-item">
                <div class="d-flex justify-content-between">
                    <span>${stat.stat.name}</span>
                    <span>${stat.base_stat}</span>
                </div>
                <div class="stat-bar">
                    <div class="stat-fill" style="width: ${(stat.base_stat / 255) * 100}%"></div>
                </div>
            </div>
        `).join('');
        
        // Habilidades
        const abilitiesContainer = document.getElementById('pokemonAbilities');
        abilitiesContainer.innerHTML = pokemon.abilities.map(ability => `
            <span class="ability-badge">${ability.ability.name}</span>
        `).join('');
        
        // Dimensões
        const dimensionsContainer = document.getElementById('pokemonDimensions');
        dimensionsContainer.innerHTML = `
            <div class="dimension-item">
                <div>Altura</div>
                <div>${pokemon.height / 10}m</div>
            </div>
            <div class="dimension-item">
                <div>Peso</div>
                <div>${pokemon.weight / 10}kg</div>
            </div>
        `;
        
        // Evoluções
        const evolutionsContainer = document.getElementById('pokemonEvolutions');
        if (pokemon.evolution_chain) {
            evolutionsContainer.innerHTML = pokemon.evolution_chain.map(evolution => `
                <div class="evolution-item" onclick="showPokemonDetails(${evolution.id})">
                    <img src="${evolution.sprite}" alt="${evolution.name}">
                    <div>${evolution.name}</div>
                </div>
            `).join('');
        } else {
            evolutionsContainer.innerHTML = '<p>Nenhuma evolução disponível</p>';
        }
        
        // Movimentos
        const movesContainer = document.getElementById('pokemonMoves');
        movesContainer.innerHTML = pokemon.moves.slice(0, 10).map(move => `
            <span class="move-badge">${move.move.name}</span>
        `).join('');
        
        // Botões de ação
        const favoriteBtn = document.querySelector('.modal-actions .fa-heart');
        favoriteBtn.className = favorites.includes(id) ? 'fas fa-heart' : 'far fa-heart';
        
        const compareBtn = document.querySelector('.modal-actions .fa-balance-scale');
        compareBtn.style.color = compareList.includes(id) ? 'var(--primary-color)' : '';
        
        // Mostrar modal
        const modal = new bootstrap.Modal(document.getElementById('pokemonModal'));
        modal.show();
    } catch (error) {
        console.error('Erro ao carregar detalhes do Pokémon:', error);
    }
}

// Adicionar evento de busca ao pressionar Enter
document.getElementById('searchInput').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        searchPokemon();
    }
});

// Filtrar Pokémon
function filterPokemon() {
    const container = document.getElementById('pokemonList');
    container.innerHTML = '';
    
    let filteredPokemon = allPokemon;
    
    if (currentTypeFilter !== 'all') {
        filteredPokemon = allPokemon.filter(pokemon => 
            pokemon.types.some(type => type.type.name === currentTypeFilter)
        );
    }
    
    filteredPokemon = sortPokemonList(filteredPokemon);
    displayPokemon(filteredPokemon);
}

// Ordenar Pokémon
function sortPokemon() {
    const sortField = document.getElementById('sortSelect').value;
    currentSort.field = sortField;
    filterPokemon();
}

// Alternar ordem de classificação
function toggleSortOrder() {
    currentSort.ascending = !currentSort.ascending;
    filterPokemon();
}

// Ordenar lista de Pokémon
function sortPokemonList(pokemonList) {
    return [...pokemonList].sort((a, b) => {
        let valueA = a[currentSort.field];
        let valueB = b[currentSort.field];
        
        if (typeof valueA === 'string') {
            valueA = valueA.toLowerCase();
            valueB = valueB.toLowerCase();
        }
        
        if (currentSort.ascending) {
            return valueA > valueB ? 1 : -1;
        } else {
            return valueA < valueB ? 1 : -1;
        }
    });
}

// Alternar favorito
function toggleFavorite() {
    const pokemonId = Number(document.getElementById('pokemonName').getAttribute('data-id'));
    const index = favorites.indexOf(pokemonId);
    if (index === -1) {
        favorites.push(pokemonId);
    } else {
        favorites.splice(index, 1);
    }
    localStorage.setItem('favorites', JSON.stringify(favorites));
    updateFavoriteCount();
    const favoriteBtn = document.querySelector('.modal-actions .fa-heart');
    favoriteBtn.className = index === -1 ? 'fas fa-heart' : 'far fa-heart';
    // Se estiver na visualização de favoritos, atualizar a lista imediatamente
    if (currentTypeFilter === 'favorites') {
        filterPokemonByType();
        // Fechar o modal automaticamente
        const modal = bootstrap.Modal.getInstance(document.getElementById('pokemonModal'));
        if (modal) modal.hide();
    }
}

// Atualizar contador de favoritos
function updateFavoriteCount() {
    // Garantir que todos os IDs são números
    favorites = favorites.map(id => Number(id));
    const countElement = document.querySelector('.favorite-count');
    countElement.textContent = favorites.length;
}

// Salvar filtro atual sempre que mudar
function setCurrentTypeFilter(filter) {
    currentTypeFilter = filter;
    localStorage.setItem('currentTypeFilter', filter);
}

// Modificar toggleFavoritesView para usar setCurrentTypeFilter
function toggleFavoritesView() {
    const container = document.getElementById('pokemonList');
    container.innerHTML = '';
    favorites = favorites.map(id => Number(id));
    console.log('Favoritos:', favorites);
    console.log('allPokemon:', allPokemon.map(p => p.id));
    if (currentTypeFilter === 'favorites') {
        setCurrentTypeFilter('all');
        filterPokemonByType();
    } else {
        setCurrentTypeFilter('favorites');
        filterPokemonByType();
    }
}

// Modificar backToAllPokemon para usar setCurrentTypeFilter
function backToAllPokemon() {
    setCurrentTypeFilter('all');
    document.getElementById('backButtonRow').style.display = 'none';
    filterPokemonByType();
}

// Persistir compareList na sessão
function saveCompareList() {
    sessionStorage.setItem('compareList', JSON.stringify(compareList));
    updateCompareCount();
}

// Adicionar à comparação
function addToCompare() {
    const pokemonId = parseInt(document.getElementById('pokemonName').getAttribute('data-id'));
    const index = compareList.indexOf(pokemonId);
    if (index === -1) {
        if (compareList.length < 3) {
            compareList.push(pokemonId);
        } else {
            alert('Você pode comparar no máximo 3 Pokémon por vez.');
            return;
        }
    } else {
        compareList.splice(index, 1);
    }
    saveCompareList();
    const compareBtn = document.querySelector('.modal-actions .fa-balance-scale');
    compareBtn.style.color = index === -1 ? 'var(--primary-color)' : '';
    if (compareList.length > 1) {
        showCompareModal();
    }
}

// Limpar comparação
function clearCompare() {
    compareList = [];
    saveCompareList();
    showCompareModal();
}

// Mostrar modal de comparação
async function showCompareModal() {
    const container = document.getElementById('compareContainer');
    container.innerHTML = '';
    if (!compareList.length) {
        container.innerHTML = '<div style="text-align:center; color:var(--text-color); font-size:1.2rem; margin-top:2rem;">Nenhum Pokémon selecionado para comparar</div>';
    } else {
        let erroComparacao = false;
        for (const id of compareList) {
            try {
                const response = await fetch(`/api/pokemon/${id}`);
                if (!response.ok) throw new Error('Erro ao buscar Pokémon ID ' + id);
                const pokemon = await response.json();
                if (!pokemon || !pokemon.name) throw new Error('Pokémon não encontrado para comparação: ' + id);
                const col = document.createElement('div');
                col.className = 'col-md-4';
                col.innerHTML = `
                    <div class="compare-pokemon">
                        <img src="${pokemon.sprites.front_default}" alt="${pokemon.name}">
                        <h5>${pokemon.name}</h5>
                        <div class="compare-stats">
                            ${pokemon.stats.map(stat => `
                                <div class="stat-item">
                                    <div class="d-flex justify-content-between">
                                        <span>${stat.stat.name}</span>
                                        <span>${stat.base_stat}</span>
                                    </div>
                                    <div class="stat-bar">
                                        <div class="stat-fill" style="width: ${(stat.base_stat / 255) * 100}%"></div>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                `;
                container.appendChild(col);
            } catch (error) {
                erroComparacao = true;
                console.error('Erro ao carregar Pokémon para comparação:', error);
            }
        }
        if (erroComparacao && !container.innerHTML) {
            container.innerHTML = '<div style="text-align:center; color:var(--text-color); font-size:1.2rem; margin-top:2rem;">Erro ao buscar Pokémon para comparação. Veja o console para detalhes.</div>';
        }
    }
    // Adicionar botão de limpar comparação SEMPRE que houver pelo menos 1 na lista
    let clearBtn = document.getElementById('clearCompareBtn');
    if (!clearBtn) {
        clearBtn = document.createElement('button');
        clearBtn.id = 'clearCompareBtn';
        clearBtn.className = 'btn btn-danger mt-3';
        clearBtn.textContent = 'Limpar comparação';
        clearBtn.onclick = clearCompare;
        container.parentElement.appendChild(clearBtn);
    }
    clearBtn.style.display = compareList.length ? 'block' : 'none';
    const modal = new bootstrap.Modal(document.getElementById('compareModal'));
    modal.show();
}

// Obter cor do tipo
function getTypeColor(type) {
    return typeColors[type] || '#A8A878';
}

// Busca local em tempo real
function setupSearchFilter() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            if (!allPokemon || allPokemon.length === 0) return;
            const term = this.value.trim().toLowerCase();
            if (!term) {
                displayPokemon(allPokemon); // Mostra todos
                return;
            }
            const filtered = allPokemon.filter(p => p.name.toLowerCase().includes(term));
            displayPokemon(filtered);
        });
    }
}

function displayPokemon(pokemonList) {
    const container = document.getElementById('pokemonList');
    container.innerHTML = '';
    if (!pokemonList || pokemonList.length === 0) {
        container.innerHTML = '<div style="text-align:center; color:var(--text-color); font-size:1.2rem; margin-top:2rem;">Nenhum Pokémon encontrado</div>';
        return;
    }
    pokemonList.forEach(pokemon => {
        const card = createPokemonCard(pokemon);
        container.appendChild(card);
    });
}

function updateCompareCount() {
    const compareCount = document.getElementById('compare-count');
    if (compareCount) {
        compareCount.textContent = compareList.length;
    }
}

function forceFavoriteIcon() {
    // Garante que o botão de favoritos na navbar sempre tenha o ícone de coração
    const favBtn = document.querySelector('.navbar .fa-heart');
    if (favBtn) {
        favBtn.className = 'fas fa-heart';
    }
}

// Salvar filtro de tipo sempre que mudar
function setCurrentType(type) {
    currentType = type;
    localStorage.setItem('currentType', type);
}

function goToHome() {
    setCurrentTypeFilter('all');
    setCurrentType('all');
    // Resetar botões de tipo
    const typeButtons = document.querySelectorAll('.type-filters button');
    typeButtons.forEach(btn => {
        if (btn.dataset.type === 'all') {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });
    document.getElementById('backButtonRow').style.display = 'none';
    filterPokemonByType();
} 