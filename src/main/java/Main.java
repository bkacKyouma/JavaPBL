/*******************************************************************************
Autor: Bruno Carneiro e Raphael Henrique
Componente Curricular: Algoritmos II
Concluido em: 20/09/2026
Declaro que este código foi elaborado por mim de forma individual e não contém nenhum
trecho de código de outro colega ou de outro autor, tais como provindos de livros e
apostilas, e páginas ou documentos eletrônicos da Internet. Qualquer trecho de código
de outra autoria que não a minha está destacado com uma citação para o autor e a fonte
do código, e estou ciente que estes trechos não serão considerados para fins de avaliação.
******************************************************************************************/

import controllers.MenuController;

public class Main {
    public static void main(String[] args) {
        Boolean sair = false;

        while (!sair) {
            sair = MenuController.menuPrincipalController();
        }


    }

}