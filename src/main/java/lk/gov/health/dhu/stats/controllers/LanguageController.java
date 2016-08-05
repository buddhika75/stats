/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.gov.health.dhu.stats.controllers;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author User
 */
@Named
@SessionScoped
public class LanguageController implements Serializable {

    String language;
    @Inject
    PersonController personController;

    /**
     * Creates a new instance of LanguageController
     */
    public LanguageController() {
    }

    public String getLanguage() {
        if (language==null||language.equals("")) {
            language = "en";
        }
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private void makeLanguage(String lan){
        language = lan;
        if(personController.getLoggedUser()!=null){
            personController.getLoggedUser().setUserLanguage(lan);
            personController.updateLoggedUser();
        }
    }
    
    public void makeLanguageSinhala() {
        makeLanguage("si");
    }

    public void makeLanguageTamil() {
        makeLanguage("ta");
    }
    
     public void makeLanguageEnglish() {
         makeLanguage("en");
    }

}
