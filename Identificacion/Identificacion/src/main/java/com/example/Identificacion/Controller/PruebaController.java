package com.example.Identificacion.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pruebas")
public class PruebaController {

    // 1. Ruta pública (Cualquier persona logueada puede entrar)
    @GetMapping("/saludo")
    public ResponseEntity<String> saludo() {
        return ResponseEntity.ok("¡Hola! Estás autenticado correctamente.");
    }

    // 2. Ruta SOLO para Veterinarios
    // Fíjate: usamos hasRole('VET'). Spring automáticamente busca ROLE_VET en el token.
    @GetMapping("/receta")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<String> emitirReceta() {
        return ResponseEntity.ok("Dr. García, aquí puede emitir su receta médica.");
    }

    // 3. Ruta SOLO para Asistentes
    @GetMapping("/citas")
    @PreAuthorize("hasRole('ASSISTANT')")
    public ResponseEntity<String> agendarCita() {
        return ResponseEntity.ok("Asistente, aquí puede agendar las citas del día.");
    }

    // 4. Ruta SOLO para Dueños de mascotas
    @GetMapping("/mascota")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> verMascota() {
        return ResponseEntity.ok("Carlos, aquí puede ver el historial de su mascota.");
    }

    // 5. Ruta para VARIOS roles (Vet o Assistant pueden ver el inventario)
    @GetMapping("/inventario")
    @PreAuthorize("hasAnyRole('VET', 'ASSISTANT')")
    public ResponseEntity<String> verInventario() {
        return ResponseEntity.ok("Personal de la clínica, aquí está el inventario de medicinas.");
    }
}