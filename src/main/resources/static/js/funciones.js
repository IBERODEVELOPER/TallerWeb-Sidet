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



function QuestionDeleteByIdRol(id) {
	console.log(id);
	swal({
		title: "¿Estas seguro de eliminar este registro?",
		text: "¡Una vez eliminado no se prodra restablecer!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				$.ajax({
					url: "/rol/deleteByIdRol/" + id,
					success: function(res) {
						console.log(res);
					},
				});
				swal("UPS! Registro eliminado!", {
					icon: "success",
				}).then((ok) => {
					if (ok) {
						location.href = "/rol/listRol";
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

function QuestionEditRol(id) {
	console.log(id);
	swal({
		title: "¿Deseas editar los datos?",
		text: "¡Existen algunos datos sensible, por favor solicita permiso!",
		icon: "info",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				location.href = "/rol/formRol/" + id;
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

$(document).ready(function() {
	 var messagesHidden = false;
	function hideShowContainers() {
		if (messagesHidden) return;
		var successMessage = $('#message1');
		var errorMessage   = $('#message2');
		var warningMessage = $('#message3');
		var infoMessage    = $('#message4');

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

	}
	// Ejecutar la función para ocultar los mensajes
	hideShowContainers();
});
