document.addEventListener('DOMContentLoaded', () => {
    // --- Lógica para Animaciones al Hacer Scroll ---
    const fadeElements = document.querySelectorAll('.fade-in-section');

    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    fadeElements.forEach(el => observer.observe(el));


    // --- Lógica para el Contador Animado ---
    const statsSection = document.querySelector('.stats-section');
    const counters = document.querySelectorAll('.stat-number');
    let hasCounted = false;

    function runCounters() {
        counters.forEach(counter => {
            const target = +counter.dataset.target;
            let current = 0;
            const duration = 2000;
            const increment = target / (duration / 16);

            const updateCount = () => {
                current += increment;
                if (current < target) {
                    counter.innerText = Math.ceil(current).toLocaleString('es-PE');
                    requestAnimationFrame(updateCount);
                } else {
                    counter.innerText = target.toLocaleString('es-PE');
                }
            };
            requestAnimationFrame(updateCount);
        });
    }

    const counterObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !hasCounted) {
                runCounters();
                hasCounted = true;
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5 });

    if (statsSection) {
        counterObserver.observe(statsSection);
    }
});