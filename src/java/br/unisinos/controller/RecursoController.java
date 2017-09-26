package br.unisinos.controller;

import java.util.ArrayList;
import br.unisinos.dao.ConexaoDAO;
import br.unisinos.model.Recurso;
import br.unisinos.model.Trilha;

/**
 * Classe responsável por controlar o resource entre o DAO
 * @author Bruno Mota
 */
public class RecursoController {

	public ArrayList<Recurso> consultarNumeroSOS(String id){
		return ConexaoDAO.getInstance().consultarNumeroSOS(id);
	}
	public ArrayList<Recurso> consultarRecursosIndoor(String codigoTag){
		return ConexaoDAO.getInstance().consultarRecursosIndoor(codigoTag);
	}
	public ArrayList<Recurso> consultarRecursosOutdoor(double latitude, double longitude, Integer quant) {
		return ConexaoDAO.getInstance().consultarRecursosOutdoor(latitude, longitude, quant);
	}
	public ArrayList<Recurso> consultarRecursosKeyword(String key, double latitude, double longitude, Integer quant) {
		return ConexaoDAO.getInstance().consultarRecursosKeyword(key, latitude, longitude, quant);
	}
	public ArrayList<Recurso> consultarRecursoAmb(String codigoTag) {
		return ConexaoDAO.getInstance().consultarRecursoAmb(codigoTag);
	}
	public ArrayList<Recurso> consultarRecursos(double latitude, double longitude, Integer quant) {
		return ConexaoDAO.getInstance().consultarRecursos(latitude, longitude, quant);
	}
	public String saveResource(String nome, String desc, int id_tipo, 
			double lat, double lon){
		return ConexaoDAO.getInstance().saveResource(nome, desc, id_tipo, 
				 lat, lon);
	}
	public ArrayList<Trilha> regTrilha(String data)
	{
		return ConexaoDAO.getInstance().regTrilha(data);
	}
}
