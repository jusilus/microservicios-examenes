package com.formacionbdi.microservicios.app.examenes.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.microservicios.app.examenes.services.ExamenService;
import com.formacionbdi.microservicios.commons.controllers.CommonController;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;

@RestController
public class ExamenController extends CommonController<Examen, ExamenService> {
	
	/* METODOS GET */
	
	@GetMapping("/respondidos-por-preguntas")
	public ResponseEntity<?> obtenerExamenesIdsPorPreguntasRespondidas(@RequestParam List<Long> preguntaIds){
		return ResponseEntity.ok().body(commonService.findExamenesIdsConRespuestasByPreguntaIds(preguntaIds));
	}
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> buscarPorNombre(@PathVariable String term) {
		List<Examen> examenes = this.commonService.findByNombre(term);
		return ResponseEntity.ok(examenes);
	}

	@GetMapping("/asignaturas")
	public ResponseEntity<?> listarAsignaturas() {
		return ResponseEntity.ok(commonService.findAllAsignaturas());
	}
	
	/* METODOS PUT */

	@PutMapping("/{id}")
	public ResponseEntity<?> modificarExamen(@Valid @RequestBody Examen examen, BindingResult result,
			@PathVariable Long id) {
		if (result.hasErrors()) {
			return validar(result);
		}
		Optional<Examen> o = commonService.findById(id);
		if (!o.isPresent()) {
			return ResponseEntity.noContent().build();
		}
		Examen examenDb = o.get();
		examenDb.setNombre(examen.getNombre());

		examenDb.getPreguntas().stream().filter(pregunta -> !examen.getPreguntas().contains(pregunta))
				.forEach(examenDb::removePregunta);

		examenDb.setPreguntas(examen.getPreguntas());

		return ResponseEntity.status(HttpStatus.CREATED).body(commonService.save(examenDb));
	}	
}
