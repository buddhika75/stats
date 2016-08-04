package lk.gov.health.dhu.stats.controllers;

import lk.gov.health.dhu.stats.entity.Person;
import lk.gov.health.dhu.stats.controllers.util.JsfUtil;
import lk.gov.health.dhu.stats.controllers.util.PaginationHelper;
import lk.gov.health.dhu.stats.sessionBeans.PersonFacade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.xml.rpc.Call;
import lk.gov.health.dhu.stats.entity.Institution;
import lk.gov.health.dhu.stats.enums.UserRole;

@ManagedBean(name = "personController")
@SessionScoped
public class PersonController implements Serializable {

    private Person current;
    private DataModel items = null;
    @EJB
    private lk.gov.health.dhu.stats.sessionBeans.PersonFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private String userName;
    private String password;
    private Person loggedUser;
    private Institution loggedInstitution;
    private boolean logged;
    private UserRole loggedUserRole;

    public void logOut() {
        loggedUser = null;
        logged = false;
        loggedInstitution=null;
        loggedUserRole = null;
    }

    public void logIn() {
        logged=false;
        loggedInstitution = null;
        loggedUser =null;
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
        if(p.getPassword().equals(password)){
            logged=true;
            loggedInstitution = p.getInstitution();
            loggedUser = p;
            loggedUserRole = p.getUserRole();
        }
        
        
    }

    public PersonController() {
    }

    public Person getSelected() {
        if (current == null) {
            current = new Person();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PersonFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Person) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Person();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Person) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Person) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
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

    @FacesConverter(forClass = Person.class)
    public static class PersonControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonController controller = (PersonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personController");
            return controller.ejbFacade.find(getKey(value));
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
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Person.class.getName());
            }
        }

    }

}
