logado = sessionStorage.getItem('logado');
if (logado){
	
} else {	
	senha = prompt("Senha:", "");
	
	if (senha == "2020") {
		sessionStorage.setItem('logado',true);
	} else {
		window.location = "nao-autorizado.html"
	}
}