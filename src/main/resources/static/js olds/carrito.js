// carrito.js — versión migrada a BD (KOKY API) + Delegación de Eventos + Puntos
// Requiere backend corriendo en: http://72.61.9.249:9000

document.addEventListener('DOMContentLoaded', () => {
    console.log("carrito.js (BD): DOMContentLoaded ejecutado.");

    // === CONFIG API ===
    const API_BASE = 'http://72.61.9.249:9000/api';

    // Obtiene un userId “temporal” para pruebas
    function getUserId() {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        // 1 = ID del usuario Admin cargado por DataLoader. Si usas otro, cámbialo aquí.
        const DEFAULT_DB_USER_ID = 1; 
        return currentUser?.dbUserId || DEFAULT_DB_USER_ID;
    }

    const PLACEHOLDER_IMG = 'https://placehold.co/100x100/EFEFEF/333?text=Producto';

    // --- Elementos del DOM ---
    const addToCartModal = document.getElementById('add-to-cart-modal');
    const closeModalBtn = addToCartModal ? addToCartModal.querySelector('.modal-close') : null;
    const continueShoppingBtn = addToCartModal ? document.getElementById('continue-shopping-btn') : null;
    
    // --- Carrito desde BD ---
    async function getCart() {
        const USER_ID = getUserId();
        try {
            const resp = await fetch(`${API_BASE}/cart/current?userId=${USER_ID}`);
            if (!resp.ok) throw new Error('No se pudo obtener el carrito');
            const items = await resp.json(); 
            return items.map(i => ({
                id: String(i.productId), 
                name: i.name,
                price: Number(i.price),
                image: i.image_url || PLACEHOLDER_IMG,
                quantity: Number(i.quantity)
            }));
        } catch (e) {
            console.error("Error obteniendo carrito:", e);
            return [];
        }
    }

    // --- Lógica del Modal "Producto Agregado" ---
    function showAddToCartModal(name, price, image) {
        if (!addToCartModal) return console.warn("Modal 'Producto agregado' no encontrado.");
        const modalName = document.getElementById('modal-product-name');
        const modalPrice = document.getElementById('modal-product-price');
        const modalImg = document.getElementById('modal-product-image');

        if(modalName) modalName.textContent = name;
        if(modalPrice) modalPrice.textContent = `S/ ${parseFloat(price).toFixed(2)}`;
        if(modalImg) modalImg.src = image;

        addToCartModal.classList.add('active');
    }

    function hideAddToCartModal() {
        if (addToCartModal) addToCartModal.classList.remove('active');
    }

    if (closeModalBtn) closeModalBtn.addEventListener('click', hideAddToCartModal);
    if (continueShoppingBtn) continueShoppingBtn.addEventListener('click', hideAddToCartModal);
    if (addToCartModal) addToCartModal.addEventListener('click', (e) => { if (e.target === addToCartModal) hideAddToCartModal(); });

    // --- Añadir al Carrito (Backend) ---
    async function addToCart(productId, name, price, image) {
        const USER_ID = getUserId();
        try {
            const payload = { 
                userId: Number(USER_ID), 
                productId: Number(productId), 
                name: name,
                price: parseFloat(price),
                image_url: image,
                quantity: 1 
            };

            const resp = await fetch(`${API_BASE}/cart/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) {
                const errData = await resp.json();
                console.error('Error /cart/add:', errData);
                alert('Error al agregar al carrito: ' + (errData.error || 'Desconocido'));
                return;
            }
            
            showAddToCartModal(name, price, image || PLACEHOLDER_IMG);
            updateCartIconCounter();
        } catch (e) {
            console.error('addToCart() fallo de red', e);
            alert('Error de conexión al agregar al carrito.');
        }
    }

    // --- DELEGACIÓN DE EVENTOS ---
    document.addEventListener('click', (event) => {
        if (event.target && event.target.classList.contains('add-to-cart-btn')) {
            const card = event.target.closest('.product-card');
            if (!card) return;

            const productId = card.dataset.id;
            const name = card.dataset.name; 
            const price = card.dataset.price;
            const imgElement = card.querySelector('img');
            const image = imgElement ? imgElement.src : '';

            if (!productId || !name || !price) {
                console.error("Faltan datos del producto en el HTML generado.", { productId, name, price });
                return;
            }
            
            addToCart(productId, name, price, image);
        }
    });

    // --- Contador del ícono del carrito ---
    async function updateCartIconCounter() {
        try {
            const cart = await getCart();
            const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
            const cartIcon = document.querySelector('.cart-icon');
            
            if (cartIcon) {
                let counter = cartIcon.querySelector('.cart-counter');
                if (totalItems > 0) {
                    if (!counter) {
                        counter = document.createElement('span');
                        counter.className = 'cart-counter';
                        cartIcon.appendChild(counter);
                    }
                    counter.textContent = totalItems;
                } else {
                    if (counter) counter.remove();
                }
            }
        } catch (e) {
            console.error('updateCartIconCounter() fallo', e);
        }
    }

    // --- Lógica de la página CARRITO.HTML ---
    const coupons = [
        { pointsRequired: 100, reward: "10% de Descuento", code: "KOKY10", type: 'percentage', value: 0.10 },
        { pointsRequired: 250, reward: "Envío Gratis", code: "ENVIOGRATIS", type: 'shipping', value: 0 },
        { pointsRequired: 400, reward: "15% de Descuento", code: "KOKY15", type: 'percentage', value: 0.15 },
        { pointsRequired: 600, reward: "S/ 20 de Regalo", code: "REGALO20", type: 'fixed', value: 20 },
        { pointsRequired: 800, reward: "25% de Descuento", code: "KOKYPRO", type: 'percentage', value: 0.25 }
    ];
    let appliedCoupon = null;

    async function renderCartPage() {
        const cartItemList = document.getElementById('cart-item-list');
        const cartEmptyMessage = document.getElementById('cart-empty-message');
        const cartSummaryBox = document.getElementById('cart-summary-box');
        const checkoutBtn = document.getElementById('checkout-btn');
        const cartProductCountHeader = document.getElementById('cart-product-count');

        if (!cartItemList) return; // No estamos en carrito.html

        const cart = await getCart();
        const totalProducts = cart.reduce((sum, item) => sum + item.quantity, 0);

        if (cartProductCountHeader) {
            cartProductCountHeader.textContent = `${totalProducts} producto${totalProducts !== 1 ? 's' : ''}`;
        }

        if (cart.length === 0) {
            if(cartEmptyMessage) cartEmptyMessage.style.display = 'flex';
            if(cartSummaryBox) cartSummaryBox.style.display = 'none';
            cartItemList.innerHTML = '';
        } else {
            if(cartEmptyMessage) cartEmptyMessage.style.display = 'none';
            if(cartSummaryBox) cartSummaryBox.style.display = 'block';

            cartItemList.innerHTML = '';
            cart.forEach(item => {
                const cartItemHTML = `
                    <div class="cart-item" data-id="${item.id}">
                        <img src="${item.image}" alt="${item.name}">
                        <div class="item-details">
                            <h3>${item.name}</h3>
                            <p>S/ ${item.price.toFixed(2)}</p>
                        </div>
                        <div class="item-quantity">
                            <button class="quantity-btn decrease-btn" disabled>-</button>
                            <input type="number" value="${item.quantity}" min="1" readonly class="quantity-input">
                            <button class="quantity-btn increase-btn" disabled>+</button>
                        </div>
                        <div class="item-actions">
                            <button class="action-menu-btn" disabled><i class="fa-solid fa-ellipsis-vertical"></i></button>
                        </div>
                    </div>
                `;
                cartItemList.innerHTML += cartItemHTML;
            });
            updateCartSummary();
            addCouponFunctionality();
        }

        if (checkoutBtn) {
            const newBtn = checkoutBtn.cloneNode(true);
            checkoutBtn.parentNode.replaceChild(newBtn, checkoutBtn);
            
            newBtn.addEventListener('click', async () => {
                const currentCart = await getCart();
                if (!currentCart || currentCart.length === 0) return alert('Tu carrito está vacío.');
                
                const paymentModal = document.getElementById('payment-modal');
                if (paymentModal) {
                    paymentModal.classList.add('active');
                    prefillPaymentForm();
                }
            });
        }
    }

    async function updateCartSummary() {
        const cart = await getCart();
        const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const productCount = cart.reduce((sum, item) => sum + item.quantity, 0);
        let shipping = subtotal > 0 ? 10.00 : 0;
        let discount = 0;

        const discountLine = document.getElementById('summary-discount-line');

        if (appliedCoupon) {
            if (appliedCoupon.type === 'percentage') {
                discount = subtotal * appliedCoupon.value;
            } else if (appliedCoupon.type === 'fixed') {
                discount = appliedCoupon.value;
            } else if (appliedCoupon.type === 'shipping') {
                shipping = 0;
                discount = 10.00;
            }
            if (discountLine) {
                discountLine.style.display = 'flex';
                document.getElementById('summary-discount').textContent = `-S/ ${discount.toFixed(2)}`;
            }
        } else {
            if (discountLine) discountLine.style.display = 'none';
        }

        const total = subtotal - discount + shipping;

        const summaryProductCount = document.getElementById('summary-product-count');
        const summarySubtotal = document.getElementById('summary-subtotal');
        const summaryShipping = document.getElementById('summary-shipping');
        const summaryTotal = document.getElementById('summary-total');

        if (summaryProductCount) summaryProductCount.textContent = productCount;
        if (summarySubtotal) summarySubtotal.textContent = `S/ ${subtotal.toFixed(2)}`;
        if (summaryShipping) summaryShipping.textContent = `S/ ${shipping.toFixed(2)}`;
        if (summaryTotal) summaryTotal.textContent = `S/ ${total.toFixed(2)}`;
    }

    function addCouponFunctionality() {
        const couponInput = document.getElementById('coupon-input');
        const applyBtn = document.getElementById('apply-coupon-btn');

        if (!applyBtn || !couponInput) return;

        const newApplyBtn = applyBtn.cloneNode(true);
        applyBtn.parentNode.replaceChild(newApplyBtn, applyBtn);

        newApplyBtn.addEventListener('click', () => {
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            const enteredCode = couponInput.value.trim().toUpperCase();

            if (!currentUser) return alert('Debes iniciar sesión para usar cupones.');

            const coupon = coupons.find(c => c.code === enteredCode);
            if (!coupon) {
                alert('El cupón ingresado no es válido.');
                appliedCoupon = null;
            } else if ((currentUser.points || 0) < coupon.pointsRequired) {
                alert(`No tienes suficientes puntos. Requiere ${coupon.pointsRequired}.`);
                appliedCoupon = null;
            } else {
                alert(`¡Cupón "${coupon.reward}" aplicado!`);
                appliedCoupon = coupon;
                couponInput.disabled = true;
                newApplyBtn.disabled = true;
                newApplyBtn.textContent = 'Aplicado';
            }
            updateCartSummary();
        });
    }

    function prefillPaymentForm() {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        if (currentUser) {
            const bName = document.getElementById('billing-name');
            const bLast = document.getElementById('billing-lastname');
            const bEmail = document.getElementById('billing-email');
            if(bName) bName.value = currentUser.name || '';
            if(bLast) bLast.value = currentUser.lastname || '';
            if(bEmail) bEmail.value = currentUser.email || '';
        }
    }

    // === Checkout ===
    const paymentForm = document.getElementById('payment-form');
    if (paymentForm) {
        const newForm = paymentForm.cloneNode(true);
        paymentForm.parentNode.replaceChild(newForm, paymentForm);

        newForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            console.log("Procesando pago...");
            
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            if (!currentUser) {
                alert('Inicia sesión para comprar.');
                return;
            }

            const paymentModal = document.getElementById('payment-modal');
            if(paymentModal) paymentModal.classList.remove('active');

            try {
                const USER_ID = getUserId();
                const resp = await fetch(`${API_BASE}/checkout`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId: Number(USER_ID) })
                });
                const data = await resp.json();
                
                if (!resp.ok) {
                    console.error('Error checkout:', data);
                    alert('Error en pago: ' + (data.error || 'Desconocido'));
                    return;
                }

                // =======================================================
                // === NUEVO: ACTUALIZAR PUNTOS EN LOCALSTORAGE ===
                // =======================================================
                // Recibimos 'puntosGanados' del backend o usamos el total como fallback
                const puntosGanados = data.puntosGanados || Math.floor(data.total);
                
                // Actualizamos el objeto en memoria y en localStorage
                currentUser.points = (currentUser.points || 0) + puntosGanados;
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                console.log(`¡Puntos actualizados! Nuevo total: ${currentUser.points}`);
                // =======================================================

                const lastOrderData = {
                    id: data.orderId,
                    date: new Date().toLocaleDateString(),
                    time: new Date().toLocaleTimeString(),
                    total: data.total,
                    items: data.items.map(i => ({ name: i.name, quantity: i.quantity, price: i.price })),
                    billingInfo: {
                        name: document.getElementById('billing-name')?.value || 'Cliente',
                        lastname: document.getElementById('billing-lastname')?.value || '',
                        email: document.getElementById('billing-email')?.value || '',
                        address: document.getElementById('billing-address')?.value || '',
                        city: document.getElementById('billing-city')?.value || '',
                        zip: document.getElementById('billing-zip')?.value || '',
                        country: 'Perú',
                        phone: document.getElementById('billing-phone')?.value || ''
                    },
                    documentType: document.getElementById('document-type')?.value || 'boleta',
                    documentDetails: {
                        dni: document.getElementById('document-dni')?.value || '',
                        ruc: document.getElementById('document-ruc')?.value || '',
                        razonSocial: document.getElementById('document-razon-social')?.value || ''
                    },
                    paymentMethod: document.getElementById('payment-method')?.value || 'Tarjeta',
                    subtotal: data.total - 10,
                    shipping: 10,
                    discount: 0
                };
                localStorage.setItem('lastOrder', JSON.stringify(lastOrderData));

                window.location.href = `confirmacion.html?orderId=${data.orderId}`;
            } catch (err) {
                console.error('Fallo checkout', err);
                alert('Error de conexión en checkout.');
            }
        });
    }

    updateCartIconCounter();
    
    if (window.location.pathname.includes('carrito.html')) {
        renderCartPage();
    }
});