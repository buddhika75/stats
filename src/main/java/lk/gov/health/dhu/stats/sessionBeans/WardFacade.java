/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.gov.health.dhu.stats.sessionBeans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lk.gov.health.dhu.stats.entity.Ward;

/**
 *
 * @author User
 */
@Stateless
public class WardFacade extends AbstractFacade<Ward> {

    @PersistenceContext(unitName = "pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public WardFacade() {
        super(Ward.class);
    }
    
}
