function QuestionDeleteByIdPerson(id) {
	console.log(id);
	swal({
		title: "¿Estas seguro de eliminar este registro?",
		text: "¡Una vez eliminado no se podrá restablecer!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((OK) => {
			if (OK) {
				$.ajax({
					url: "/Peoples/deleteByIdPerson/" + id,
					success: function(res) {
						console.log(res);
					},
				});
				swal("UPS! Registro eliminado!", {
					icon: "success",
				}).then((ok) => {
					if (ok) {
						location.href = "/Peoples/listPeople";
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
				location.href = "/Peoples/formPeople/" + id;
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
				location.href = "/Peoples/listPeople";
			}
		});
}