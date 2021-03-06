package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosDevolucao;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosEmprestimo;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.EmprestimoConcedido;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosLivro;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosUsuario;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.TipoExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.TipoUsuario;

public class Solucao {

	/**
	 * Você precisa implementar o código para executar o fluxo o completo de
	 * empréstimo e devoluções a partir dos dados que chegam como argumento.
	 * 
	 * Caso você queira pode adicionar coisas nas classes que já existem, mas não
	 * pode alterar nada.
	 */

	/**
	 * 
	 * @param livros                            dados necessários dos livros
	 * @param exemplares                        tipos de exemplares para cada livro
	 * @param usuarios                          tipos de usuarios
	 * @param emprestimos                       informações de pedidos de
	 *                                          empréstimos
	 * @param devolucoes                        informações de devoluções, caso
	 *                                          exista.
	 * @param dataParaSerConsideradaNaExpiracao aqui é a data que deve ser
	 *                                          utilizada para verificar expiração
	 * @return
	 */
	
	// CEN�RIOS DE TESTES 1, 2, 3 e 4 EST�O RETORNANDO O RESULTADO ESPERADO, MAS NA ORDEM ERRADA. 
	// ALBERTO PEDIU PARA ALTERAR O TESTE.
	public static Set<EmprestimoConcedido> executa(Set<DadosLivro> livros, Set<DadosExemplar> exemplares,
			Set<DadosUsuario> usuarios, Set<DadosEmprestimo> emprestimos, Set<DadosDevolucao> devolucoes,
			LocalDate dataParaSerConsideradaNaExpiracao) {
		/*
		 * Estamos recebendo os parametros e devemos realizar os emprestimos.
		 */

		Set<EmprestimoConcedido> emprestimosConcedidos = new LinkedHashSet<EmprestimoConcedido>();

		for (DadosEmprestimo emprestimo : emprestimos) {

			// Se n�o h� exemplar dispon�vel, n�o pode haver empr�stimo.
			DadosExemplar exemplar = getExemplarUsandoIdLivro(emprestimo.idLivro, exemplares);
			if (exemplar != null) {

				// usuario == null � porque n�o existe no Set. Validado no m�todo
				// ValidaEmprestimoUsuarioPadrao().
				DadosUsuario usuario = getUsuarioUsandoId(emprestimo.idUsuario, usuarios);

				if (usuario.padrao.equals(TipoUsuario.PADRAO)) {

					if (ValidaEmprestimoUsuarioPadrao(emprestimo, usuario)
							&& ValidaNumeroEmprestimosPorUsuario(emprestimosConcedidos, usuario.idUsuario)) {

						EmprestimoConcedido emprestimoConcedido = new EmprestimoConcedido(usuario.idUsuario,
								exemplar.idExemplar, dataParaSerConsideradaNaExpiracao);

						emprestimosConcedidos.add(emprestimoConcedido);
					}
					
				} else if (usuario.padrao.equals(TipoUsuario.PESQUISADOR)) {
					if (ValidaEmprestimoUsuarioPesquisador(emprestimo, usuario)) {

						EmprestimoConcedido emprestimoConcedido = new EmprestimoConcedido(usuario.idUsuario,
								exemplar.idExemplar, dataParaSerConsideradaNaExpiracao);

						emprestimosConcedidos.add(emprestimoConcedido);
					}
				}

			}
		}

		// O teste CENARIO 1 est� recebendo os valores esperados, mas na ordem errada,
		// verificar se o teste precisa ser modificado.
		return emprestimosConcedidos;
	}

	private static boolean ValidaEmprestimoUsuarioPadrao(DadosEmprestimo emprestimo, DadosUsuario usuario) {

		// Todo usuario padr�o precisa informar o tempo.
		// Somente exemplares to tipo Livre podem ser emprestados para o usu�rio Padr�o.
		if (usuario != null && usuario.padrao.equals(TipoUsuario.PADRAO)
				&& emprestimo.tipoExemplar.equals(TipoExemplar.LIVRE) 
				&& emprestimo.tempo <= 60) {

			return true;

		}
		return false;
	}

	private static boolean ValidaEmprestimoUsuarioPesquisador(DadosEmprestimo emprestimo, DadosUsuario usuario) {

		// Usuario Pesquisador n�o precisa informar tempo para devolu��o, mas o m�ximo �
		// 60 dias para todos.
		// Usuario pesquisador poder pegar TipoExemplar Livre e Restrito.

		if (usuario != null && usuario.padrao.equals(TipoUsuario.PESQUISADOR) 
				&& emprestimo.tempo <= 60) {

			// Caso mais na frente apare�am mais tipos de exemplares, continuar� funcionando
			// bem para o Pesquisador.
			if (emprestimo.tipoExemplar.equals(TipoExemplar.LIVRE)
					|| emprestimo.tipoExemplar.equals(TipoExemplar.RESTRITO)) {
				return true;
			}
		}
		return false;
	}

	private static boolean ValidaNumeroEmprestimosPorUsuario(Set<EmprestimoConcedido> emprestimos, int idUsuario) {
		// O usu�rio padr�o s� pode ter no m�ximo 5 emprestimos ao mesmo tempo.

		int somaEmprestimos = 0;
		for (EmprestimoConcedido emprestimo : emprestimos) {
			if (emprestimo.idUsuario == idUsuario) {
				somaEmprestimos += 1;
			}
		}

		if (somaEmprestimos >= 5) {
			return false;
		}

		return true;
	}

	public static DadosUsuario getUsuarioUsandoId(int idUsuario, Set<DadosUsuario> usuarios) {

		for (DadosUsuario dadosUsuario : usuarios) {
			if (idUsuario == dadosUsuario.idUsuario) {
				return dadosUsuario;
			}
		}
		return null;
	}

	public static DadosExemplar getExemplarUsandoIdLivro(int idLivro, Set<DadosExemplar> exemplares) {

		for (DadosExemplar exemplar : exemplares) {
			if (idLivro == exemplar.idLivro) {
				return exemplar;
			}
		}
		return null;
	}

}
