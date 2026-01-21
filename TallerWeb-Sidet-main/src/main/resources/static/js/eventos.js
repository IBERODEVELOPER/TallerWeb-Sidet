    function initEventos() {
        
    const modal = document.getElementById("modal1");
    const finishWorkBtn = document.getElementById("finishWork");


    if (modal) {
        modal.addEventListener("show.bs.modal", cargarEventos);
    }

    if (finishWorkBtn) {
        finishWorkBtn.addEventListener("click", finalizarEvento);
    }
}

document.addEventListener("DOMContentLoaded", initEventos);



    // === Función: cargar los eventos dinámicos en el modal ===
    function cargarEventos() {
    console.log("Entrando a la función cargarEventos()");
    const modal = document.getElementById("modal1"); // ✅ importante
    const select = modal.querySelector('select.form-select');
    const modalTitle = document.getElementById('modal1Label');
    const modalDescription = document.getElementById('modalEventDescription');

    fetch('/event/listevents')
        .then(response => response.json())
        .then(data => {
            console.log("Datos recibidos del servidor:", data);

            if (!select || !modalTitle || !modalDescription) {
                console.error("⚠️ No se encontró uno de los elementos del modal.");
                return;
            }

            // Limpiar select
            select.innerHTML = '<option selected>[Seleccione subtipo]</option>';

            // Insertar opciones
            data.forEach(event => {
                const option = document.createElement('option');
                option.value = event.id;
                option.textContent = event.nameEvent;
                option.dataset.tipoEvento = event.tipoEvento;
                select.appendChild(option);
            });

            console.log("✅ Opciones insertadas en el select:", select.options.length);

            // Asignar listener al select (una sola vez)
            select.removeEventListener("change", onSelectChange);
            select.addEventListener("change", onSelectChange);

            function onSelectChange() {
                const selected = select.options[select.selectedIndex];
                modalTitle.textContent = selected.dataset.tipoEvento || "Evento";
                modalDescription.textContent = `¿Desea iniciar el evento "${selected.textContent}"?`;
            }
        })
        .catch(err => console.error("Error al cargar eventos:", err));
}


    // === Botón principal del modal ===
    const startEventBtn = modal ? modal.querySelector(".btn-primary") : null;
    if (startEventBtn) {
        startEventBtn.addEventListener("click", iniciarEvento);
    }

    // === Función para iniciar evento ===
    function iniciarEvento() {
        const select = modal.querySelector("select.form-select");
        if (!select || select.selectedIndex <= 0) {
            return swal({ title: "Advertencia", text: "Debe seleccionar un subtipo antes de iniciar.", icon: "warning", button: "OK" });
        }

        const option = select.options[select.selectedIndex];
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const authenticatedUser = document.getElementById("authenticatedUser").value;

        const body = {
            activityId: option.value,
            username: authenticatedUser,
            startTime: new Date().toLocaleString('es-PE', { timeZone: 'America/Lima', hour12: false })
        };

        fetch('/api/events/start-activity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(body)
        })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    swal({
                        title: '¡Actividad iniciada!',
                        text: 'El evento ha comenzado exitosamente.',
                        icon: 'success',
                        timer: 1000,
                        buttons: false
                    }).then(() => {
                        sessionStorage.setItem('eventId', option.value);
                        sessionStorage.setItem('eventName', option.textContent);
                        sessionStorage.setItem('tipoEvento', option.dataset.tipoEvento);
                        const modalInstance = bootstrap.Modal.getInstance(modal);
                        modalInstance.hide();
                        window.location.href = '/index';
                    });
                } else {
                    swal({
                        title: 'Error',
                        text: data.message || 'Ya tienes una actividad en curso.',
                        icon: 'error',
                        button: 'OK'
                    });
                }
            })
            .catch(error => {
                console.error('Error al iniciar la actividad:', error);
                swal({
                    title: 'Error',
                    text: 'Ocurrió un error al iniciar la actividad.',
                    icon: 'error',
                    button: 'OK'
                });
            });
    }

    // === Función: finalizar trabajo del día ===
    function finalizarJornada() {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch('/api/events/register-outwork', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    swal({
                        title: '¡Salida Registrada!',
                        text: 'Se ha registrado su hora de salida correctamente.',
                        icon: 'success',
                        timer: 1000,
                        buttons: false
                    }).then(() => {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = '/logout';
                        const inputCsrf = document.createElement('input');
                        inputCsrf.type = 'hidden';
                        inputCsrf.name = '_csrf';
                        inputCsrf.value = csrfToken;
                        form.appendChild(inputCsrf);
                        document.body.appendChild(form);
                        form.submit();
                    });
                } else {
                    swal({
                        title: 'Error',
                        text: data.message,
                        icon: 'error',
                        button: 'OK'
                    });
                }
            })
            .catch(error => {
                console.error('Error al finalizar jornada:', error);
                swal({
                    title: 'Error',
                    text: 'Ocurrió un error al intentar registrar la hora de salida.',
                    icon: 'error',
                    button: 'OK'
                });
            });
    }




