package controllers;

import models.DadosGlobais;
import persistence.PersistenciaGlobal;
import views.Colecionaveis;
import views.MenuView;
import persistence.PersistenciaJogo;

public class MenuController {

    // Carrega o perfil do jogador (cores e conquistas)
    private static final PersistenciaGlobal persistenciaGlobal = new PersistenciaGlobal();
    private static DadosGlobais perfilAtual = persistenciaGlobal.carregar();

    // Instancia a view com o setup salvo
    private static final MenuView MenuScreen = new MenuView(
            perfilAtual.corPrimaria,
            perfilAtual.corSecundaria,
            perfilAtual.modo
    );

    public static Boolean menuPrincipalController() {
        int option = 0;
        PersistenciaJogo persistencia = new PersistenciaJogo();

        // O loop principal agora vai até 7 (Sair)
        while (option != 7) {
            option = MenuScreen.openMenu();

            while (option < 1 || option > 7) {
                MenuScreen.apresentaErro("A entrada fornecida é inválida.");
                option = MenuScreen.openMenu();
            }

            // NOVA PARTIDA
            if (option == 1) {
                int atributoOption = MenuScreen.openAtributesMenu();
                while (atributoOption < 1 || atributoOption > 3) {
                    MenuScreen.apresentaErro("Temperamento inválido.");
                    atributoOption = MenuScreen.openAtributesMenu();
                }
                new JogoController(perfilAtual.corPrimaria, perfilAtual.corSecundaria, perfilAtual.modo).iniciarJogo(atributoOption);
            }

            // CARREGAR / CONTINUAR (Unificados)
            if (option == 2) {
                int slotMaisRecente = persistencia.slotMaisRecente();
                if (slotMaisRecente == 0) {
                    MenuScreen.apresentaErro("Nenhuma partida salva foi encontrada.");
                } else {
                    int escolha = MenuScreen.escolherSlot("1 a 4 para Slots | 5 para Continuar Último Jogo");
                    if (escolha == 5) {
                        new JogoController(perfilAtual.corPrimaria, perfilAtual.corSecundaria, perfilAtual.modo).carregarJogo(slotMaisRecente);
                    } else if (escolha >= 1 && escolha <= 4) {
                        new JogoController(perfilAtual.corPrimaria, perfilAtual.corSecundaria, perfilAtual.modo).carregarJogo(escolha);
                    } else if (escolha != 0) {
                        MenuScreen.apresentaErro("Slot inválido.");
                    }
                }
            }

            // COLECIONÁVEIS (Submenu para Finais e Extras)
            if (option == 3) {
                boolean noColecionaveis = true;
                while (noColecionaveis) {
                    int optCol = MenuScreen.openColecionaveisMenu();

                    if (optCol == 1) { // 1 - Galeria de Finais
                        perfilAtual = persistenciaGlobal.carregar();
                        MenuScreen.mostrarGaleriaFinais(perfilAtual.finaisDesbloqueados);
                    }
                    else if (optCol == 2) {
                        // 2 - Conteúdos Extras
                        Colecionaveis colecionaveis = new Colecionaveis();
                        boolean noExtras = true;
                        while (noExtras) {
                            perfilAtual = persistenciaGlobal.carregar();
                            int optExtra = MenuScreen.openMenuExtras(perfilAtual.extrasDesbloqueados);

                            if (optExtra == 1) {
                                // O JogoController registra "FIM?" ao alcançar qualquer final
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
                                noExtras = false; // Volta ao menu de colecionáveis
                            }
                            else {
                                MenuScreen.apresentaErro("Opção inválida.");
                            }
                        }
                    }
                    else if (optCol == 0) { // Voltar ao Menu Principal
                        noColecionaveis = false;
                    }
                    else {
                        MenuScreen.apresentaErro("Opção inválida.");
                    }
                }
            }

            // INSTRUÇÕES
            if (option == 4) MenuScreen.Instrucoes();

            // PREFERÊNCIAS (Configurações)
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

            // CRÉDITOS
            if (option == 6) MenuScreen.Creditos();

            // SAIR
            if (option == 7) return true;
        }
        return false;
    }
}