/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.gov.health.dhu.stats.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author User
 */
@ManagedBean
@SessionScoped
public class LanguageController {

    String language;

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

    public void makeLanguageSinhala() {
        language = "si";
    }

    public void makeLanguageTamil() {
        language = "ta";
    }
    
     public void makeLanguageEnglish() {
        language = "en";
    }

}
