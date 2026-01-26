document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".ajax-link").forEach(link => {
        link.addEventListener("click", function(event) {
            event.preventDefault(); //evita la carga de la pagina
            const url = this.getAttribute("href");
            const content = document.getElementById("content");

            // Loader
            content.innerHTML = `
                <div class="text-center p-5">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2">Cargando...</p>
                </div>`;

            fetch(url)
                .then(response => response.text())
                .then(html => {
                    content.innerHTML = html;

                    // Reasignar eventos después de cargar contenido AJAX
                    initFilePreview(); 
                    if (typeof initEventos === 'function') {
                        initEventos();
                    }
                })
                .catch(error => {
                    console.error(error);
                    Swal.fire("Error", "No se pudo cargar el contenido", "error");
                });
        });
    });

    // Inicializar vista previa en carga inicial (por si existe en la página)
    initFilePreview();

    toggleUserSelection();

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
