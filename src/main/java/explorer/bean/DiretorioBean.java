package explorer.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import explorer.domain.Documento;

@ManagedBean(name = "diretorioBean")
@ViewScoped
public class DiretorioBean {

	private TreeNode root;
	private Documento documentoSelecionado;
	private List<Documento> documentos = new ArrayList<>();
	private TreeNode selectedNode;
	private String diretorioAtual;
	private StreamedContent arquivoParaDownload;
	private String nomeNovaPasta;
	private String nomeNovoArquivo;

	public DiretorioBean() {
		listar();
	}

	public void listar() {
		root = new DefaultTreeNode(new Documento("/", true, "/", null, null), null);
		listarDiretorios(root);
		documentos = buscarDocumentos(diretorioAtual, false);
	}

	private void listarDiretorios(TreeNode node) {
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			if (node.getChildren().get(0) instanceof DefaultTreeNode) {
				DefaultTreeNode no = (DefaultTreeNode) node.getChildren().get(0);
				Documento doc = (Documento) no.getData();
				if (doc.getNome().equals("Carregando")) {
					node.getChildren().remove(0);
				}
			}
		}

		Documento documento = (Documento) node.getData();
		List<Documento> lista = buscarDocumentos(documento.getCaminho(), true);
		for (Documento doc : lista) {
			TreeNode n = new DefaultTreeNode(doc, node);
			new DefaultTreeNode(new Documento("Carregando", false, null, null, null), n);
		}

	}

	private List<Documento> buscarDocumentos(String diretorio, boolean somenteDiretorio) {
		diretorioAtual = diretorio;
		List<Documento> lista = new ArrayList<>();
		File file = new File(diretorio);
		File afile[] = file.listFiles();
		if (afile != null && afile.length > 0) {
			int i = 0;
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				Long tamanho = arquivos.length() / 1024; // em kb
				if (somenteDiretorio) {
					if (arquivos.isDirectory()) {
						lista.add(new Documento(arquivos.getName(), arquivos.isDirectory(), arquivos.getAbsolutePath(),
								new Date(arquivos.lastModified()), tamanho));
					}
				} else {
					lista.add(new Documento(arquivos.getName(), arquivos.isDirectory(), arquivos.getAbsolutePath(),
							new Date(arquivos.lastModified()), tamanho));
				}
			}
		}
		return lista;
	}

	public void voltar() {
		int pos = diretorioAtual.lastIndexOf(File.separator);
		documentos = buscarDocumentos(diretorioAtual.substring(0, pos), false);
	}

	public void novaPasta() {
		if (nomeNovaPasta != null && !nomeNovaPasta.equals("")) {
			File file = new File(diretorioAtual + File.separator + nomeNovaPasta);
			if (file.mkdir()) {
				documentos = buscarDocumentos(diretorioAtual, false);
				showMessage("Diretório " + nomeNovaPasta + " criado.", FacesMessage.SEVERITY_INFO);
			} else {
				showMessage("Diretório " + nomeNovaPasta + " criado.", FacesMessage.SEVERITY_ERROR);
			}
		}
	}

	public void novoArquivo() {
		if (nomeNovoArquivo != null && !nomeNovoArquivo.equals("")) {
			File file = new File(diretorioAtual + File.separator + nomeNovoArquivo);
			try {
				if (file.createNewFile()) {
					documentos = buscarDocumentos(diretorioAtual, false);
					showMessage("Arquivo " + nomeNovoArquivo + " criado.", FacesMessage.SEVERITY_INFO);
				} else {
					showMessage("Ocorreu um problema ao criar o arquivo " + nomeNovoArquivo,
							FacesMessage.SEVERITY_ERROR);
				}
			} catch (IOException e) {
				e.printStackTrace();
				showMessage("Ocorreu um problema ao criar o arquivo " + nomeNovoArquivo + ": " + e.getMessage(),
						FacesMessage.SEVERITY_ERROR);
			}
		}
	}

	public void deletar() {
		if (documentoSelecionado != null) {
			File file = new File(documentoSelecionado.getCaminho());
			if (file.delete()) {
				documentos = buscarDocumentos(diretorioAtual, false);
				if (documentoSelecionado.isDiretorio()) {
					showMessage("Diretório " + documentoSelecionado.getNome() + " excluído.",
							FacesMessage.SEVERITY_INFO);
				} else {
					showMessage("Arquivo " + documentoSelecionado.getNome() + " excluído.", FacesMessage.SEVERITY_INFO);
				}
			} else {
				showMessage("Ocorreu um problema ao excluír " + documentoSelecionado.getNome(),
						FacesMessage.SEVERITY_ERROR);
			}
		}
	}
	
	public void irParaDiretorio(ActionEvent events) {
		MenuActionEvent j = (MenuActionEvent) events;
		DefaultMenuItem item = (DefaultMenuItem) j.getMenuItem();
		Map mapa = item.getParams();
		List param = (List) mapa.get("caminho");
		documentos = buscarDocumentos((String)param.get(0), false);
	}

	public MenuModel getBreadCrumbModel() {
		MenuModel model = new DefaultMenuModel();
		addItemToMenuModel(model, 0, "root", "/", false);
		String[] caminhos = diretorioAtual.split(File.separator);
		String breadcrump = "";
		int count = 0;
		for (String s : caminhos) {
			breadcrump += s + File.separator;
			if (s != null && !s.equals("")) {
				count ++;
				addItemToMenuModel(model, count, s, breadcrump, false);
			}
		}
		return model;
	}

	private void addItemToMenuModel(MenuModel model, int index, String value, String caminho, boolean disabled) {
		DefaultMenuItem element = new DefaultMenuItem();
		element.setValue(value);
		element.setId(String.valueOf(index));
		element.setCommand("#{diretorioBean.irParaDiretorio}");
		element.setDisabled(disabled);
		element.setParam("caminho", caminho);
		element.setUpdate("lista breadcrumb");
		model.addElement(element);
	}

	public void atualizar() {
		documentos = buscarDocumentos(diretorioAtual, false);
		showMessage("Diretório atualizado.", FacesMessage.SEVERITY_INFO);
	}

	public void editar() throws NoSuchAlgorithmException {
		String time = "" + new Date().getTime();
		String hashMd5 = toMd5(time);

		FacesContext context = FacesContext.getCurrentInstance();
		Cookie cookie = new Cookie(hashMd5, documentoSelecionado.getCaminho());
		cookie.setMaxAge(-1);
		((HttpServletResponse) context.getExternalContext().getResponse()).addCookie(cookie);

		PrimeFaces.current().executeScript("window.open('editar.xhtml?id=" + hashMd5 + "')");
	}

	public void onNodeExpand(NodeExpandEvent event) {
		TreeNode node = event.getTreeNode();
		listarDiretorios(node);
	}

	public void onNodeSelect(NodeSelectEvent event) {
		TreeNode node = event.getTreeNode();
		Documento doc = (Documento) node.getData();
		documentos = buscarDocumentos(doc.getCaminho(), false);
		documentoSelecionado = doc;
	}

	public void onRowDblClckSelect(final SelectEvent event) {
		Documento obj = (Documento) event.getObject();
		if (obj.isDiretorio()) {
			documentos = buscarDocumentos(obj.getCaminho(), false);
		}
	}

	private void showMessage(String mensagem, Severity severity) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(severity, mensagem, null));
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public Documento getDocumentoSelecionado() {
		return documentoSelecionado;
	}

	public void setDocumentoSelecionado(Documento documentoSelecionado) {
		this.documentoSelecionado = documentoSelecionado;
	}

	public StreamedContent getArquivoParaDownload() {
		if (documentoSelecionado != null && !documentoSelecionado.isDiretorio()) {
			try {
				InputStream is = new FileInputStream(new File(documentoSelecionado.getCaminho()));
				arquivoParaDownload = new DefaultStreamedContent(is, null, documentoSelecionado.getNome());
				return arquivoParaDownload;
			} catch (FileNotFoundException e) {

			}
		}
		return null;
	}

	public void setArquivoParaDownload(StreamedContent arquivoParaDownload) {
		this.arquivoParaDownload = arquivoParaDownload;
	}

	public String getNomeNovaPasta() {
		return nomeNovaPasta;
	}

	public void setNomeNovaPasta(String nomeNovaPasta) {
		this.nomeNovaPasta = nomeNovaPasta;
	}

	public String getNomeNovoArquivo() {
		return nomeNovoArquivo;
	}

	public void setNomeNovoArquivo(String nomeNovoArquivo) {
		this.nomeNovoArquivo = nomeNovoArquivo;
	}

	private String toMd5(String texto) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(texto.getBytes(), 0, texto.length());
		return new BigInteger(1, m.digest()).toString(16);
	}
}
