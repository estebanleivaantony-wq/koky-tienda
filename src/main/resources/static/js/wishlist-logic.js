document.addEventListener('DOMContentLoaded', () => {
    console.log("Wishlist Logic Loaded");

    // Delegación de eventos: Detectar click en cualquier corazón
    document.body.addEventListener('click', async (e) => {
        // Buscamos si el click fue en el botón o en el ícono dentro del botón
        const btn = e.target.closest('.wishlist-btn');
        
        if (btn) {
            e.preventDefault(); // Evitar saltos de página

            // 1. Verificar sesión
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            if (!currentUser) {
                alert("Debes iniciar sesión para guardar favoritos.");
                return;
            }

            // 2. Obtener datos
            const card = btn.closest('.product-card');
            const productId = card.dataset.id;
            const userId = currentUser.dbUserId || 1; // Tu ID de BD

            // 3. Efecto visual inmediato (antes de la petición para que se sienta rápido)
            const icon = btn.querySelector('i');
            const isActive = btn.classList.contains('active');

            if (!isActive) {
                // --- AGREGAR A FAVORITOS ---
                try {
                    const response = await fetch(`http://72.61.9.249:9000/api/wishlist/${userId}/add/${productId}`, {
                        method: 'POST'
                    });

                    if (response.ok) {
                        btn.classList.add('active');
                        icon.classList.remove('fa-regular');
                        icon.classList.add('fa-solid'); // Corazón lleno
                        console.log("Producto agregado a wishlist");
                    } else {
                        alert("Error al agregar a favoritos");
                    }
                } catch (error) {
                    console.error(error);
                }
            } else {
                // --- ELIMINAR DE FAVORITOS (Opcional: si vuelves a dar click) ---
                try {
                    const response = await fetch(`http://72.61.9.249:9000/api/wishlist/${userId}/remove/${productId}`, {
                        method: 'DELETE'
                    });

                    if (response.ok) {
                        btn.classList.remove('active');
                        icon.classList.remove('fa-solid');
                        icon.classList.add('fa-regular'); // Corazón vacío
                        console.log("Producto eliminado de wishlist");
                    }
                } catch (error) {
                    console.error(error);
                }
            }
        }
    });
});