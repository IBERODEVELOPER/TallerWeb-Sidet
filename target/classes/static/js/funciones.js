$(document).ready(function() {
	var messagesHidden = false;
	function hideShowContainers() {
		if (messagesHidden) return;
		var successMessage = $('#message1');
		var errorMessage = $('#message2');
		var warningMessage = $('#message3');
		var infoMessage = $('#message4');

		// Ocultar mensajes después de 5 segundos
		setTimeout(function() {
			if (successMessage.is(':visible')) {
				successMessage.hide(3000, function() {
					successMessage.addClass('hidden');
				});
			}
			if (errorMessage.is(':visible')) {
				errorMessage.hide(3000, function() {
					errorMessage.addClass('hidden');
				});
			}
			if (warningMessage.is(':visible')) {
				warningMessage.fadeOut(3000, function() {
					warningMessage.addClass('hidden');
				});
			}
			if (infoMessage.is(':visible')) {
				infoMessage.hide(3000, function() {
					infoMessage.addClass('hidden');
				});
			}
			messagesHidden = true;
		}, 5000);

	};
	// Ejecutar la función para ocultar los mensajes
	hideShowContainers();
});

function togglePasswordVisibility(passwordFieldId, toggleIcon) {
	const passwordField = document.getElementById(passwordFieldId);
	const isPasswordVisible = passwordField.type === 'text';

	passwordField.type = isPasswordVisible ? 'password' : 'text';
	toggleIcon.classList.toggle('fa-eye-slash', isPasswordVisible);
	toggleIcon.classList.toggle('fa-eye', !isPasswordVisible);
}

function validateForm() {
	const newPassword = document.getElementById('newPassword').value;
	const repeatPassword = document.getElementById('repeatPassword').value;

	const newPasswordError = document.getElementById('newPasswordError');
	const repeatPasswordError = document.getElementById('repeatPasswordError');

	// Limpiar mensajes de error
	newPasswordError.textContent = '';
	repeatPasswordError.textContent = '';

	// Reglas de complejidad de la contraseña (puedes modificar estas reglas según tus necesidades)
	const minLength = 8;
	const hasUpperCase = /[A-Z]/.test(newPassword);
	const hasLowerCase = /[a-z]/.test(newPassword);
	const hasNumber = /[0-9]/.test(newPassword);
	const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(newPassword);

	let valid = true;

	if (newPassword.length < minLength || !hasUpperCase || !hasLowerCase || !hasNumber || !hasSpecialChar) {
		newPasswordError.textContent = 'La contraseña debe tener al menos 8 caracteres, incluyendo una letra mayúscula, una letra minúscula, un número y un carácter especial !@#$%^&*(),.?":{}|<>.';
		valid = false;
	}

	if (newPassword !== repeatPassword) {
		repeatPasswordError.textContent = 'Las contraseñas no coinciden.';
		valid = false;
	}

	return valid;
}

//actualizar la foto del usuario autentiocado
function updateProfilePicture(newPhotoUrl) {
    document.querySelector('.rounded-circle').src = newPhotoUrl;
}

function QuestionDeleteByIdPerson(id) {
	console.log(id);
	swal({
		title: "¿Estás seguro de eliminar este registro?",
		text: "¡Una vez eliminado no se podrá restablecer!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				$.ajax({
					url: "/peoples/deleteByIdPerson/" + id,
					method: "GET",
					dataType: "json",  // Esperamos una respuesta JSON
					success: function(res) {
						console.log(res);
						swal(res.message, {
							icon: "success",
						}).then((ok) => {
							if (ok) {
								location.href = "/peoples/listPeople";
							}
						});
					},
					error: function(xhr) {
						let errorMessage = "Hubo un problema al eliminar el registro.";
						if (xhr.responseJSON && xhr.responseJSON.message) {
							errorMessage = xhr.responseJSON.message;
						}
						swal("Error", errorMessage, "error");
					}
				});
			}
		});
}



function QuestionDeleteByIdUser(id) {
	console.log(id);
	swal({
		title: "¿Estas seguro de eliminar el usuario?",
		text: "¡Una vez eliminado no se prodra restablecer!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				$.ajax({
					url: "/user/deleteByIdUser/" + id,
					success: function(res) {
						console.log(res);
					},
				});
				swal("UPS! Registro eliminado!", {
					icon: "success",
				}).then((ok) => {
					if (ok) {
						location.href = "/user/listUsers";
					}
				});
			}
		});
}

function QuestionEditPeople(id) {
	console.log(id);
	swal({
		title: "¿Deseas editar los datos del Cliente?",
		text: "¡Existen algunos datos sensible, por favor solicita permiso!",
		icon: "info",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				location.href = "/peoples/formPeople/" + id;
			}
		});
}

function QuestionEditPeople(id) {
    const url = "/peoples/formPeople/" + id;
    const content = document.getElementById("content");

    swal({
        title: "¿Deseas editar los datos del Cliente?",
        text: "¡Existen algunos datos sensibles, por favor solicita permiso!",
        icon: "info",
        buttons: ["Cancelar", "Sí, editar"], // Define los nombres de los botones
        dangerMode: false,
    })
    .then((willEdit) => {
        // willEdit será true si el usuario hizo clic en "Sí, editar"
        if (willEdit) {
            // Mostramos un loader mientras carga
            content.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-primary"></div></div>';

            fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
                .then(response => {
                    if (!response.ok) throw new Error("Error en la respuesta del servidor");
                    return response.text();
                })
                .then(html => {
                    window.history.pushState({ path: url }, '', url);
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const nuevoContenido = doc.querySelector('#pantll');

                    content.innerHTML = nuevoContenido ? nuevoContenido.innerHTML : html;

                    // Volver a activar funciones de la vista cargada
                    initFilePreview();
                    if (typeof initEventos === 'function') initEventos();
                })
                .catch(error => {
                    console.error("Error AJAX:", error);
                    swal("Error", "No se pudo cargar el formulario", "error");
                });
        }
    });
}


function QuestionEditUser(id) {
	console.log(id);
	swal({
		title: "¿Deseas editar los datos del Usuario?",
		text: "¡Existen algunos datos sensible, por favor solicita permiso!",
		icon: "info",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				location.href = "/user/formUser/" + id;
			}
		});
}

function QuestionSavePeople() {
	swal({
		title: "Revise sus Datos",
		text: "¡Revise que los datos esten llenados correctamente!",
		icon: "info",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				location.href = "/peoples/listPeople";
			}
		});
}



//Carga los datos del formulario eventos
function loadEventData(eventId) {
    fetch(`/event/eventEditBy/${eventId}`)
        .then(response => response.json())  // Asegúrate de que el controlador devuelva un JSON
        .then(data => {
            // Cargar los datos en los campos del formulario del modal
            document.getElementById('eventId').value = data.id;
            document.getElementById('eventName').value = data.nameEvent;
			document.getElementById('eventTipe').value = data.tipoEvento;
            // Cargar otros campos si es necesario
        })
        .catch(error => {
            console.error('Error al cargar los datos del evento:', error);
        });
}
//procesa los datos del formulario en el modal
function submitEventForm() {
    const form = document.getElementById('editEventForm');
    const formData = new FormData(form);

    fetch(`/event/formevent`, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            // Cerrar el modal y posiblemente actualizar la lista de eventos
			$('#editEventModal').modal('hide');
            window.location.reload(); // Recargar la página para ver los cambios
        } else {
            alert('Error al actualizar el evento');
        }
    })
    .catch(error => {
        console.error('Error al enviar el formulario:', error);
    });
}

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
