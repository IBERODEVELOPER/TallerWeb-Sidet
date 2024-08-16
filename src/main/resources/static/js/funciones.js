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

document.getElementById('formFile').addEventListener('change', function(event) {
	const file = event.target.files[0];
	if (file) {
		const reader = new FileReader();
		reader.onload = function(e) {
			document.getElementById('imgAvatar').src = e.target.result;
		}
		reader.readAsDataURL(file);
	}
});

