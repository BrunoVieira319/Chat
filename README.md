# chat-with-sockets
Exercício de sockets para disciplina Redes de Computadores

<h2>Relatório</h2>

Ao iniciar a classe ServerApp, ela instacia um Server.

No construtor da classe Server, ele instancia duas listas, uma para armazenar todos clients conectados e outra para armazenar as suas saidas de dados. Também no construtor ele chama o método <code>run()</code> que é responsável por iniciar o <code>Server</code> de fato.

No método <code>run()</code> é  instanciado um <code>ServerSocket</code> na porta 12345 e chamado o método <code>waitConnections()</code> que é responsável por administrar as conexões feitas com o <code>Server</code>.

O método <code>waitConnections()</code> contém um loop infinito. Dentro deste loop ele instancia um objeto do tipo <code>Connection</code> que recebe em seu construtor um <code>Socket</code>, este que é passado pelo método <code>accept()</code> do <code>ServerSocket</code> toda vez que um client se conectar. A classe <code>Connection</code> é responsável por criar uma nova Thread para aquele client e também armazenar o seu Socket e Nickname. Em seguida esta <code>Connection</code> e sua saída de dados é adicionada dentro das respectivas listas da classe <code>Server</code>. E por último, o método <code>listenToClient()</code> é chamado passando a <code>Connection</code>.

O método <code>listenToClient()</code> é responsável por capturar as mensagens do client através da classe <code>Scanner</code> e repassá-las para o método <code>shareMessages()</code>. A primeira coisa feita é iniciar uma nova Thread para este método, depois ele captura o nickName do usuário, pois este é o primeiro dado enviado do client e depois notifica todos os usuários que um novo usuário se conectou atraves do método <code>notifyUsersThatHasNewUserConnected()</code>. Em seguida inicia um laço até que não tenha mais linhas de texto a serem enviadas pelo usuário, elas são verificadas pelo método <code>checkMessage()</code> para ver se elas são comandos ao invés de mensagens de texto normais, em seguida repassadas para todos usuários pelo método <code>shareMessage()</code>.

O método <code>shareMessage()</code> é simples, ele recebe uma String e envia para todos usuários fazendo um forEach em cima da lista de <code>outputsClients</code>.

Para abrir uma conexão com o Server basta iniciar a classe <code>App</code>, ela instancia um <code>User</code> e pede pro usuário o seu nickName através da classe Scanner.

No construtor da classe <code>User</code> ela recebe uma String, o nickName, verifica se é válido através do método <code>setNickname()</code>, depois se conecta ao Server com o método <code>connectToServer()</code>, em seguida chama os métodos <code>sendMessages()</code> e <code>receiveMessages()</code> responsáveis pelo serviço de chat do usuário, estes métodos iniciam uma nova Thread dentro deles para que o serviço seja em tempo real.
