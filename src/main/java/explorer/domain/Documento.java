package explorer.domain;

import java.util.Date;

public class Documento {
	private String nome;
	private boolean diretorio;
	private String caminho;
	private Date ultimaModificacao;
	private Long tamanho;
	
	public Documento(String nome, boolean diretorio, String caminho, Date ultimaModificacao, Long tamanho) {
		this.nome = nome;
		this.diretorio = diretorio;
		this.caminho = caminho;
		this.ultimaModificacao = ultimaModificacao;
		this.tamanho = tamanho;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public boolean isDiretorio() {
		return diretorio;
	}
	public void setDiretorio(boolean diretorio) {
		this.diretorio = diretorio;
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}

	public Date getUltimaModificacao() {
		return ultimaModificacao;
	}

	public void setUltimaModificacao(Date ultimaModificacao) {
		this.ultimaModificacao = ultimaModificacao;
	}

	public Long getTamanho() {
		return tamanho;
	}

	public void setTamanho(Long tamanho) {
		this.tamanho = tamanho;
	}
	
	
}
