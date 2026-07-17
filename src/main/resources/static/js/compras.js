document.addEventListener('DOMContentLoaded', async () => {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const historyContainer = document.getElementById('history-container');
    const modal = document.getElementById('order-details-modal');
    const loadingMessage = document.querySelector('p[style*="Cargando tus compras"]');

    let downloadedOrders = [];

    if (!currentUser) {
        if(loadingMessage) loadingMessage.style.display = 'none';
        historyContainer.innerHTML = `
            <div style="text-align: center; margin-top: 40px;">
                <p>Debes iniciar sesión para ver tu historial.</p>
                <a href="#" class="btn btn-primary" onclick="document.querySelector('.open-login-modal').click(); return false;">Iniciar Sesión</a>
            </div>`;
        return;
    }

    try {
        const userId = currentUser.dbUserId || currentUser.id;
        const response = await fetch(`http://72.61.9.249:9000/api/orders/user/${userId}`);
        
        if (!response.ok) throw new Error('Error al conectar con el servidor');
        
        downloadedOrders = await response.json();

        if(loadingMessage) loadingMessage.style.display = 'none';

        if (downloadedOrders.length === 0) {
            historyContainer.innerHTML = `
                <div style="text-align:center; padding: 40px; color: #666;">
                    <i class="fa-solid fa-bag-shopping" style="font-size: 50px; color: #ccc; margin-bottom: 15px;"></i>
                    <p>Aún no has realizado ninguna compra.</p>
                    <a href="index.html" class="btn btn-primary" style="margin-top:20px;">Ir a comprar</a>
                </div>`;
            return;
        }

        let htmlContent = '';
        
        // Renderizar lista de compras
        downloadedOrders.reverse().forEach(order => {
            const dateObj = new Date(order.orderDate);
            const dateStr = dateObj.toLocaleDateString() + ' ' + dateObj.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

            let itemsPreviewHtml = '';
            order.items.slice(0, 2).forEach(item => {
                const img = item.image_url || 'https://placehold.co/60x60/EFEFEF/333?text=IMG';
                itemsPreviewHtml += `<img src="${img}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; margin-right:5px;" title="${item.name}">`;
            });
            if(order.items.length > 2) {
                itemsPreviewHtml += `<span style="font-size:0.8rem; color:#777; align-self:center;">+${order.items.length - 2} más</span>`;
            }

            // *** CAMBIO: Forzar estado visual a "EN PROCESO" en la tarjeta ***
            // Usamos un color naranja/amarillo para indicar proceso
            const statusBadge = `<div style="font-size: 0.8rem; background: #ffc107; color: #333; padding: 4px 10px; border-radius: 15px; font-weight: 600;">EN PROCESO</div>`;

            htmlContent += `
                <div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 25px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                    <div style="background: #f8f9fa; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #e0e0e0;">
                        <div>
                            <span style="font-weight: 700; color: #333; font-size: 1.1rem;">Orden #${order.receiptNumber}</span>
                            <div style="font-size: 0.85rem; color: #777; margin-top: 4px;">${dateStr}</div>
                        </div>
                        ${statusBadge}
                    </div>
                    <div style="padding: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                        <div style="display:flex;">${itemsPreviewHtml}</div>
                        <div style="text-align: right;">
                            <div style="font-weight: 700; color: #FE4A49; font-size: 1.2rem;">S/ ${order.totalAmount.toFixed(2)}</div>
                            <button class="btn btn-primary btn-sm view-details-btn" data-id="${order.orderId}" style="margin-top: 10px; padding: 8px 15px; font-size: 0.9rem;">
                                <i class="fa-solid fa-eye"></i> Ver Detalles y Rastreo
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });

        historyContainer.innerHTML = htmlContent;

        document.querySelectorAll('.view-details-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const orderId = e.target.closest('button').dataset.id;
                openDetailsModal(orderId);
            });
        });

    } catch (error) {
        console.error("Error:", error);
        historyContainer.innerHTML = '<p style="text-align:center; color:red;">Error al cargar compras.</p>';
    }

    // --- LÓGICA DEL MODAL ---
    function openDetailsModal(orderId) {
        const order = downloadedOrders.find(o => o.orderId == orderId);
        if (!order) return;

        document.getElementById('modal-order-id').textContent = `Orden #${order.receiptNumber}`;
        document.getElementById('modal-total-amount').textContent = `S/ ${order.totalAmount.toFixed(2)}`;

        const itemsContainer = document.getElementById('modal-items-list');
        itemsContainer.innerHTML = '';
        order.items.forEach(item => {
            itemsContainer.innerHTML += `
                <div style="display: flex; gap: 10px; margin-bottom: 10px; border-bottom: 1px solid #f9f9f9; padding-bottom:5px;">
                    <img src="${item.image_url || 'https://placehold.co/50'}" style="width: 40px; height: 40px; object-fit: cover;">
                    <div style="font-size: 0.9rem;">
                        <strong>${item.name}</strong><br>
                        Cant: ${item.quantity} x S/ ${item.price.toFixed(2)}
                    </div>
                </div>
            `;
        });

        // *** CAMBIO: Forzar la lógica de rastreo a "En Proceso" ***
        const step2 = document.getElementById('track-step-2');
        const step3 = document.getElementById('track-step-3');
        const progressBar = document.getElementById('track-progress');
        const locationText = document.getElementById('modal-location-text');

        // Resetear todo a inactivo
        step2.classList.remove('active');
        step3.classList.remove('active');
        
        // Estado: EN PROCESO (Solo el paso 1 activo)
        progressBar.style.width = '15%'; // Un poquito de progreso
        locationText.innerHTML = `
            <span style="color: #d39e00; font-weight: bold;">En Preparación</span><br>
            Almacén Central KOKY (Huancayo) - Estamos empaquetando tu pedido.
        `;
        locationText.style.color = "#555";

        modal.classList.add('active');
    }

    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.classList.remove('active');
        });
        const closeBtn = modal.querySelector('.modal-close');
        if(closeBtn) closeBtn.addEventListener('click', () => modal.classList.remove('active'));
    }
});