document.addEventListener('DOMContentLoaded', function () {
    console.log("script.js: Iniciado (Conectado a Base de Datos)");

    // URL del Backend
    const API_AUTH = 'http://72.61.9.249:9000/api/auth';

    // ===========================================================
    // ===== Lógica para los CARRUSELES (Mantenida) =====
    // ===========================================================
    const carousels = document.querySelectorAll('.product-carousel');
    carousels.forEach(carouselWrapper => {
        const carousel = carouselWrapper.querySelector('.carousel');
        const prevButton = carouselWrapper.querySelector('.carousel-btn.prev');
        const nextButton = carouselWrapper.querySelector('.carousel-btn.next');

        if (!carousel || !prevButton || !nextButton) return;

        nextButton.addEventListener('click', () => {
            const card = carousel.querySelector('.product-card');
            if (card) {
                const scrollAmount = card.offsetWidth + 20;
                carousel.scrollBy({ left: scrollAmount, behavior: 'smooth' });
            }
        });

        prevButton.addEventListener('click', () => {
            const card = carousel.querySelector('.product-card');
            if (card) {
                const scrollAmount = card.offsetWidth + 20;
                carousel.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
            }
        });
    });

    // ===========================================================
    // ===== LÓGICA DE AUTENTICACIÓN (CONECTADA A BD) =====
    // ===========================================================

    // --- Elementos del DOM ---
    const userMenuLoggedOut = document.getElementById('user-menu-logged-out');
    const userMenuLoggedIn = document.getElementById('user-menu-logged-in');
    const welcomeMessage = document.getElementById('welcome-message');
    const logoutBtn = document.getElementById('logout-btn');
    const authModal = document.getElementById('auth-modal');
    
    // Formularios
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const forgotForm = document.getElementById('forgot-form');

    // Vistas del Modal
    const loginView = document.getElementById('login-view');
    const registerView = document.getElementById('register-view');
    const forgotView = document.getElementById('forgot-view');

    // Botones de navegación del Modal
    const btnForgotPass = document.getElementById('btn-forgot-pass');
    const btnBackToLogin = document.getElementById('back-to-login');

    // --- 1. Lógica de UI (Mostrar/Ocultar menú según estado) ---
    function updateUI() {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        if (currentUser) {
            // Usuario logueado
            if (userMenuLoggedOut) userMenuLoggedOut.style.display = 'none';
            if (userMenuLoggedIn) userMenuLoggedIn.style.display = 'block';
            
            // Mostrar nombre y puntos
            if (welcomeMessage) {
                welcomeMessage.innerHTML = `Hola, <br><b>${currentUser.name}</b><small>${currentUser.points || 0} puntos</small>`;
            }
        } else {
            // Usuario no logueado
            if (userMenuLoggedOut) userMenuLoggedOut.style.display = 'block';
            if (userMenuLoggedIn) userMenuLoggedIn.style.display = 'none';
        }
    }

    // --- 2. REGISTRO (POST a /api/auth/register) ---
    if (registerForm) {
        registerForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            
            const name = document.getElementById('register-name').value;
            const email = document.getElementById('register-email').value;
            const password = document.getElementById('register-password').value;

            try {
                const response = await fetch(`${API_AUTH}/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password })
                });

                const data = await response.json();

                if (response.ok) {
                    alert('¡Cuenta creada exitosamente en la Base de Datos! Por favor inicia sesión.');
                    openModal('login');
                } else {
                    alert('Error al registrar: ' + (data.error || 'Datos inválidos.'));
                }
            } catch (error) {
                console.error("Error registro:", error);
                alert("Error de conexión con el servidor.");
            }
        });
    }

    // --- 3. LOGIN (POST a /api/auth/login) ---
    // *** AQUÍ ESTÁ EL CAMBIO IMPORTANTE PARA GUARDAR EL ROL ***
    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;

            try {
                const response = await fetch(`${API_AUTH}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();

                if (response.ok) {
                    // Guardamos TODA la información importante, incluyendo ROL y TOKEN
                    const userSession = {
                        dbUserId: data.id, 
                        name: data.name,
                        lastname: data.lastname,
                        email: data.email,
                        points: data.points,
                        role: data.role,   // <--- ¡IMPORTANTE! Guardamos el rol (ADMIN/USER)
                        token: data.token  // <--- ¡IMPORTANTE! Guardamos el token JWT
                    };
                    
                    localStorage.setItem('currentUser', JSON.stringify(userSession));
                    
                    updateUI();
                    if(authModal) authModal.classList.remove('active');
                    
                    // Si es admin, podrías redirigir, o simplemente recargar
                    window.location.reload(); 
                } else {
                    alert('Login fallido: ' + (data.error || 'Credenciales incorrectas'));
                }
            } catch (error) {
                console.error("Error login:", error);
                alert("Error de conexión con el servidor.");
            }
        });
    }

    // --- 4. RECUPERAR CONTRASEÑA (POST a /api/auth/forgot-password) ---
    if (forgotForm) {
        forgotForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('forgot-email').value;
            const btnSubmit = forgotForm.querySelector('button');
            const originalText = btnSubmit.textContent;
            
            btnSubmit.textContent = "Enviando...";
            btnSubmit.disabled = true;

            try {
                const res = await fetch(`${API_AUTH}/forgot-password`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email })
                });
                
                const data = await res.json();

                if (res.ok) {
                    alert(data.message);
                    openModal('login');
                } else {
                    alert(data.error || "Error al enviar el correo.");
                }
            } catch (error) {
                console.error(error);
                alert("Error de conexión.");
            } finally {
                btnSubmit.textContent = originalText;
                btnSubmit.disabled = false;
            }
        });
    }

    // --- 5. NAVEGACIÓN DEL MODAL (Login / Registro / Recuperar) ---
    function openModal(view) {
        if (!authModal) return;
        
        if(loginView) loginView.style.display = 'none';
        if(registerView) registerView.style.display = 'none';
        if(forgotView) forgotView.style.display = 'none';

        if (view === 'register') {
            if(registerView) registerView.style.display = 'block';
        } else if (view === 'forgot') {
            if(forgotView) forgotView.style.display = 'block';
        } else {
            if(loginView) loginView.style.display = 'block';
        }
        authModal.classList.add('active');
    }

    document.querySelectorAll('.open-login-modal').forEach(b => b.addEventListener('click', (e) => { e.preventDefault(); openModal('login'); }));
    document.querySelectorAll('.open-register-modal').forEach(b => b.addEventListener('click', (e) => { e.preventDefault(); openModal('register'); }));
    
    document.getElementById('show-register-view')?.addEventListener('click', (e) => { e.preventDefault(); openModal('register'); });
    document.getElementById('show-login-view')?.addEventListener('click', (e) => { e.preventDefault(); openModal('login'); });
    
    if (btnForgotPass) {
        btnForgotPass.addEventListener('click', (e) => { e.preventDefault(); openModal('forgot'); });
    }
    if (btnBackToLogin) {
        btnBackToLogin.addEventListener('click', (e) => { e.preventDefault(); openModal('login'); });
    }

    const closeBtn = authModal ? authModal.querySelector('.modal-close') : null;
    if (closeBtn) closeBtn.addEventListener('click', () => authModal.classList.remove('active'));
    if (authModal) authModal.addEventListener('click', (e) => { if(e.target === authModal) authModal.classList.remove('active'); });


    // --- 6. CERRAR SESIÓN ---
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function (event) {
            event.preventDefault();
            localStorage.removeItem('currentUser'); 
            window.location.href = 'index.html';
        });
    }

    // --- Inicializar UI ---
    updateUI();

    // ===========================================================
    // ===== LÓGICA DE BÚSQUEDA =====
    // ===========================================================
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-input');

    if (searchForm) {
        searchForm.addEventListener('submit', (event) => {
            event.preventDefault();
            const searchTerm = searchInput.value.trim();
            if (searchTerm) {
                window.location.href = `busqueda.html?q=${encodeURIComponent(searchTerm)}`;
            }
        });
    }

    // ===========================================================
    // ===== LÓGICA DEL MENÚ DESPLEGABLE =====
    // ===========================================================
    const menuTriggers = document.querySelectorAll('.user-menu-trigger');

    menuTriggers.forEach(trigger => {
        trigger.addEventListener('click', function(e) {
            e.stopPropagation(); // Evitar que se cierre inmediatamente
            
            const parentMenu = this.parentElement; // El div .user-menu

            // Cerrar otros menús abiertos si los hubiera
            document.querySelectorAll('.user-menu').forEach(menu => {
                if (menu !== parentMenu) {
                    menu.classList.remove('active');
                }
            });

            // Abrir/Cerrar este menú
            parentMenu.classList.toggle('active');
        });
    });

    // Cerrar menú al hacer clic fuera
    document.addEventListener('click', () => {
        document.querySelectorAll('.user-menu').forEach(menu => {
            menu.classList.remove('active');
        });
    });
});