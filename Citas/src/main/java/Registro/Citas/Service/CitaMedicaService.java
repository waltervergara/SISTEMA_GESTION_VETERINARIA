package Registro.Citas.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import Registro.Citas.Modelo.*;
import Registro.Citas.Client.*;
import Registro.Citas.Repository.CitaMedicaRepository;
import feign.FeignException;

@Service
public class CitaMedicaService {

    @Autowired
    private CitaMedicaRepository citaMedicaRepository;

    @Autowired
    private PropietarioClient propietarioClient;

    @Autowired
    private MascotaClient mascotaClient;
    @Autowired
    private EmpleadoClient empleadoClient;

    //guardar la cita
    public Optional<CitaMedica> guardarCitaMedica(CitaMedica citaMedica) {
    
    //Validar si el ya existe una consulta con ese codigo
        Optional<CitaMedica> existe = citaMedicaRepository.findByCodigoConsulta(citaMedica.getCodigoConsulta());
        if(existe.isPresent()){
            // Lanzamos una excepción personalizada en lugar de Optional.empty()
            throw new RuntimeException("Ya existe una cita con el código: " + citaMedica.getCodigoConsulta());
        }

    //Validar con los otros microservicios
    //Validar al propietario
        try {
            // Llama al microservicio de propietario
            propietarioClient.obtenerPropietarioporRun(citaMedica.getRunPropietario());
        } catch (FeignException.NotFound e) {
            // Si algún microservicio responde 404, detenemos el guardado
            throw new RuntimeException("Error: El propietario con run " + citaMedica.getRunPropietario() + " no existe o no se a encontrado");
        }
    //validar a la mascota
        try{
            //llamamos al microservicio de mascota
            mascotaClient.obtenerMascotaporCodigo(citaMedica.getCodigoMicrochip());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: La mascota con el codigo " + citaMedica.getCodigoMicrochip() + " no existe o no se encontrado");
        }
    //Validar al empleado
        try{
            //llamamos al microservicio de empleados
            empleadoClient.obtenerEmpleadoporRun(citaMedica.getRunEmpleado());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: El empleado con el run " + citaMedica.getRunEmpleado() + " no existe o no se encontrado");
        }

        //Si pasó todas las validaciones, guardamos en la base de datos
        try {
            return Optional.of(citaMedicaRepository.save(citaMedica));
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al intentar registrar la cita: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por codigoConsulta
    public Optional<CitaMedica> buscarPorCodigoConsulta(String codigoConsulta){
        try{
            return citaMedicaRepository.findByCodigoConsulta(codigoConsulta);
        }catch(Exception e){
            System.err.println("Error al buscar el codigo de la cita medica " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    //la mejor forma de definirlo es que , esta parte es la api hacia los otros microservicios , es como un puente de datos seguros
    public Optional<MascotaDTO> obtenerMascota(String codigoMicrochip){
        try {
            MascotaDTO mascota = mascotaClient.obtenerMascotaporCodigo(codigoMicrochip);
            return Optional.ofNullable(mascota);
        } catch (Exception e) {
            System.err.println("Error al obtener datos de la mascota " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<PropietarioDTO> obtenerPropietario(String run){
        try{
            PropietarioDTO propietario = propietarioClient.obtenerPropietarioporRun(run);
            return Optional.ofNullable(propietario);
        } catch (Exception e){
            System.err.println("Error al obtener los datos del propietario " + e.getMessage());
            return Optional.empty();
        } 
    }

    public Optional<EmpleadosDTO> obtenerEmpleado(String run){
        try{
            EmpleadosDTO empleado = empleadoClient.obtenerEmpleadoporRun(run);
            return Optional.ofNullable(empleado);
        } catch (Exception e){
            System.err.println("Error al obtener datos del empleado " + e.getMessage());
            return Optional.empty();
        }
    }

    //Para el get
    public Optional<CitaMedicaDTO> obtenerDetalleCompletoCita(String codigoConsulta) {
        try {
            //Aqui buscamos la consulta que se guarda premiamente en base de datos
            Optional<CitaMedica> existe = buscarPorCodigoConsulta(codigoConsulta);
            
            //si no la encuentra se detiene y no devuelve nada 
            if (existe.isEmpty()) {
                return Optional.empty(); 
            }
            
            //si la encuentra saca la informacion desde existe
            CitaMedica cita = existe.get();
            //y despues la guarda en DetalleCompleto
            CitaMedicaDTO detalleCompleto = new CitaMedicaDTO();
            
            //Al tener el objeto creado le colocaremos los datos de la cita creada para despues mostrarlo al final
            detalleCompleto.setCodigoConsulta(cita.getCodigoConsulta());
            detalleCompleto.setFechaHora(cita.getFechaHora());
            detalleCompleto.setMotivo(cita.getMotivo());
            detalleCompleto.setEstado(cita.getEstado());
            detalleCompleto.setObservaciones(cita.getObservaciones());

            //Aqui usamos la comunicacion de microservicios , aqui con los metodos del client traemos la informacion desde los otros servicios
            MascotaDTO mascota = obtenerMascota(cita.getCodigoMicrochip()).orElse(null);
            PropietarioDTO propietario = obtenerPropietario(cita.getRunPropietario()).orElse(null);
            EmpleadosDTO empleado = obtenerEmpleado(cita.getRunEmpleado()).orElse(null);
            //Aqui coloco una verificacion para  ver si se conecto bien con los otras bases de datos
            if (propietario == null || mascota == null || empleado == null) {
                //Mensaje de error
                throw new RuntimeException("Error : no se pudo obtener la informacion completa de la cita. Uno o más servicios no estan operativos");
            }

            //Y aqui colocamos la informacion faltante en el detalle de la cita
            detalleCompleto.setMascota(mascota);
            detalleCompleto.setPropietario(propietario);
            detalleCompleto.setEmpleado(empleado);

            //Y aqui al final devolvemos el detalle completo con toda la informacion que necesitemos
            return Optional.of(detalleCompleto);

        } catch (Exception e) {
            System.err.println("Error al armar el detalle completo de la cita: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar el detalle de la cita");
        }
    }
    
    //Put
    public CitaMedica actualizarCitaMedica (String codigoConsulta , CitaMedica citaMedicaExistente){

        if (citaMedicaExistente.getEstado() == null || citaMedicaExistente.getEstado().trim().isEmpty()) {
                throw new IllegalArgumentException("El estado es obligatorio.");
        }    
        
        if (citaMedicaExistente.getObservaciones() == null || citaMedicaExistente.getObservaciones().trim().isEmpty()) {
                throw new IllegalArgumentException("la observacion es obligatoria.");
        }

        try{
            CitaMedica citaNueva = citaMedicaRepository.findByCodigoConsulta(codigoConsulta).orElse(null);

            //validacion
            if(citaNueva != null){
                citaNueva.setEstado(citaMedicaExistente.getEstado());
                citaNueva.setObservaciones(citaMedicaExistente.getObservaciones());

                return citaMedicaRepository.save(citaNueva);
            }else{
                return null;
            }
        }catch(DataAccessException e){
            System.err.println("Error en la base de datos al actualizar : " + e.getMessage());
            throw new RuntimeException("Error Interno al actualizar los datos");
        }
    }

    //delete
    public boolean eliminarCitaMedica(String codigoConsulta){
        try{
            CitaMedica citaExistente = citaMedicaRepository.findByCodigoConsulta(codigoConsulta).orElse(null);

            if(citaExistente != null){
                citaMedicaRepository.delete(citaExistente);
                return true;
            }else{
                return false;
            }
        }catch(DataAccessException e){
            System.err.println("Error al intentar eliminar la consulta " + e.getMessage() );
            throw new RuntimeException("No se puede eliminar la consulta");
        }
        
    }

    //Para guardar las citas en una lista relacionado con el codigoMicrochip
    public List<CitaMedicaDTO> obtenerCitasPorMicrochip(String codigoMicrochip) {
        try {
            //Buscamos todas las citas asociadas a ese microchip en la BD local
            List<CitaMedica> citasLocales = citaMedicaRepository.findByCodigoMicrochip(codigoMicrochip);

            //Si no hay citas, devolvemos una lista vacía de forma segura
            if (citasLocales.isEmpty()) {
                return Collections.emptyList(); 
            }

            //Recorremos la lista de citas y transformamos cada una en su DTO con datos externos
            return citasLocales.stream().map(cita -> {
                CitaMedicaDTO dto = new CitaMedicaDTO();
                
                // Seteamos los datos propios de la cita
                dto.setCodigoConsulta(cita.getCodigoConsulta());
                dto.setFechaHora(cita.getFechaHora());
                dto.setMotivo(cita.getMotivo());
                dto.setEstado(cita.getEstado());
                dto.setObservaciones(cita.getObservaciones());

                // Reutilizamos tus métodos seguros para traer los datos de los otros microservicios
                MascotaDTO mascota = obtenerMascota(cita.getCodigoMicrochip()).orElse(null);
                PropietarioDTO propietario = obtenerPropietario(cita.getRunPropietario()).orElse(null);
                EmpleadosDTO empleado = obtenerEmpleado(cita.getRunEmpleado()).orElse(null);

                // Inyectamos los objetos externos en el DTO
                dto.setMascota(mascota);
                dto.setPropietario(propietario);
                dto.setEmpleado(empleado);

                return dto;
            }).toList(); // Retorna la lista completa de DTOs armada

        } catch (Exception e) {
            System.err.println("Error al obtener las citas por microchip: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar las citas de la mascota");
        }
    }
    
}