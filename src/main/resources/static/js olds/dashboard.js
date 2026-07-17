document.addEventListener('DOMContentLoaded', function() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (!currentUser) {
        window.location.href = 'index.html';
        return;
    }

    // --- Definición de los Cupones ---
    const coupons = [
        { pointsRequired: 100, reward: "10% de Descuento", code: "KOKY10" },
        { pointsRequired: 250, reward: "Envío Gratis", code: "ENVIOGRATIS" },
        { pointsRequired: 400, reward: "15% de Descuento", code: "KOKY15" },
        { pointsRequired: 600, reward: "S/ 20 de Regalo", code: "REGALO20" },
        { pointsRequired: 800, reward: "25% de Descuento", code: "KOKYPRO" }
    ];

    const pointsCircle = document.getElementById('points-circle');
    const currentPointsEl = document.getElementById('current-points');
    const pointsGoalTextEl = document.getElementById('points-goal-text');
    const couponsContainer = document.getElementById('coupons-container');

    // SOLUCIÓN DEL ERROR: Verificar si estamos en la página de puntos
    if (!pointsCircle || !currentPointsEl) {
        console.log("No estamos en la página de puntos, omitiendo renderizado del dashboard.");
        return; 
    }

    // --- Función para encontrar el próximo objetivo de puntos ---
    function findNextGoal(userPoints) {
        for (const coupon of coupons) {
            if (userPoints < coupon.pointsRequired) {
                return coupon.pointsRequired;
            }
        }
        return coupons[coupons.length - 1].pointsRequired; 
    }
    
    // --- Función para renderizar el círculo de progreso ---
    function renderPointsCircle(user) {
        const nextGoal = findNextGoal(user.points || 0);
        const pointsForDisplay = Math.min(user.points || 0, nextGoal);
        const percentage = (pointsForDisplay / nextGoal) * 100;
        
        currentPointsEl.textContent = user.points || 0;
        pointsGoalTextEl.textContent = `/ ${nextGoal} Puntos`;
        pointsCircle.style.setProperty('--points-percentage', percentage + '%');
    }

    // --- Función para renderizar la lista de cupones ---
    function renderCoupons(user) {
        if(!couponsContainer) return;
        couponsContainer.innerHTML = ''; 
        
        coupons.forEach(coupon => {
            const isUnlocked = (user.points || 0) >= coupon.pointsRequired;
            const lockClass = isUnlocked ? '' : 'locked';
            
            const couponHTML = `
                <div class="coupon-card ${lockClass}">
                    <div class="coupon-reward-text">${coupon.reward}</div>
                    <div class="coupon-points-req">Requiere ${coupon.pointsRequired} Puntos</div>
                    <div class="coupon-code-display">
                        ${isUnlocked ? coupon.code : 'BLOQUEADO'}
                    </div>
                </div>
            `;
            couponsContainer.innerHTML += couponHTML;
        });
    }

    renderPointsCircle(currentUser);
    renderCoupons(currentUser);
});