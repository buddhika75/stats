package lk.gov.health.dhu.stats.controllers;

import lk.gov.health.dhu.stats.entity.Person;
import lk.gov.health.dhu.stats.controllers.util.JsfUtil;
import lk.gov.health.dhu.stats.controllers.util.JsfUtil.PersistAction;
import lk.gov.health.dhu.stats.sessionBeans.PersonFacade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import lk.gov.health.dhu.stats.entity.Institution;
import lk.gov.health.dhu.stats.enums.UserRole;

@Named("personController")
@SessionScoped
public class PersonController implements Serializable {

    @Inject
    LanguageController languageController;
    @EJB
    private lk.gov.health.dhu.stats.sessionBeans.PersonFacade ejbFacade;
    private List<Person> items = null;
    private Person selected;
    private String userName;
    private String password;
    private Person loggedUser;
    private Institution loggedInstitution;
    private boolean logged;
    private UserRole loggedUserRole;
    List<Person> alreadyRegisteredPersons = null;

    public String registerNewUserStart() {
        return "/edits/person/register_1";
    }

    public String registerNewUserInsSelected() {
        if (loggedInstitution == null) {
            JsfUtil.addErrorMessage("Please select an institution to proceed");
            return "";
        }
        
        String j;
        Map m = new HashMap();
        j = "select p "
                + " from Person p "
                + " where p.institution=:ins "
                + " and p.userRole=:ur";
        m.put("ins", loggedInstitution);
        m.put("ur", UserRole.InsAdmin);
        if (alreadyRegisteredPersons == null || alreadyRegisteredPersons.isEmpty()) {
            return "/edits/person/register_2";
        }else{
            return "/edits/person/register_3";
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Person> getAlreadyRegisteredPersons() {
        return alreadyRegisteredPersons;
    }

    public void setAlreadyRegisteredPersons(List<Person> alreadyRegisteredPersons) {
        this.alreadyRegisteredPersons = alreadyRegisteredPersons;
    }

    
    
    public void logOut() {
        loggedUser = null;
        logged = false;
        loggedInstitution = null;
        loggedUserRole = null;
    }

    public void logIn() {
        logged = false;
        loggedInstitution = null;
        loggedUser = null;
        if (userName == null || userName.trim().equals("")) {
            JsfUtil.addErrorMessage("User Name?");
            return;
        }
        if (password == null || password.trim().equals("")) {
            JsfUtil.addErrorMessage("Password?");
            return;
        }
        String j;
        j = "select p from Person p "
                + " where p.active=:pac "
                + " and upper(p.userName)=:un ";
        Map m = new HashMap();
        m.put("un", userName.toUpperCase());
        m.put("pac", true);

        Person p = getFacade().findFirstBySQL(j, m);
        if (p == null) {
            JsfUtil.addErrorMessage("No such user?");
            return;
        }
        if (p.getPassword().equals(password)) {
            logged = true;
            loggedInstitution = p.getInstitution();
            loggedUser = p;
            loggedUserRole = p.getUserRole();
            languageController.setLanguage(p.getUserLanguage());
        }

    }

    public PersonController() {
    }

    public Person getSelected() {
        return selected;
    }

    public void setSelected(Person selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonFacade getFacade() {
        return ejbFacade;
    }

    public Person prepareCreate() {
        selected = new Person();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PersonUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PersonDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Person> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Person getPerson(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Person> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Person> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Institution getLoggedInstitution() {
        return loggedInstitution;
    }

    public void setLoggedInstitution(Institution loggedInstitution) {
        this.loggedInstitution = loggedInstitution;
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isLoggable() {
        if (logged) {
            return true;
        } else if (getFacade().count() < 3) {
            return true;
        }
        return false;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public UserRole getLoggedUserRole() {
        return loggedUserRole;
    }

    public void setLoggedUserRole(UserRole loggedUserRole) {
        this.loggedUserRole = loggedUserRole;
    }

    public Person getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Person loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void updateLoggedUser() {
        if (loggedUser != null) {
            getFacade().edit(loggedUser);
        }
    }

    @FacesConverter(forClass = Person.class)
    public static class PersonControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonController controller = (PersonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personController");
            return controller.getPerson(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Person) {
                Person o = (Person) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Person.class.getName()});
                return null;
            }
        }

    }

}
