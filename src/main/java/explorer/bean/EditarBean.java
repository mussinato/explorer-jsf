package explorer.bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@ManagedBean(name="editarBean")
@ViewScoped
public class EditarBean {

	private String caminho;
	private String content;
	
	public EditarBean() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("id");
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)facesContext.getExternalContext().getRequest();
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
		  for(Cookie cookie: cookies){
		    if(cookie.getName().equals(id)){
		      caminho = cookie.getValue().replace("#", "\\");
		    }
		  }
		}
		
		FileInputStream input;
	    try {
	        input = new FileInputStream(new File(caminho));
	        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(input, decoder);
	        BufferedReader bufferedReader = new BufferedReader( reader );
	        StringBuilder sb = new StringBuilder();
	        String line = bufferedReader.readLine();
	        while( line != null ) {
	            sb.append(line).append("\n");
	            line = bufferedReader.readLine();
	        }
	        bufferedReader.close();
	        content = sb.toString();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        showMessage("Arquivo não encontrado", FacesMessage.SEVERITY_ERROR);
	    } catch( IOException e ) {
	        e.printStackTrace();
	        showMessage("Ocorreu um problema ao abrir o arquivo: "+e.getMessage(), FacesMessage.SEVERITY_ERROR);
	    }
	}
	
	public void salvar(){
		try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(caminho));
			writer.write(content);
			writer.close();
			showMessage("Arquivo alterado com sucesso.", FacesMessage.SEVERITY_INFO);
		} catch (IOException e) {
			showMessage("Ocorreu um problema ao salvar o arquivo: "+e.getMessage(), FacesMessage.SEVERITY_ERROR);
			e.printStackTrace();
		}	     
	    
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
	
	private void showMessage(String mensagem, Severity severity) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, 
        		new FacesMessage(severity,mensagem,null));
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
