// ==========================================================
// ===== DATOS CENTRALES DE LA TIENDA (PRODUCTOS Y CUPONES) =====
// ==========================================================

const allProducts = [
    // --- (Aquí va tu lista completa de más de 100 productos que ya tenemos en productos.js) ---
];

const coupons = [
    { pointsRequired: 100, reward: "10% de Descuento", code: "KOKY10", type: 'percentage', value: 0.10 },
    { pointsRequired: 250, reward: "Envío Gratis", code: "ENVIOGRATIS", type: 'shipping', value: 0 },
    { pointsRequired: 400, reward: "15% de Descuento", code: "KOKY15", type: 'percentage', value: 0.15 },
    { pointsRequired: 600, reward: "S/ 20 de Regalo", code: "REGALO20", type: 'fixed', value: 20 },
    { pointsRequired: 800, reward: "25% de Descuento", code: "KOKYPRO", type: 'percentage', value: 0.25 }
];