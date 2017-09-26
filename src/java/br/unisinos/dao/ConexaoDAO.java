package br.unisinos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import br.unisinos.factory.ConexaoPostgres;
import br.unisinos.model.Recurso;
import br.unisinos.model.Trilha;

/**
 * Classe resposável por conter os métodos de consulta no banco de dados
 * @author Bruno Mota
 */
public class ConexaoDAO extends ConexaoPostgres {

	/**
	 * Método de ordenação de recursos pela Distancia
	 * @param recursos
	 */
	private static void ordenaPorNumero(ArrayList<Recurso> recursos) {
		Collections.sort(recursos, new Comparator<Recurso>() {
			@Override
			public int compare(Recurso o1, Recurso o2) {
				return o1.getDistancia().compareTo(o2.getDistancia());
			}
		});
	}
		
	/**
     * Método para cálculo de distância entre pontos com os seguintes parametros:
     * @param lat1
     * @param lat2
     * @param long1
     * @param long2
     * @return
     */
	public int distanceBetweenPoints(double lat1, double lat2, double long1, double long2) {
        Double dlon, dlat, a, distancia;

        dlon = Math.toRadians(long2 - long1);
        dlat = Math.toRadians(lat2 - lat1);
        a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        distancia = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 6378140;
        return distancia.intValue();
    }
	private static ConexaoDAO instance;
	/**
	 * Método responsável por criar uma instancia da classe ConexaoDAO
	 * @return instance
	 */
	public static ConexaoDAO getInstance() {
		if (instance == null)
			instance = new ConexaoDAO();
		return instance;
	}
	
	/**
	 * Método responsável por consultar contato de emergência (SOS)
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarNumeroSOS(String id) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		recursos = new ArrayList<Recurso>();

		try {
			pstmt = conexao.prepareStatement("SELECT nome,"
					+ " telefonesos FROM pessoa WHERE id_pessoa = "+ id +";");
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setNumeroSOS(rs.getString("telefonesos"));
				recursos.add(recurso);
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar contato SOS: " + e);
			e.printStackTrace();
		} finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return recursos;
	}

	/**
	 * Método responsável por consultar recurso indoor
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarRecursosIndoor(String codigoTag) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		recursos = new ArrayList<Recurso>();
		DateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat hora = new SimpleDateFormat("HH:mm:ss");
				
		try {
			pstmt = conexao.prepareStatement("SELECT id_tiporecurso, nome, descricao FROM recurso_indoor r WHERE r.id_recurso_indoor_ambiente=" + codigoTag + ";");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setTipoRecurso(rs.getInt("id_tiporecurso"));
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setDescricaoRecurso(rs.getString("descricao"));
				recursos.add(recurso);
			}
			
			} catch (Exception e) {
				System.out.println("Erro ao retornar recursos indoor: " + e);
				e.printStackTrace();
			}
			String sql = "INSERT INTO trilha(datta, hora, codigoTag, rec1, rec2, rec3) VALUES (?,?,?,?,?,?)";
			Date dt = new Date();
			
			try {
				pstmt = conexao.prepareStatement(sql);
				
				pstmt.setString(1, data.format(dt.getTime()));
				pstmt.setString(2, hora.format(dt.getTime()));
				pstmt.setString(3, codigoTag);
				pstmt.setString(4, recursos.get(0).getDescricaoRecurso());
				pstmt.setString(5, recursos.get(1).getDescricaoRecurso());
				pstmt.setString(6, recursos.get(2).getDescricaoRecurso());
				pstmt.execute();
				
			} catch (Exception i) {
				System.out.println("Erro ao salvar trilha de recursos indoor: " + i);
				i.printStackTrace();
			}

			
		finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return recursos;
	}
	
	/**
	 * Método responsável por consultar recursos outdoor
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarRecursosOutdoor(double latitude, double longitude, Integer quant) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> finale = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		recursos = new ArrayList<Recurso>();
		finale = new ArrayList<Recurso>();
		int cont = 0;
		DateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat hora = new SimpleDateFormat("HH:mm:ss");
		
		try {
			pstmt = conexao.prepareStatement("SELECT id_tiporecurso, nome, descricao, latitude, longitude FROM recurso_outdoor;");
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setTipoRecurso(rs.getInt("id_tiporecurso"));
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setDescricaoRecurso(rs.getString("descricao"));
				recurso.setDistancia(distanceBetweenPoints(latitude, (rs.getDouble("latitude")), longitude, (rs.getDouble("longitude"))));
				recursos.add(recurso);
			}
			ordenaPorNumero(recursos);
			while (cont < quant) {
				Recurso rec = new Recurso();
				rec.setTipoRecurso(recursos.get(cont).getTipoRecurso());
				rec.setNomeRecurso(recursos.get(cont).getNomeRecurso());
				rec.setDescricaoRecurso(recursos.get(cont).getDescricaoRecurso());
				rec.setDistancia(recursos.get(cont).getDistancia());
				finale.add(rec);				
				cont++;
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar recursos outdoor: " + e);
			e.printStackTrace();
		} 
		
		String sql = "INSERT INTO trilha(datta, hora, latitude, longitude, rec1, rec2, rec3, rec4, rec5) VALUES (?,?,?,?,?,?,?,?,?)";
		Date dt = new Date();
		
		try {
			pstmt = conexao.prepareStatement(sql);
			
			pstmt.setString(1, data.format(dt.getTime()));
			pstmt.setString(2, hora.format(dt.getTime()));
			pstmt.setDouble(3, latitude);
			pstmt.setDouble(4, longitude);
			pstmt.setString(5, finale.get(0).getDescricaoRecurso());
			pstmt.setString(6, finale.get(1).getDescricaoRecurso());
			pstmt.setString(7, finale.get(2).getDescricaoRecurso());
			pstmt.setString(8, finale.get(3).getDescricaoRecurso());
			pstmt.setString(9, finale.get(4).getDescricaoRecurso());
			pstmt.execute();
			
		} catch (Exception i) {
			System.out.println("Erro ao salvar trilha de recursos outdoor: " + i);
			i.printStackTrace();
		}
		finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return finale;
	}

	/**
	 * Método responsável por consultar recursos outdoor usando keyword
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarRecursosKeyword(String key, double latitude, double longitude, Integer quant) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> finale = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		recursos = new ArrayList<Recurso>();
		finale = new ArrayList<Recurso>();
		int cont = 0;
		
		try {
			pstmt = conexao.prepareStatement("(SELECT id_tiporecurso, nome, descricao, latitude, longitude FROM Recurso_Outdoor WHERE UPPER(Nome) LIKE UPPER ('%" + key + "%')) union (SELECT id_tiporecurso, nome, descricao, latitude, longitude FROM Recurso_Indoor WHERE UPPER(Nome) LIKE UPPER ('%" + key + "%'));");
			rs = pstmt.executeQuery();
						
			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setTipoRecurso(rs.getInt("id_tiporecurso"));
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setDescricaoRecurso(rs.getString("descricao"));
				recurso.setDistancia(distanceBetweenPoints(latitude, (rs.getDouble("latitude")), longitude, (rs.getDouble("longitude"))));
				recursos.add(recurso);
			}
			ordenaPorNumero(recursos);
			while (cont < quant) {
				Recurso rec = new Recurso();
				rec.setTipoRecurso(recursos.get(cont).getTipoRecurso());
				rec.setNomeRecurso(recursos.get(cont).getNomeRecurso());
				rec.setDescricaoRecurso(recursos.get(cont).getDescricaoRecurso());
				rec.setDistancia(recursos.get(cont).getDistancia());
				finale.add(rec);				
				cont++;
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar recursos keyword: " + e);
			e.printStackTrace();
		} finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return finale;
	}

	/**
	 * Método responsável por consultar o ambiente de um recurso
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarRecursoAmb(String codigoTag) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		recursos = new ArrayList<Recurso>();

		try {
			pstmt = conexao.prepareStatement("SELECT r.id_recurso_indoor FROM recurso_indoor r JOIN Recurso_Indoor_Tag rit ON r.id_recurso_indoor=rit.id_recurso_indoor JOIN Tag t ON rit.id_tag=t.id_tag WHERE t.codigo='" + codigoTag + "';");
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setIdPai(rs.getString("id_recurso_indoor"));
				recursos.add(recurso);
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar ambiente do recurso: " + e);
			e.printStackTrace();
		} finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return recursos;
	}

	/**
	 * Método responsável por consultar recursos outdoor e indoor
	 * @return ArrayList<Recurso>
	 * @author Bruno Mota
	 */
	public ArrayList<Recurso> consultarRecursos(double latitude, double longitude, Integer quant) {
		Connection conexao = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Recurso> finale = null;
		ArrayList<Recurso> recursos = null;
		conexao = criarConexao();
		finale = new ArrayList<Recurso>();
		recursos = new ArrayList<Recurso>();
		int cont = 0;
		DateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat hora = new SimpleDateFormat("HH:mm:ss");
		
		try {
			pstmt = conexao.prepareStatement("SELECT id_tiporecurso, nome, descricao, latitude, longitude FROM recurso_outdoor;");
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setTipoRecurso(rs.getInt("id_tiporecurso"));
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setDescricaoRecurso(rs.getString("descricao"));
				recurso.setDistancia(distanceBetweenPoints(latitude, (rs.getDouble("latitude")), longitude, (rs.getDouble("longitude"))));
				recursos.add(recurso);
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar recursos (outdoor): " + e);
			e.printStackTrace();
		}
		
		// indoor
		
		try {
			pstmt = conexao.prepareStatement("SELECT id_tiporecurso, nome, descricao, latitude, longitude FROM recurso_indoor;");
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Recurso recurso = new Recurso();
				recurso.setTipoRecurso(rs.getInt("id_tiporecurso"));
				recurso.setNomeRecurso(rs.getString("nome"));
				recurso.setDescricaoRecurso(rs.getString("descricao"));
				recurso.setDistancia(distanceBetweenPoints(latitude, (rs.getDouble("latitude")), longitude, (rs.getDouble("longitude"))));
				recursos.add(recurso);
			}
			ordenaPorNumero(recursos);
			while (cont < quant) {
				Recurso rec = new Recurso();
				rec.setTipoRecurso(recursos.get(cont).getTipoRecurso());
				rec.setNomeRecurso(recursos.get(cont).getNomeRecurso());
				rec.setDescricaoRecurso(recursos.get(cont).getDescricaoRecurso());
				rec.setDistancia(recursos.get(cont).getDistancia());
				finale.add(rec);				
				cont++;
			}
		} catch (Exception e) {
			System.out.println("Erro ao retornar recursos (indoor): " + e);
			e.printStackTrace();
		} 
		String sql = "INSERT INTO trilha(datta, hora, latitude, longitude, rec1,"
				+ " rec2, rec3, rec4, rec5) VALUES (?,?,?,?,?,?,?,?,?)";
		Date dt = new Date();
		
		try {
			pstmt = conexao.prepareStatement(sql);
			
			pstmt.setString(1, data.format(dt.getTime()));
			pstmt.setString(2, hora.format(dt.getTime()));
			pstmt.setDouble(3, latitude);
			pstmt.setDouble(4, longitude);
			pstmt.setString(5, finale.get(0).getDescricaoRecurso());
			pstmt.setString(6, finale.get(1).getDescricaoRecurso());
			pstmt.setString(7, finale.get(2).getDescricaoRecurso());
			pstmt.setString(8, finale.get(3).getDescricaoRecurso());
			pstmt.setString(9, finale.get(4).getDescricaoRecurso());
			pstmt.execute();
			
		} catch (Exception i) {
			System.out.println("Erro ao salvar trilha de recursos: " + i);
			i.printStackTrace();
		}
		finally {
			fecharConexao(conexao, pstmt, rs);
		}
		return finale;
	}
	
	public String saveResource(String nome, String desc, int id_tipo,  
			 double lat, double lon){
	
		return cadastrarRecursoOutdoor(nome,desc,id_tipo,lat,lon);
		
	}
	
	//método para cadastrar recursos
	
	public String cadastrarRecursoOutdoor(String nome, String desc, int id_tipo, 
			double lat, double lon){
		
		PreparedStatement pstmt = null;
		Connection conexao = null;
		String sql = null;
		String retornar = null;
		conexao = criarConexao();
		
		
		sql = "INSERT INTO recurso_outdoor"
				+ "(id_tiporecurso, nome, descricao, latitude, longitude) "
				+ "VALUES (?,?,?,?,?);";
		
		
		try
		{
			pstmt = conexao.prepareStatement(sql);
			
			pstmt.setInt(1,id_tipo);
			pstmt.setString(2, nome);
			pstmt.setString(3, desc);
			pstmt.setString(4, Double.toString(lat));
			pstmt.setString(5, Double.toString(lon));
			
			pstmt.execute();
			
			
			
				
			retornar = nome+", "+desc+", "+id_tipo+", "+lat+", "+lon;
		}
		catch(Exception e)
		{
			retornar = "Erro ao cadastrar recurso outdoor: " + e;
			System.out.println("Erro ao cadastrar recurso outdoor: " + e);
			e.printStackTrace();
		}
		
		
		return retornar;
	}
	
	//método para registrar trilhas
	public ArrayList<Trilha> regTrilha(String data)
	{
		PreparedStatement pstmt = null;
		Connection conexao = null;
		String sql = null;
		
		conexao = criarConexao();
		
		
		sql = "INSERT INTO reg_trilha"
				+ "(id_user, inout, id_tipo, data, hora) "
				+ "VALUES (?,?,?,?,?);";
		
		ArrayList<Trilha> result = new ArrayList<Trilha>();
		
		String valores[] = data.split("-");
		
		
		String id_user = null;
		String inout = null;
		String id_tipo = null;
		String dia= null;
		String hora = null;
		//String d = null;
		
		for(int i=0;i<valores.length;i++)
		{
			String aux[] = valores[i].split(",");
			id_user = aux[0];
			inout = aux[1];
			id_tipo = aux[2];
			dia = aux[3];
			hora = aux[4];
			
			dia = dia.replaceAll(":", "/");
			
			Trilha t = new Trilha();
			t.setId_user(id_user);
			t.setInout(inout);
			t.setId_tipo(id_tipo);
			t.setDia(dia);
			t.setHora(hora);
			result.add(t);
			
			try
			{
				
				pstmt = conexao.prepareStatement(sql);
				
				int idu = Integer.parseInt(id_user);
				int io = Integer.parseInt(inout);
				int idt = Integer.parseInt(id_tipo);
				
				pstmt.setInt(1, idu);
				pstmt.setInt(2, io);
				pstmt.setInt(3, idt);
				pstmt.setString(4, dia);
				pstmt.setString(5, hora);
				
				pstmt.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
		
		
		
		System.out.println(id_user);
		System.out.println(inout);
		System.out.println(id_tipo);
		System.out.println(dia);
		System.out.println(hora);
		
		
		
		return result;
	}
	
}
