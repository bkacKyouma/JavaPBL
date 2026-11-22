# A Noite Que Não Se Apaga

Jogo narrativo interativo de investigação em Java (UEFS - EXA863 MI Programação).
Katarina é interrogada pelo detetive Fauan depois de um incêndio; suas escolhas mudam
atributos, relacionamentos, pistas, cenas e o final.

## Requisitos
- JDK 21
- Maven 3.9 ou superior (as dependências, inclusive o JavaFX 21, são baixadas pelo Maven)

## Compilar e executar

Interface gráfica (JavaFX):

    mvn clean javafx:run

Versão de terminal (primeiras fases):

    mvn compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=Main

Testes de unidade:

    mvn test

Empacotar (gera o .jar em `target/`):

    mvn clean package

> Execute sempre a partir da pasta do projeto: os saves e o perfil ficam na pasta `saves/`,
> criada em relação ao diretório de execução.

## Como jogar
Menu inicial: Nova partida, Continuar (save mais recente), Carregar partida, Galeria,
Instruções, Configurações, Créditos e Sair. Durante a partida use os botões do topo:
Status, Salvar, Reiniciar e Menu. O jogo salva automaticamente no slot 1 a cada escolha.

## Arquitetura (MVC)
- `models/`: regras e dados do jogo (cenas, escolhas, protagonista, estado, itens da galeria).
- `controllers/`: `JogoController` (partida), `ConfigController` (preferências),
  `GaleriaController` (conquistas), `MenuController` (terminal).
- `views/`: terminal (`views/`) e JavaFX (`views/fx/`). As telas só exibem e repassam
  ações ao controller; nenhuma regra de jogo, save ou escolha fica nelas.
- `persistence/`: leitura e escrita dos saves e do perfil em JSON.

## Padrões GoF
| Padrão | Onde | Por que |
|---|---|---|
| Singleton | `ConfigController.getInstance()` | Só pode existir uma cópia das preferências em memória, para as telas nunca divergirem entre si nem do perfil.json. |
| Observer | `ConfigObserver` + `ConfigController` (Subject) + `Navegador` (Observer) | Ao mudar cor ou estilo, a tela atual se atualiza na hora sem o controller conhecer a View. |
| Strategy | `views/fx/feedback/` (`EstrategiaFeedback` e implementações) | Cada tipo de mensagem (aumento, queda, informação) tem seu próprio estilo e formatação; novos tipos entram sem alterar `Componentes`. |

## Testes
Os testes ficam em `src/test/java/Tests/` e cobrem models, persistência, controllers,
a API usada pela interface (sem abrir janelas), galeria, preferências (Singleton e Observer)
e as estratégias de feedback.
