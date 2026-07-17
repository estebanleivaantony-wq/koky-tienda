document.addEventListener('DOMContentLoaded', async () => {
    // 1. Obtener el término de búsqueda de la URL (?q=mochila)
    const params = new URLSearchParams(window.location.search);
    const query = params.get('q');

    const title = document.getElementById('search-page-title');
    const resultsCount = document.getElementById('search-results-count');
    const resultsGrid = document.getElementById('search-results-grid');
    const noResults = document.getElementById('no-results-message');

    if (!query) {
        title.textContent = "Búsqueda vacía";
        resultsCount.textContent = "";
        return;
    }

    title.textContent = `Resultados para: "${query}"`;

    try {
        // 2. Conectar con el Backend (Base de Datos)
        // Asegúrate de que el puerto sea el correcto (3001 según tu captura)
        const response = await fetch(`http://72.61.9.249:9000/api/products/search?q=${encodeURIComponent(query)}`);
        
        if (!response.ok) throw new Error('Error en el servidor');
        
        const products = await response.json();

        // 3. Renderizar Productos
        if (products.length > 0) {
            resultsCount.textContent = `Se encontraron ${products.length} productos`;
            resultsGrid.innerHTML = '';
            
            products.forEach(product => {
                // Generar HTML de la tarjeta (Igual que en tendencias.html)
                const cardHTML = `
                    <div class="product-card" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}" data-image="${product.imageUrl || 'https://placehold.co/300'}">
                        
                        <!-- Botón Wishlist -->
                        <button class="wishlist-btn" title="Agregar a favoritos"><i class="fa-regular fa-heart"></i></button>
                        
                        <img src="${product.imageUrl || 'https://placehold.co/300x300/EFEFEF/333?text=Sin+Imagen'}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <div class="price">
                            <span class="current-price">S/ ${product.price.toFixed(2)}</span>
                            ${product.originalPrice ? `<span class="old-price">S/ ${product.originalPrice.toFixed(2)}</span>` : ''}
                        </div>
                        
                        <button class="btn btn-secondary add-to-cart-btn">Comprar</button>
                    </div>
                `;
                resultsGrid.innerHTML += cardHTML;
            });
            
            noResults.style.display = 'none';
            resultsGrid.style.display = 'grid';

        } else {
            // No hay resultados
            resultsCount.textContent = "0 resultados";
            resultsGrid.style.display = 'none';
            noResults.style.display = 'block';
        }

    } catch (error) {
        console.error("Error buscando productos:", error);
        resultsGrid.innerHTML = '<p style="text-align:center; color:red;">Ocurrió un error al buscar. Intenta de nuevo.</p>';
    }
});