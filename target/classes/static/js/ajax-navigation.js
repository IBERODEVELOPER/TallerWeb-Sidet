document.addEventListener("DOMContentLoaded", function() {

document.addEventListener("click", function(event) {
    // Buscamos si el clic fue en un .ajax-link o dentro de uno (por los iconos)
    const link = event.target.closest(".ajax-link");

    if (link) {
        event.preventDefault();
        const url = link.getAttribute("href");
        const content = document.getElementById("content");

        // Loader
        content.innerHTML = `
            <div class="text-center p-5">
                <div class="spinner-border text-primary"></div>
                <p class="mt-2">Cargando...</p>
            </div>`;

        fetch(url, {
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(response => response.text())
        .then(html => {
            // Actualizar URL en el navegador
            window.history.pushState({ path: url }, '', url);

            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const nuevoContenido = doc.querySelector('#pantll');

            if (nuevoContenido) {
                content.innerHTML = nuevoContenido.innerHTML;
            } else {
                content.innerHTML = html;
            }

            // Re-ejecutar inicializaciones
            initFilePreview();
            if (typeof initEventos === 'function') initEventos();
        })
        .catch(error => {
            console.error(error);
            Swal.fire("Error", "No se pudo cargar el contenido", "error");
        });
    }
});

document.addEventListener("submit", function(event) {

    const form = event.target;

    if (form.tagName === 'FORM' && !form.hasAttribute('data-no-ajax')) {
        event.preventDefault(); // Detener envío tradicional

        const formData = new FormData(form);
        const url = form.action;
        const content = document.getElementById("content");

        // Mostrar loader
        content.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-primary"></div></div>';

        fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (response.redirected) {
                // Si el servidor redirige (al listado), seguimos la redirección por AJAX
                return fetch(response.url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } }).then(r => r.text());
            }
            return response.text();
        })
        .then(html => {
            // Extraer solo el fragmento necesario para evitar duplicar menús
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const nuevoContenido = doc.querySelector('#pantll');

            if (nuevoContenido) {
                content.innerHTML = nuevoContenido.innerHTML;
            } else {
                content.innerHTML = html;
            }

            // RE-INICIALIZAR SCRIPTS (Importante)
            initFilePreview();
            if (typeof initEventos === 'function') initEventos();

            // Mostrar mensaje de éxito con SweetAlert si es necesario
            Swal.fire("¡Logrado!", "Proceso finalizado con éxito", "success");
        })
        .catch(error => {
            console.error("Error:", error);
            Swal.fire("Error", "No se pudo procesar el formulario", "error");
        });
    }
});
    // Inicializar vista previa en carga inicial (por si existe en la página)
    initFilePreview();

    toggleUserSelection();

    window.onpopstate = function(event) {
        // Forzamos la recarga de la página para que el Layout se cargue correctamente
        if (event.state && event.state.path) {
            window.location.href = event.state.path;
        } else {
            window.location.reload();
        }
    };

});

function initFilePreview() {
    const inputFile = document.getElementById("formFile");
    const imgAvatar = document.getElementById("imgAvatar");

    if (inputFile && imgAvatar) {
        inputFile.addEventListener("change", function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    imgAvatar.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

 function toggleUserSelection() {
     const userTypeElement = document.getElementById("userType");
     const employeeSection = document.getElementById("employeeSection");
     const customerSection = document.getElementById("customerSection");

        // Si no existen los elementos, salir de la función sin hacer nada
    if (!userTypeElement || !employeeSection || !customerSection) {
        return;
    }
    const userType = userTypeElement.value;

    employeeSection.style.display = (userType === "Empleado") ? "block" : "none";
    customerSection.style.display = (userType === "Cliente") ? "block" : "none";

    }
