document.addEventListener('DOMContentLoaded', () => {
    // --- Elementos del DOM ---
    const resultsTitle = document.getElementById('search-page-title');
    const resultsSubtitle = document.getElementById('search-page-subtitle');
    const resultsCount = document.getElementById('search-results-count');
    const resultsGrid = document.getElementById('search-results-grid');
    const noResultsMessage = document.getElementById('no-results-message');
    
    // --- Filtros ---
    const categoryLinks = document.querySelectorAll('#category-filter-list a');
    const priceSlider = document.getElementById('price-range-slider');
    const sortOptions = document.getElementById('sort-options');

    // --- Obtener datos iniciales ---
    const params = new URLSearchParams(window.location.search);
    let searchTerm = params.get('q') || '';

    // --- Estado actual de los filtros ---
    let currentCategory = 'all';
    let currentMaxPrice = 500;
    let currentSort = 'default';

    // Función principal para filtrar y mostrar productos
    function filterAndRenderProducts() {
        let filteredProducts = [...allProducts];

        // 1. Filtrar por término de búsqueda (si existe)
        if (searchTerm) {
            const lowerCaseQuery = searchTerm.toLowerCase();
            filteredProducts = filteredProducts.filter(p => p.name.toLowerCase().includes(lowerCaseQuery));
            resultsTitle.textContent = `Resultados para: "${searchTerm}"`;
            resultsSubtitle.textContent = `Hemos encontrado estos productos para ti.`;
        } else {
            resultsTitle.textContent = 'Explora Todos Nuestros Productos';
            resultsSubtitle.textContent = 'Encuentra todo lo que necesitas para tu creatividad y organización.';
        }

        // 2. Filtrar por categoría
        if (currentCategory !== 'all') {
            // Un truco para que las categorías coincidan con las palabras en los nombres de productos
            const categoryTerm = currentCategory.toLowerCase().replace('regalería', 'regalo');
            filteredProducts = filteredProducts.filter(p => p.name.toLowerCase().includes(categoryTerm));
        }

        // 3. Filtrar por precio
        filteredProducts = filteredProducts.filter(p => p.price <= currentMaxPrice);
        
        // 4. Ordenar productos
        switch(currentSort) {
            case 'price-asc':
                filteredProducts.sort((a, b) => a.price - b.price);
                break;
            case 'price-desc':
                filteredProducts.sort((a, b) => b.price - a.price);
                break;
            case 'name-asc':
                filteredProducts.sort((a, b) => a.name.localeCompare(b.name));
                break;
        }

        // 5. Renderizar en el DOM
        renderGrid(filteredProducts);
    }

    // Función para dibujar la cuadrícula de productos
    function renderGrid(products) {
        resultsCount.textContent = `Mostrando ${products.length} producto(s)`;
        if (products.length > 0) {
            noResultsMessage.style.display = 'none';
            resultsGrid.style.display = 'grid';
            resultsGrid.innerHTML = ''; 
            products.forEach(product => {
                const productCardHTML = `
                    <div class="product-card" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}" data-image="${product.image}">
                        <img src="${product.image}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <div class="price"><span class="current-price">S/ ${product.price.toFixed(2)}</span></div>
                        <button class="btn btn-secondary add-to-cart-btn">Comprar</button>
                    </div>
                `;
                resultsGrid.innerHTML += productCardHTML;
            });
            // Vuelve a activar los botones "Comprar" después de dibujarlos
            addCartEventListenersToPage();
        } else {
            resultsGrid.style.display = 'none';
            noResultsMessage.style.display = 'flex';
        }
    }

    // Event Listeners para los filtros
    categoryLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            categoryLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            currentCategory = link.dataset.category;
            // Al filtrar por categoría, limpiamos el término de búsqueda para evitar confusiones
            searchTerm = '';
            filterAndRenderProducts();
        });
    });

    if (priceSlider) {
        priceSlider.addEventListener('input', (e) => {
            currentMaxPrice = e.target.value;
            document.querySelector('#price-range-values span:last-child').textContent = `S/ ${currentMaxPrice}`;
        });
        // Aplicar el filtro de precio solo cuando se suelta el control deslizante para mejor rendimiento
        priceSlider.addEventListener('change', filterAndRenderProducts);
    }

    if (sortOptions) {
        sortOptions.addEventListener('change', (e) => {
            currentSort = e.target.value;
            filterAndRenderProducts();
        });
    }

    // Carga inicial de productos
    filterAndRenderProducts();
});

// Función para (re)asignar eventos a los botones "Comprar" generados dinámicamente
// Asegúrate de que carrito.js se cargue antes que busqueda.js
function addCartEventListenersToPage() {
    document.querySelectorAll('.add-to-cart-btn').forEach(button => {
        // Clonamos el botón para limpiar eventos previos y evitar duplicados
        const newButton = button.cloneNode(true);
        button.parentNode.replaceChild(newButton, button);

        newButton.addEventListener('click', (event) => {
            const card = event.target.closest('.product-card');
            const productId = card.dataset.id;
            const name = card.dataset.name;
            const price = card.dataset.price;
            const image = card.querySelector('img').src; 
            if (typeof addToCart === 'function') {
                addToCart(productId, name, price, image);
            }
        });
    });
}