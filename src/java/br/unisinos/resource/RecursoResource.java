package br.unisinos.resource;

import java.util.ArrayList;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import br.unisinos.controller.RecursoController;
import br.unisinos.model.Recurso;
import br.unisinos.model.Trilha;

/**
 * Classe responsável por conter os métodos de acesso ao WebService REST
 * @author Bruno Mota
 */
@Path("/recurso")
public class RecursoResource {

	/**
	 * Método responsável por invocar o controller e executar o consultarNumeroSOS
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarNumeroSOS/{id}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarNumeroSOS(
			@PathParam("id") String id) {
		return new RecursoController().consultarNumeroSOS(id);
	}
	
	/**
	 * Método responsável por invocar o controller e executar o consultarRecursosIndoor
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarRecursosIndoor/{codigoTag}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarRecursosIndoor(
			@PathParam("codigoTag") String codigoTag) {
		return new RecursoController().consultarRecursosIndoor(codigoTag);
	}
	
	/**
	 * Método responsável por invocar o controller e executar o consultarRecursosOutdoor
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarRecursosOutdoor/{latitude}/{longitude}/{quant}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarRecursosOutdoor(
			@PathParam("latitude") double latitude, 
			@PathParam("longitude") double longitude,
			@PathParam("quant") Integer quant) {
		return new RecursoController().consultarRecursosOutdoor(latitude, longitude, quant);
	}
	
	/**
	 * Método responsável por invocar o controller e executar o consultarRecursosKeyword
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarRecursosKeyword/{key}/{latitude}/{longitude}/{quant}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarRecursosKeyword(
			@PathParam("key") String key,
			@PathParam("latitude") double latitude,
			@PathParam("longitude") double longitude,
			@PathParam("quant") Integer quant) {
		return new RecursoController().consultarRecursosKeyword(key, latitude, longitude, quant);
	}
	
	/**
	 * Método responsável por invocar o controller e executar o consultarRecursoAmb
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarRecursoAmb/{codigoTag}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarRecursoAmb(
			@PathParam("codigoTag") String codigoTag) {
		return new RecursoController().consultarRecursoAmb(codigoTag);
	}
	
	/**
	 * Método responsável por invocar o controller e executar o consultarRecursos
	 * @return ArrayList<Recurso>
	 */
	@GET
	@Path("/consultarRecursos/{latitude}/{longitude}/{quant}")
	@Produces("application/json")
	public ArrayList<Recurso> consultarRecursos(
			@PathParam("latitude") double latitude,
			@PathParam("longitude") double longitude,
			@PathParam("quant") Integer quant) {
		return new RecursoController().consultarRecursos(latitude, longitude, quant);
	}
	
	@GET
	@Path("/regTrilha/{data}")
	@Produces("application/json")
	public ArrayList<Trilha> regTrilha(@PathParam("data") String data)
	{
		return new RecursoController().regTrilha(data);
	}
	
	@GET
	@Path("/saveResource/{nome}/{desc}/{id_tipo}/{lat}/{long}")
	@Produces("application/json")
	public String saveResource(
			@PathParam("nome") String nome, 
			@PathParam("desc") String desc,
			@PathParam("id_tipo") int id_tipo,
			@PathParam("lat") Double lat,
			@PathParam("long") Double lon){
		return new RecursoController().saveResource(nome, desc, id_tipo, lat, lon);
	}
}