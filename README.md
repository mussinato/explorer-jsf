# explorer-jsf [![Build Status](https://travis-ci.org/mussinato/explorer-jsf.svg?branch=master)](https://travis-ci.org/mussinato/explorer-jsf)
O explorer-jsf é um gerenciador de arquivos baseado na web. 

## Características
* árvore de diretórios dinâmicos com carregamento sob demanda de diretórios
* navegar em diretórios e arquivos no servidor
* editar e excluir arquivos
* upload e download de arquivos
* criar novos arquivos e diretórios

## Implementações Futuras
* renomear arquivos e diretórios
* mover arquivos e diretórios
* compactar e extrair arquivos e diretórios (zip, tar, rar)
* mudar as permissões dos arquivos (chmod)
* segurança com login e senha

## Instalação
1. Instale o [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html) ou superior
2. Instale um servidor de aplicação (ex. [Tomcat](http://tomcat.apache.org/))
3. Clone o projeto em um diretório do seu computador
4. Ultilize o maven para compilar o projeto e gerar o pacote: `mvn clean package`
5. Copie o arquivo explorer.war gerado para o servidor de aplicação
6. Inicie o servidor de aplicação e acesse a aplicação pelo navegador

## Autor
Renato Sacoman Mussinato (mussinato@gmail.com)
