/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.gov.health.dhu.stats.controllers;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;
import lk.gov.health.dhu.stats.enums.CategoryType;
import lk.gov.health.dhu.stats.enums.DataCollectionFrequency;
import lk.gov.health.dhu.stats.enums.InstitutionType;
import lk.gov.health.dhu.stats.enums.LocationType;
import lk.gov.health.dhu.stats.enums.PersonType;
import lk.gov.health.dhu.stats.enums.RecordType;
import lk.gov.health.dhu.stats.enums.UnitType;
import lk.gov.health.dhu.stats.enums.UserRole;

/**
 *
 * @author User
 */
@Named
@ApplicationScoped
public class EnumController {

    /**
     * Creates a new instance of EnumController
     */
    public EnumController() {
    }
    
    public UserRole[] getUserRoles(){
        return UserRole.values();
    }
    
    public String[] getLanguages(){
        return new String[]{"en","si","ta"};
    }
    
    public CategoryType[] getCategoryTypes(){
        return CategoryType.values();
    }
    
    public DataCollectionFrequency[] getDataCollectionFrequencys(){
        return DataCollectionFrequency.values();
    }
    
    public InstitutionType[] getInstitutionTypes(){
        return InstitutionType.values();
    }
    
    public LocationType[] getLocationTypes(){
        return LocationType.values();
    }
    
    public PersonType[] getPersonTypes(){
        return PersonType.values();
    }
    
    public RecordType[] getRecordTypes(){
        return RecordType.values();
    }
    
    public UnitType[] getUnitTypes(){
        return UnitType.values();
    }
    
    public UserRole[] getUserRole(){
        return UserRole.values();
    }
    
    
    
    
    
}
