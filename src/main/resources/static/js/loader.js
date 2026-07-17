document.addEventListener('DOMContentLoaded', async () => {
    // Detectar qué página es para saber qué cargar
    const path = window.location.pathname;
    let category = '';

    // Mapeo de páginas a categorías de la base de datos
    if (path.includes('tendencias.html')) category = 'Stitch';
    else if (path.includes('escolar.html')) category = 'Escolar';
    else if (path.includes('oficina.html')) category = 'Oficina';
    else if (path.includes('regaleria.html')) category = 'Regalo'; // Ojo: En tu DB es 'Regalo', no 'Regalería'
    else if (path.includes('marcas.html')) category = 'Marca';
    else if (path.includes('index.html')) {
        // El index carga "Novedades" (podría ser una lógica distinta o una categoría específica)
        // Por ahora lo dejamos que cargue manual o lógica específica si tienes.
        return; 
    }

    if (!category) return;

    console.log(`Cargando productos de categoría: ${category}...`);
    const container = document.querySelector('.product-grid');

    try {
        // Petición al Backend
        const response = await fetch(`http://72.61.9.249:9000/api/products/category/${category}`);
        
        if (!response.ok) throw new Error('Error al obtener productos');
        
        const products = await response.json();

        if (products.length > 0) {
            container.innerHTML = ''; // Limpiar lo que había en el HTML estático
            
            products.forEach(product => {
                // Manejo de imagen por si viene vacía
                const imgUrl = product.imageUrl || 'https://placehold.co/300x300/EFEFEF/333?text=Sin+Imagen';
                
                // Construcción del HTML
                const cardHTML = `
                    <div class="product-card" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}" data-image="${imgUrl}">
                        
                        <!-- BOTÓN DE FAVORITOS AGREGADO AQUÍ -->
                        <button class="wishlist-btn" title="Agregar a favoritos"><i class="fa-regular fa-heart"></i></button>
                        
                        <img src="${imgUrl}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <div class="price">
                            <span class="current-price">S/ ${product.price.toFixed(2)}</span>
                            ${product.originalPrice ? `<span class="old-price">S/ ${product.originalPrice.toFixed(2)}</span>` : ''}
                        </div>
                        
                        <!-- Botón de comprar con la clase correcta para carrito.js -->
                        <button class="btn btn-secondary add-to-cart-btn">Comprar</button>
                    </div>
                `;
                
                container.innerHTML += cardHTML;
            });
            console.log(`Loader: ${products.length} productos cargados correctamente.`);
        } else {
            console.log("No se encontraron productos en la BD para esta categoría.");
        }

    } catch (error) {
        console.error("Error en loader.js:", error);
        // Si falla la BD, no borramos el HTML estático para que no se vea vacío
    }
});