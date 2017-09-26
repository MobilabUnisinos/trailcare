package br.unisinos.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe respons�vel por iniciar, finalizar conex�o com banco de dados
 * @author Bruno Mota
 */
public class ConexaoPostgres {

	private static final String DRIVER = "org.postgresql.Driver";
	private static final String URL = "jdbc:postgresql://localhost:5432/HefestosDatabase";
	private static final String USUARIO = "mobilab";
	private static final String SENHA = "mobilab";

	/**
	 * M�todo respons�vel por criar conex�o com o banco de dados
	 * @return conexao
	 */
	public Connection criarConexao() {
		Connection conexao = null;

		try {
			Class.forName(DRIVER);
			conexao = DriverManager.getConnection(URL, USUARIO, SENHA);

		} catch (Exception e) {
			System.out.println("Erro ao criar conex�o com o banco de dados: "
					+ URL);
			e.printStackTrace();
		}
		return conexao;
	}

	/**
	 * M�todo respons�vel por fechar conex�o existente com o banco de dados
	 * @param conexao
	 * @param pstmt
	 * @param rs
	 */
	public void fecharConexao(Connection conexao, PreparedStatement pstmt, ResultSet rs) {
		try {

			if (conexao != null) 
				conexao.close();
			
			if (pstmt != null)
				pstmt.close();
			
			if (rs != null)
				rs.close();

		} catch (Exception e) {
			System.out.println("Erro ao fechar conex�o com o banco: " + URL);
		}

	}
}
