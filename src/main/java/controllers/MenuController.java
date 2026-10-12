package controllers;

import views.MenuView;

import persistence.PersistenciaJogo;

// Recebe as opções do MenuView e chama a funcionalidade correspondente.
public class MenuController {

    public static Boolean menuPrincipalController() {
        int option = 0;
        PersistenciaJogo persistencia = new PersistenciaJogo();

        while (option != 6) {
            option = MenuView.openMenu();

            while (option < 1 || option > 6) {
                MenuView.apresentaErro("A entrada fornecida é inválida.");
                option = MenuView.openMenu();
            }

            if (option == 1) {
                int atributoOption = MenuView.openAtributesMenu();
                while (atributoOption < 1 || atributoOption > 3) {
                    MenuView.apresentaErro("Temperamento inválido.");
                    atributoOption = MenuView.openAtributesMenu();
                }
                new JogoController().iniciarJogo(atributoOption);
            }

            // Continuar abre o slot cujo arquivo foi salvo por último.
            if (option == 2) {
                int slot = persistencia.slotMaisRecente();
                if (slot == 0) {
                    MenuView.apresentaErro("Nenhuma partida salva foi encontrada.");
                } else {
                    new JogoController().carregarJogo(slot);
                }
            }

            // Carregar permite escolher manualmente um dos três slots.
            if (option == 3) {
                int slot = MenuView.escolherSlot("CARREGAR PARTIDA");
                if (slot >= 1 && slot <= 3) {
                    new JogoController().carregarJogo(slot);
                } else if (slot != 0) {
                    MenuView.apresentaErro("Slot inválido.");
                }
            }

            if (option == 4) MenuView.Instrucoes();
            if (option == 5) MenuView.Creditos();
            if (option == 6) return true;
        }
        return false;
    }
}
