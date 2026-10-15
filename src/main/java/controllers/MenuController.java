package controllers;

import models.DadosGlobais;
import persistence.PersistenciaGlobal;
import views.Colecionaveis;
import views.MenuView;
import persistence.PersistenciaJogo;

// Recebe as opções do menu principal e aciona o fluxo correspondente.
public class MenuController {

    // Carrega o perfil global para aplicar a formatação salva na interface do menu.
    private static final PersistenciaGlobal persistenciaGlobal = new PersistenciaGlobal();
    private static DadosGlobais perfilAtual = persistenciaGlobal.carregar();

    private static final MenuView MenuScreen = new MenuView(
            perfilAtual.corPrimaria,
            perfilAtual.corSecundaria,
            perfilAtual.modo
    );

    public static Boolean menuPrincipalController() {
        int option = 0;
        PersistenciaJogo persistencia = new PersistenciaJogo();

        // Mantém o menu principal em execução até que a opção de saída seja selecionada.
        while (option != 7) {
            option = MenuScreen.openMenu();

            while (option < 1 || option > 7) {
                MenuScreen.apresentaErro("A entrada fornecida é inválida.");
                option = MenuScreen.openMenu();
            }

            // Solicita o temperamento inicial da protagonista e inicia uma nova partida.
            if (option == 1) {
                int atributoOption = MenuScreen.openAtributesMenu();
                while (atributoOption < 1 || atributoOption > 3) {
                    MenuScreen.apresentaErro("Temperamento inválido.");
                    atributoOption = MenuScreen.openAtributesMenu();
                }
                new JogoController(perfilAtual.corPrimaria, perfilAtual.corSecundaria, perfilAtual.modo).iniciarJogo(atributoOption);
            }

            // Permite escolher um dos quatro slots manualmente ou retomar o save mais recente.
// Permite escolher um dos quatro slots manualmente ou retomar o save mais recente.
            if (option == 2) {
                // Recupera os nomes dos arquivos
                while (option == 2){
                String[] savesDisponiveis = persistencia.listarNomesSaves();

                // Envia o array para a view pintar o menu
                int escolha = MenuScreen.escolherSlot("CARREGAR PARTIDA", savesDisponiveis, true);

                if (escolha == 5) {
                    int opcaoExclusao = MenuScreen.excluirSaveView();
                    if (opcaoExclusao > 0){
                        MenuScreen.confirmarExclusao(opcaoExclusao,savesDisponiveis[opcaoExclusao-1]);
                      //  persistencia.excluirSave(opcaoExclusao);
                    }
                    else if (opcaoExclusao != 0){
                        MenuScreen.apresentaErro("Slot inválido");
                    }
                } else if (escolha >= 1 && escolha <= 4) {
                    // Impede o jogador de tentar carregar um slot que está vazio
                    if (savesDisponiveis[escolha - 1].equals("Vazio")) {
                        MenuScreen.apresentaErro("Este slot está vazio.");
                    } else {
                        new JogoController(perfilAtual.corPrimaria, perfilAtual.corSecundaria, perfilAtual.modo).carregarJogo(escolha);
                        option = 0;
                    }
                } else if (escolha != 0) {
                    MenuScreen.apresentaErro("Slot inválido.");
                }
                else if (escolha == 0){
                    option = 0;
                }

            }}

            // Gerencia o submenu de colecionáveis, dividindo o acesso entre finais e conteúdos extras.
            if (option == 3) {
                boolean noColecionaveis = true;
                while (noColecionaveis) {
                    int optCol = MenuScreen.openColecionaveisMenu();

                    if (optCol == 1) {
                        perfilAtual = persistenciaGlobal.carregar();
                        MenuScreen.mostrarGaleriaFinais(perfilAtual.finaisDesbloqueados);
                    }
                    else if (optCol == 2) {
                        Colecionaveis colecionaveis = new Colecionaveis();
                        boolean noExtras = true;

                        // Recarrega o perfil e verifica se a chave do extra escolhido já foi desbloqueada.
                        while (noExtras) {
                            perfilAtual = persistenciaGlobal.carregar();
                            int optExtra = MenuScreen.openMenuExtras(perfilAtual.extrasDesbloqueados);

                            if (optExtra == 1) {
                                if (perfilAtual.extrasDesbloqueados != null && perfilAtual.extrasDesbloqueados.contains("FIM?")) {
                                    colecionaveis.exibirConquistaFim();
                                } else {
                                    MenuScreen.apresentaErro("Este conteúdo ainda está bloqueado.");
                                }
                            }
                            else if (optExtra == 2) {
                                if (perfilAtual.extrasDesbloqueados != null && perfilAtual.extrasDesbloqueados.contains("ISQUEIRO_TOIN")) {

                                } else {
                                    MenuScreen.apresentaErro("Este conteúdo ainda está bloqueado.");
                                }
                            }
                            else if (optExtra == 3) {
                                if (perfilAtual.extrasDesbloqueados != null && perfilAtual.extrasDesbloqueados.contains("BRINQUEDO_BRUNO")) {
                                    colecionaveis.exibirConquistaBruno();
                                } else {
                                    MenuScreen.apresentaErro("Este conteúdo ainda está bloqueado.");
                                }
                            }
                            else if (optExtra == 0) {
                                noExtras = false;
                            }
                            else {
                                MenuScreen.apresentaErro("Opção inválida.");
                            }
                        }
                    }
                    else if (optCol == 0) {
                        noColecionaveis = false;
                    }
                    else {
                        MenuScreen.apresentaErro("Opção inválida.");
                    }
                }
            }

            if (option == 4) MenuScreen.Instrucoes();

            // Altera as cores e os modos de formatação do terminal, salvando as preferências no perfil.
            if (option == 5) {
                boolean noConfig = true;
                while (noConfig) {
                    int optConfig = MenuScreen.openConfigMenu();
                    switch (optConfig) {
                        case 1: perfilAtual.corPrimaria = "\u001B[32m"; break;
                        case 2: perfilAtual.corPrimaria = "\u001B[36m"; break;
                        case 3: perfilAtual.corPrimaria = "\u001B[33m"; break;
                        case 4: perfilAtual.corPrimaria = "\u001B[37m"; break;
                        case 5:
                            if (perfilAtual.modo.contains("\u001B[7m")) {
                                perfilAtual.modo = perfilAtual.modo.replace("\u001B[7m", "");
                            } else {
                                perfilAtual.modo += "\u001B[7m";
                            }
                            break;
                        case 6:
                            if (perfilAtual.modo.contains("\u001B[1m")) {
                                perfilAtual.modo = perfilAtual.modo.replace("\u001B[1m", "");
                            } else {
                                perfilAtual.modo += "\u001B[1m";
                            }
                            break;
                        case 7:
                            if (perfilAtual.modo.contains("\u001B[3m")) {
                                perfilAtual.modo = perfilAtual.modo.replace("\u001B[3m", "");
                            } else {
                                perfilAtual.modo += "\u001B[3m";
                            }
                            break;
                        case 0:
                            noConfig = false;
                            break;
                        default:
                            MenuScreen.apresentaErro("A entrada fornecida é inválida.");
                            break;
                    }

                    MenuScreen.setCorPrimaria(perfilAtual.corPrimaria);
                    MenuScreen.setCorSecundaria(perfilAtual.corSecundaria);
                    MenuScreen.setModo(perfilAtual.modo);

                    try {
                        persistenciaGlobal.salvar(perfilAtual);
                    } catch (Exception e) {
                        System.out.println("Erro ao salvar perfil");
                    }
                }
            }

            if (option == 6) MenuScreen.Creditos();

            if (option == 7) return true;
        }
        return false;
    }
}