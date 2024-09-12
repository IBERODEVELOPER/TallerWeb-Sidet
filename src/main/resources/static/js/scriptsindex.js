document.addEventListener('DOMContentLoaded', function() {
		 // Leer el nombre del evento
		let eventInput = document.getElementById('subtipoEvento');
	    let eventName  = sessionStorage.getItem('eventName');
		let tipoEvento = sessionStorage.getItem('selectedOptionTipoEvento');
		const tipoEventoThymeleaf = document.getElementById('TipoEvento').textContent.trim();
	 	// Si no hay un nombre de evento en sessionStorage, obtenerlo desde Thymeleaf
	    if (!eventName && eventInput) {
	        eventName = eventInput.value;
	        // guardarlo en sessionStorage
	        if (eventName) {
	            sessionStorage.setItem('eventName', eventName);
				startTimer();
	        }
	    }
	    // Verificar si se ha guardado un nombre de evento
	    if (eventName) {
	        if (eventInput) {
	            eventInput.value = eventName;
				startTimer();
	        }
	        
	    }else{
	    	// Ocultar el div si no hay evento
	        const cardDiv = document.getElementById('administrativa');
	        if (cardDiv) {
	            cardDiv.style.display = 'none';
	        }
	    }
		// Mostrar el tipo de evento en el elemento con id "TipoEvento"
		const tipoEventoElement = document.getElementById('TipoEvento');
		if (!tipoEvento) {
		        // Si no existe en sessionStorage, usar el valor de Thymeleaf
		        tipoEvento = tipoEventoThymeleaf;
		        // Guardar el valor en sessionStorage
		        sessionStorage.setItem('selectedOptionTipoEvento', tipoEvento);
		   }
		   // Mostrar el tipo de evento en el elemento correspondiente
		   if (tipoEventoElement) {
		           tipoEventoElement.textContent = tipoEvento; // Actualizar el contenido del tipo de evento
		  }
				
	// Función de temporizador
	function startTimer() {
		    try {
		        // Obtener el elemento del temporizador
		        const timerElement = document.getElementById('timer');
		        // Obtener el tiempo de inicio desde el input y convertirlo a milisegundos
		        const startTimeInput = document.getElementById('startTime');
		        let startTime = startTimeInput ? new Date(startTimeInput.value).getTime() : null;
				// Mostrar el valor del input startTime en la consola
				console.log("Valor de startTimeInput:", startTimeInput);
				console.log("Valor de startTime:", startTime);
		        // Verificar si hay un tiempo de inicio guardado en localStorage
		        if (startTime) {
		            localStorage.setItem('startTime', startTime);
		        } else {
		            // Si no hay tiempo guardado, establecer la hora actual como el tiempo de inicio
		            startTime = Date.now();
		            localStorage.setItem('startTime', startTime);
		        }

		        // Intervalo para actualizar el temporizador
		        const timerInterval = setInterval(() => {
		            // Obtener el tiempo actual
		            const now = Date.now();
		            // Calcular el tiempo transcurrido desde startTime
		            const elapsedTime = Math.floor((now - startTime) / 1000);
		            const hours = Math.floor(elapsedTime / 3600);
		            const minutes = Math.floor((elapsedTime % 3600) / 60);
		            const seconds = elapsedTime % 60;
		            
		            // Formatear el tiempo
		            const formattedTime =
		                (hours < 10 ? "0" + hours : hours) + ":" +
		                (minutes < 10 ? "0" + minutes : minutes) + ":" +
		                (seconds < 10 ? "0" + seconds : seconds);
		            
		            // Mostrar el tiempo en el elemento
		            timerElement.textContent = formattedTime;

		        }, 1000);
		        
		    } catch (error) {
		        console.error('Error al iniciar el temporizador:', error);
		    }
		}	    
	 // Al presionar el botón "Finalizar"
	    document.querySelector('.btn-finalizar').addEventListener('click', function () {
	        // Obtener el ID de la actividad desde sessionStorage
	        let activityId = sessionStorage.getItem('activityId');
			// Obtener el ID de la actividad desde el thymeleaf
			// Obtener el ID del evento desde el DOM
			const eventId = document.getElementById('eventId') ? document.getElementById('eventId').value : null;
	        const detailsevent = document.querySelector('#detalleEvento').value;
	        
	        // Verificar que el ID de la actividad exista
	        if (!activityId) {
				// Si activityId está vacío, intentar obtenerlo de eventId
				activityId = eventId;
	        }
			
			// Verificar si el ID de la actividad sigue sin estar presente
			if (!activityId) {
			    swal({
			        title: 'Advertencia',
			        text: 'No se encontró la actividad en progreso.',
			        icon: 'warning',
			        button: 'OK'
			    });
			    return;
			}
	        // Capturar la hora actual como hora de finalización en formato local (Perú)
	        const localEndTime = new Date().toLocaleString('es-PE', {
	            timeZone: 'America/Lima',
	            day: 'numeric',
	            month: 'numeric',
	            year: 'numeric',
	            hour: '2-digit',
	            minute: '2-digit',
	            second: '2-digit',
	            hour12: false // Para usar el formato de 24 horas
	        });
	     	// Imprimir o usar la hora formateada
	        console.log(localEndTime);
	        const csrfToken = document.querySelector('input[name="_csrf"]').value;
	        // Enviar los datos al servidor con fetch para finalizar la actividad
	        fetch('/api/events/finish-activity', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                'Accept': 'application/json',
	                'X-CSRF-TOKEN': csrfToken // Asegúrate de incluir el token CSRF
	            },
	            body: JSON.stringify({
	                activityId: activityId, // ID de la actividad
	                endTime: localEndTime, // Hora de finalización
	                detailsevent: detailsevent // Detalle del evento
	            })
	        })
	        .then(response => response.json())
	        .then(data => {
	        	if (data.success) {
	        	    // Mostrar un mensaje de confirmación con SweetAlert v1
	        	    swal({
	        	      title: 'Evento finalizado',
	        	      text: 'El evento ha sido finalizado correctamente.',
	        	      icon: 'success',
	        	      timer: 1000,
	        	      showConfirmButton: false,
	        	    }).then(() => {
	        	      // Limpiar sessionStorage
	        	      sessionStorage.clear();
	        	      localStorage.removeItem('startTime');
	        	      // Redirigir a la página principal
	        	      window.location.href = `/index`;
	        	    });
	        	} else {
	            	// Mostrar una alerta de error si no se pudo iniciar la actividad
	                swal({
	                  title: 'Error',
	                  text: 'Error al finalizar la actividad.',
	                  icon: 'error',
	                  button: 'OK'
	                });
	            }
	        })
	        .catch(error => console.error('Error al finalizar la actividad:', error));
	    });

	    
	    updateChart();
		initializeMonthlyProgressChart();
		initializeMeasurementChart();
	});


		function updateChart() {
			const productividad = 10;
			const adherencia = 20;
			const ocupacion = 30;
			const efectividad = 40;

			const ctx = document.getElementById('indicatorChart').getContext(
					'2d');
			new Chart(ctx, {
				type : 'polarArea',
				data : {
					labels : [ 'Productividad', 'Adherencia', 'Ocupación',
							'Efectividad' ],
					datasets : [ {
						label : 'Indicadores',
						data : [ productividad, adherencia, ocupacion,
								efectividad ],
						backgroundColor : [ '#a3e1d4', '#dedede', '#9CC3DA',
								'#f5b7b1' ]
					} ]
				},
				options : {
					responsive : true,
					maintainAspectRatio : false,
					scales : {
						r : {
							beginAtZero : true,
							max : 100
						}
					}
				}
			});
		}

		function initializeMonthlyProgressChart() {
			const ctx = document.getElementById('monthlyProgressChart')
					.getContext('2d');
			new Chart(ctx, {
				type : 'line',
				data : {
					labels : [ '8/2022', '9/2022', '10/2022', '11/2022',
							'12/2022', '1/2023', '2/2023', '3/2023', '4/2023',
							'5/2023', '6/2023', '7/2023' ],
					datasets : [ {
						label : 'Productividad',
						data : [ 0, 0, 0, 0, 25, 30, 35, 40, 45, 50, 55, 60 ],
						borderColor : '#ff6f61',
						backgroundColor : 'rgba(255, 111, 97, 0.2)',
						fill : true
					}, {
						label : 'Adherencia',
						data : [ 0, 0, 0, 0, 30, 35, 40, 45, 50, 55, 60, 65 ],
						borderColor : '#6b5b95',
						backgroundColor : 'rgba(107, 91, 149, 0.2)',
						fill : true
					}, {
						label : 'Ocupación',
						data : [ 0, 0, 0, 0, 40, 45, 50, 55, 60, 65, 70, 75 ],
						borderColor : '#88d8b0',
						backgroundColor : 'rgba(136, 216, 176, 0.2)',
						fill : true
					}, {
						label : 'Efectividad',
						data : [ 0, 0, 0, 0, 50, 55, 60, 65, 70, 75, 80, 85 ],
						borderColor : '#f8c291',
						backgroundColor : 'rgba(248, 194, 145, 0.2)',
						fill : true
					} ]
				},
				options : {
					responsive : true,
					maintainAspectRatio : false,
					scales : {
						x : {
							title : {
								display : true,
								text : 'Meses'
							}
						},
						y : {
							title : {
								display : true,
								text : 'Valor'
							}
						}
					}
				}
			});
		}
		function initializeMeasurementChart() {
			const productividad = 10;
			const adherencia = 20;
			const ocupacion = 30;
			const efectividad = 40;

			const ctx = document.getElementById('measurementChart').getContext(
					'2d');
			new Chart(ctx, {
				type : 'bar',
				data : {
					labels : [ 'Productividad', 'Adherencia', 'Ocupación',
							'Efectividad' ],
					datasets : [ {
						label : 'Indicadores',
						data : [ productividad, adherencia, ocupacion,
								efectividad ],
						backgroundColor : [ '#a3e1d4', '#dedede', '#9CC3DA',
								'#f5b7b1' ]
					} ]
				},
				options : {
					responsive : true,
					maintainAspectRatio : true,
					scales : {
						x : {
							title : {
								display : true,
								text : 'Indicadores'
							}
						},
						y : {
							title : {
								display : true,
								text : 'Valor'
							},
							beginAtZero : true
						}
					}
				}
			});
		}
