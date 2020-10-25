# Agenda-Telefonica
#### Aplicativo feito em Kotlin com CRUD SQLite
### Descrição do Projeto
> Este projeto tem como objetivo mostrar o funcionamento de um simples aplicativo feito com a linguagem Kotlin, e com funcionalidades básicas de um SGDB (Sistema Gerenciador de Banco de Dados) usando o banco de dados SQLite

### Instalação
EasyPermissions, ItextG e MaskEdiText é instalado adicionando a seguinte dependência `build.gradle` para o arquivo: 
```groovy
dependencies {
    
    //Implementação das bibliotecas usadas neste projeto
    implementation 'com.github.santalu:mask-edittext:1.0.2'
    implementation 'com.itextpdf:itextg:5.5.10'
    implementation 'pub.devrel:easypermissions:3.0.0'
   
}
```
### Telas do aplicativo 🚧
A figura abaixo mostra as principais telas do aplicativo e as que estão em construção...🚧
| ![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/agenda_vazia.png)  |![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/cadastro_pessoas.png)   |
|:---:|:---:|
| **TELA INICIAL DO APLICATIVO** | **TELA COM A ÁREA DE CADASTRO DA PESSOA**  |
|![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/listar_pessoas.png) |![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/atualizar_pessoas.png)  |
| **TELA COM A LISTA DE PESSOAS APÓS O CADASTRO**  |  **TELA COM A ÁREA DE ATUALIZAÇÃO DA PESSOA** |
|![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/permissao2.png) |![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/intent_pdf.png)  |
| **TELA DE PERMISSÃO DE ESCRITA DE DADOS ANTES DE GERAR O RELATÓRIO**  |  **TELA DE ESCOLHA DE APLICATIVO COMPATÍVEL COM O FORMATO PDF** |
|![](/app/src/main/java/chellotech/br/agendatelefonica/screenshots/relatorio_pessoas.png) 
| **RELATÓRIO GERADO**  


### Status do Projeto

- [x] Tela com a lista das pessoas cadastradas
- [x] Tela com as informações da pessoa para serem preenchidas
- [ ] Relatório com a lista de todas as pessoas cadastradas
- [ ] Ordenação da lista de pessoas cadastradas

