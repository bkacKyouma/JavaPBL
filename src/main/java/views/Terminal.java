package views;

public class Terminal {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_ITALIC = "\u001B[3m";
    public static final String ANSI_REVERSE = "\u001B[7m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void limpaTerminal(){
        try {
            String SO = System.getProperty("os.name");

            if (SO.contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else{
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }

        } catch (Exception e) {
            System.out.println("terminal não limpo, erro: " + e);
        }
    }

    public static void aplicaDelay(int x){
        try {
            Thread.sleep(x);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
