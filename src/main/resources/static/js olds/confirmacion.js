// --- START OF FILE js olds/confirmacion.js ---

document.addEventListener('DOMContentLoaded', () => {
    // La lógica de script.js (para el header, login/logout, etc.) y carrito.js (para el modal de "añadir al carrito")
    // ya debería estar enlazada en el HTML y ejecutándose independientemente.

    const lastOrder = JSON.parse(localStorage.getItem('lastOrder'));

    // Si no hay una última orden en localStorage, mostrar un mensaje de error y redirigir
    if (!lastOrder) {
        console.error('No se encontró información de la última orden. Redirigiendo al inicio.');
        const confirmationCard = document.querySelector('.confirmation-card');
        if (confirmationCard) {
            confirmationCard.innerHTML = `
                <i class="fa-solid fa-circle-exclamation error-icon"></i>
                <h2>¡Lo sentimos!</h2>
                <p>No se encontraron detalles de una compra reciente. Por favor, realiza una compra.</p>
                <div class="confirmation-actions">
                    <a href="index.html" class="btn btn-primary">Volver a la Tienda</a>
                </div>
            `;
            // Ocultar elementos adicionales si el .order-details-summary y .confirmation-actions no están directamente dentro de .confirmation-card
            const orderSummarySection = document.querySelector('.order-details-summary');
            if (orderSummarySection) orderSummarySection.style.display = 'none';
        }
        return;
    }

    // --- Mostrar detalles de la orden en la página ---
    document.getElementById('order-id-display').textContent = lastOrder.id;
    document.getElementById('order-date-display').textContent = `${lastOrder.date} ${lastOrder.time}`; // Muestra fecha y hora
    document.getElementById('order-total-display').textContent = `S/ ${lastOrder.total.toFixed(2)}`;

    const orderItemsList = document.getElementById('order-items-list');
    orderItemsList.innerHTML = ''; // Limpiar cualquier contenido previo
    lastOrder.items.forEach(item => {
        const listItem = document.createElement('li');
        listItem.innerHTML = `
            <span>${item.name} (x${item.quantity})</span>
            <span>S/ ${(item.price * item.quantity).toFixed(2)}</span>
        `;
        orderItemsList.appendChild(listItem);
    });

    // --- Mostrar información de facturación ---
    document.getElementById('billing-name-display').textContent = lastOrder.billingInfo.name;
    document.getElementById('billing-lastname-display').textContent = lastOrder.billingInfo.lastname;
    document.getElementById('billing-email-display').textContent = lastOrder.billingInfo.email;

    let fullAddress = lastOrder.billingInfo.address;
    if (lastOrder.billingInfo.address2) {
        fullAddress += `, ${lastOrder.billingInfo.address2}`;
    }
    fullAddress += `, ${lastOrder.billingInfo.city}, ${lastOrder.billingInfo.zip}, ${lastOrder.billingInfo.country}`;
    document.getElementById('billing-address-display').textContent = fullAddress;

    // Actualizar el texto del documento y el botón de descarga
    document.getElementById('document-type-display').textContent = lastOrder.documentType === 'boleta' ? 'DNI' : 'RUC';
    document.getElementById('download-document-type').textContent = lastOrder.documentType === 'boleta' ? 'Boleta' : 'Factura';

    if (lastOrder.documentType === 'boleta') {
        document.getElementById('document-id-display').textContent = lastOrder.documentDetails.dni;
        document.getElementById('document-razon-social-display').style.display = 'none'; // Asegurarse de ocultar si es boleta
    } else { // Factura
        document.getElementById('document-id-display').textContent = lastOrder.documentDetails.ruc;
        const razonSocialDisplay = document.getElementById('document-razon-social-display');
        razonSocialDisplay.textContent = `Razón Social: ${lastOrder.documentDetails.razonSocial}`;
        razonSocialDisplay.style.display = 'block';
    }


    // --- Lógica para el botón de descarga del documento ---
    const downloadBtn = document.getElementById('download-document-btn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', () => {
            generateAndDownloadDocument(lastOrder);
        });
    }

    // --- Función para generar y descargar el documento (Boleta/Factura) ---
    function generateAndDownloadDocument(order) {
        // Asegúrate de que jsPDF y su plugin autoTable estén cargados en el HTML
        if (typeof window.jspdf === 'undefined' || typeof window.jspdf.jsPDF === 'undefined') {
            alert('Error: La librería jsPDF no está cargada. No se puede generar el documento.');
            console.error('jsPDF no encontrado. Asegúrate de que jspdf.umd.min.js y jspdf.plugin.autotable.min.js estén cargados en el HTML.');
            return;
        }

        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        const margin = 15;
        let y = margin;
        const lineHeight = 7;
        const titleFontSize = 18;
        const headerFontSize = 12;
        const bodyFontSize = 10;
        const footerFontSize = 8;
        const pageWidth = doc.internal.pageSize.getWidth();

        // Título del documento
        doc.setFontSize(titleFontSize);
        doc.text(`KOKY - ${order.documentType === 'boleta' ? 'Boleta de Venta' : 'Factura'}`, pageWidth / 2, y, { align: 'center' });
        y += lineHeight * 1.5;

        // Información de la empresa (KOKY)
        doc.setFontSize(bodyFontSize);
        doc.text("Razón Social: KOKY S.A.C.", margin, y); y += lineHeight;
        doc.text("RUC: 20XXXXXXXXX", margin, y); y += lineHeight; // Reemplaza con el RUC real de KOKY
        doc.text("Dirección: Av. Real 123, Huancayo, Perú", margin, y); y += lineHeight * 1.5;

        // Información de la orden
        doc.setFontSize(headerFontSize);
        doc.text("Detalles de la Orden:", margin, y); y += lineHeight;
        doc.setFontSize(bodyFontSize);
        doc.text(`ID de Orden: ${order.id}`, margin, y); y += lineHeight;
        doc.text(`Fecha y Hora: ${order.date} ${order.time}`, margin, y); y += lineHeight * 1.5;

        // Información del cliente / facturación
        doc.setFontSize(headerFontSize);
        doc.text("Información del Cliente:", margin, y); y += lineHeight;
        doc.setFontSize(bodyFontSize);
        doc.text(`Nombre: ${order.billingInfo.name} ${order.billingInfo.lastname}`, margin, y); y += lineHeight;
        doc.text(`Email: ${order.billingInfo.email}`, margin, y); y += lineHeight;
        doc.text(`Dirección: ${order.billingInfo.address}${order.billingInfo.address2 ? ', ' + order.billingInfo.address2 : ''}, ${order.billingInfo.city}, ${order.billingInfo.zip}, ${order.billingInfo.country}`, margin, y); y += lineHeight;
        doc.text(`Teléfono: ${order.billingInfo.phone}`, margin, y); y += lineHeight;

        if (order.documentType === 'boleta') {
            doc.text(`DNI: ${order.documentDetails.dni}`, margin, y); y += lineHeight * 1.5;
        } else { // Factura
            doc.text(`RUC: ${order.documentDetails.ruc}`, margin, y); y += lineHeight;
            doc.text(`Razón Social: ${order.documentDetails.razonSocial}`, margin, y); y += lineHeight * 1.5;
        }

        // Tabla de productos
        doc.setFontSize(headerFontSize);
        doc.text("Productos:", margin, y); y += lineHeight;

        const tableHeaders = ["Producto", "Cantidad", "Precio Unit.", "Subtotal"];
        const tableData = order.items.map(item => [
            item.name,
            item.quantity,
            `S/ ${item.price.toFixed(2)}`,
            `S/ ${(item.price * item.quantity).toFixed(2)}`
        ]);

        doc.autoTable({
            startY: y,
            head: [tableHeaders],
            body: tableData,
            margin: { left: margin, right: margin },
            theme: 'striped',
            headStyles: { fillColor: [240, 240, 240], textColor: [0, 0, 0] },
            styles: { fontSize: bodyFontSize, cellPadding: 2, overflow: 'linebreak' },
            columnStyles: {
                0: { cellWidth: 70 }, // Producto
                1: { cellWidth: 20, halign: 'center' }, // Cantidad
                2: { cellWidth: 30, halign: 'right' }, // Precio Unit.
                3: { cellWidth: 30, halign: 'right' }  // Subtotal
            },
            didDrawPage: function (data) {
                // Si la tabla supera una página, ajusta 'y' para el pie de página en la siguiente
                y = data.cursor.y;
            }
        });
        // Actualizar 'y' después de la tabla, usando la posición final de la tabla
        y = doc.autoTable.previous.finalY + lineHeight;

        // Resumen de totales
        doc.setFontSize(bodyFontSize);
        doc.text(`Subtotal: S/ ${order.subtotal.toFixed(2)}`, pageWidth - margin, y, { align: 'right' }); y += lineHeight;
        if (order.discount > 0) {
            doc.text(`Descuento: -S/ ${order.discount.toFixed(2)}`, pageWidth - margin, y, { align: 'right' }); y += lineHeight;
        }
        doc.text(`Envío: S/ ${order.shipping.toFixed(2)}`, pageWidth - margin, y, { align: 'right' }); y += lineHeight;
        doc.setFontSize(headerFontSize);
        doc.text(`Total: S/ ${order.total.toFixed(2)}`, pageWidth - margin, y, { align: 'right' }); y += lineHeight * 2;

        // Método de pago
        doc.setFontSize(bodyFontSize);
        doc.text(`Método de Pago: ${order.paymentMethod.toUpperCase()}`, margin, y); y += lineHeight * 2;


        // Pie de página
        doc.setFontSize(footerFontSize);
        doc.text("¡Gracias por tu compra en KOKY!", pageWidth / 2, doc.internal.pageSize.getHeight() - margin, { align: 'center' });


        // Guardar el PDF
        const filename = `${order.documentType === 'boleta' ? 'Boleta' : 'Factura'}_KOKY_${order.id}.pdf`;
        doc.save(filename);
    }

    // Opcional: Limpiar lastOrder de localStorage después de un tiempo
    // Esto es útil para que si el usuario regresa a la página de confirmación más tarde,
    // no vea una orden antigua. Puedes ajustar el tiempo o comentarlo si prefieres.
    // setTimeout(() => {
    //     localStorage.removeItem('lastOrder');
    // }, 5 * 60 * 1000); // Eliminar después de 5 minutos

});

// --- END OF FILE js olds/confirmacion.js ---