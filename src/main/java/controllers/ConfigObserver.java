package controllers;

// Padrão Observer: quem quiser reagir a mudanças de preferência implementa esta interface.
public interface ConfigObserver {

    void aoMudarConfiguracao();
}
